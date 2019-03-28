package com.elepy.hibernate;

import com.elepy.annotations.Searchable;
import com.elepy.dao.Crud;
import com.elepy.dao.FilterQuery;
import com.elepy.dao.Page;
import com.elepy.dao.SearchQuery;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import javax.persistence.Column;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernateDao<T> implements Crud<T> {
    private static final Logger logger = LoggerFactory.getLogger(HibernateDao.class);
    private final SessionFactory sessionFactory;
    private final Class<T> aClass;
    private final ObjectMapper objectMapper;

    public HibernateDao(SessionFactory sessionFactory, ObjectMapper objectMapper, Class<T> aClass) {
        this.sessionFactory = sessionFactory;
        this.aClass = aClass;
        this.objectMapper = objectMapper;
    }


    @Override
    public Page<T> search(SearchQuery searchQuery) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(aClass);

            final Root<T> root = criteriaQuery.from(aClass);
            final long count;
            if (StringUtils.isEmpty(searchQuery.getQuery())) {
                criteriaQuery.select(root);
                count = count();
            } else {
                criteriaQuery.select(root);

                final List<Field> searchableFields = com.elepy.utils.ClassUtils.searchForFieldsWithAnnotation(aClass, Searchable.class);

                final List<Predicate> predicatelist = new ArrayList<>();
                for (Field searchableField : searchableFields) {
                    final Predicate like = cb.like(root.get(searchableField.getName()), "%" + searchQuery.getQuery() + "%");
                    predicatelist.add(like);

                }
                criteriaQuery.where(cb.or(predicatelist.toArray(new Predicate[0])));
                count = count(searchQuery.getQuery());

            }

            Query<T> query = session.createQuery(criteriaQuery);


            return toPage(query, searchQuery, count);
        }
    }


    private Page<T> toPage(Query<T> query, SearchQuery pageSearch, long amountOfResultsWithThatQuery) {
        return toPage(query, pageSearch.getPageSize(), pageSearch.getPageNumber(), amountOfResultsWithThatQuery);
    }

    private Page<T> toPage(Query<T> query, int pageSize, long pageNumber, long amountOfResultsWithThatQuery) {
        final List<T> values = query.setMaxResults(pageSize).setFirstResult(((int) pageNumber - 1) * pageSize).list();

        if (amountOfResultsWithThatQuery == -1) {
            amountOfResultsWithThatQuery = values.size();
        }
        loadLazyCollections(values);

        final long remainder = amountOfResultsWithThatQuery % pageSize;
        long amountOfPages = amountOfResultsWithThatQuery / pageNumber;
        if (remainder > 0) amountOfPages++;

        return new Page<>(pageNumber, amountOfPages, values);
    }

    @Override
    public Optional<T> getById(Object id) {
        try (Session session = sessionFactory.openSession()) {

            final T t = session.get(aClass, (Serializable) id);

            loadLazyCollections(t);
            return Optional.ofNullable(t);
        }
    }

    @Override
    public List<T> searchInField(Field field, String qry) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(aClass);

            final Root<T> root = criteriaQuery.from(aClass);

            criteriaQuery.select(root).where(cb.like(root.get(getJPAFieldName(field)), qry));

            Query<T> query = session.createQuery(criteriaQuery);
            final List<T> resultList = query.list();
            loadLazyCollections(resultList);
            return resultList;
        }
    }

    @Override
    public void update(T item) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            session.update(item);
            transaction.commit();
        }
    }

    private void create(Session session, T item) {
        session.save(item);
    }

    @Override
    public void create(T item) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            create(session, item);
            transaction.commit();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ElepyException(e.getMessage());
        }
    }

    @Override
    public void create(Iterable<T> items) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            for (T item : items) {
                create(session, item);
            }
            transaction.commit();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ElepyException(e.getMessage());
        }
    }

    @Override
    public long count(String q) {

        try (Session session = sessionFactory.openSession()) {
            if (StringUtils.isEmpty(q)) {
                return count();
            }

            List<Field> searchables = ClassUtils.searchForFieldsWithAnnotation(aClass, Searchable.class);
            searchables.add(ClassUtils.getIdField(aClass).orElseThrow(() -> new ElepyConfigException(String.format("%s does not have an identifying field", aClass.getName()))));


            String hql = "select count(*) from " + aClass.getName() +
                    (searchables.isEmpty() ? "" : (" WHERE " + searchables.stream().map(field -> field.getName() + " LIKE :searchTerm").collect(Collectors.joining(" OR "))));


            return session.createQuery(hql, Long.class).setParameter("searchTerm", "%" + q + "%")
                    .getSingleResult();
        }
    }


    @Override
    public Class<T> getType() {
        return aClass;
    }

    @Override
    public long count(List<FilterQuery> filterQueries) {

        if (filterQueries.isEmpty()) {
            return count();
        } else {
            //Should probably do an optimization here.
            return filter(1, Integer.MAX_VALUE, filterQueries).getValues().size();
        }
    }

    @Override
    public Page<T> filter(int pageNumber, int pageSize, List<FilterQuery> filterQueries) {
        if (filterQueries.size() == 0) {
            return search(new SearchQuery("", null, null, (long) pageSize, pageSize));
        }

        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(aClass);

            final Root<T> root = criteriaQuery.from(aClass);

            criteriaQuery.select(root);


            final List<Predicate> predicatelist = new ArrayList<>();
            for (FilterQuery filterQuery : filterQueries) {

                predicatelist.add(HibernatePredicateFactory.fromFilter(root, cb, filterQuery));
            }
            criteriaQuery.where(cb.and(predicatelist.toArray(new Predicate[0])));


            Query<T> query = session.createQuery(criteriaQuery);

            return toPage(query, pageSize, pageNumber, -1);
        }
    }

    @Override
    public void delete(Object id) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final T item = session.get(aClass, (Serializable) id);
            if (item != null) {
                session.delete(item);
            }

            transaction.commit();
        }
    }


    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            final Query<Long> query = session.createQuery("select count(*) from " + aClass.getName(), Long.class);
            return query.getSingleResult();
        }
    }

    private String getJPAFieldName(Field field) {
        Column annotation = field.getAnnotation(Column.class);

        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }

        return field.getName();
    }

    private void loadLazyCollections(Object object) {

        try {
            objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {

            logger.error(e.getMessage(), e);
            throw new ElepyException("Error loading object's collections.");
        }
    }


}
