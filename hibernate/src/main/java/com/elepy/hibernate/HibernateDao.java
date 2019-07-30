package com.elepy.hibernate;

import com.elepy.annotations.Searchable;
import com.elepy.annotations.Unique;
import com.elepy.dao.*;
import com.elepy.describers.Model;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;
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
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernateDao<T> implements Crud<T> {
    private static final Logger logger = LoggerFactory.getLogger(HibernateDao.class);
    private final SessionFactory sessionFactory;
    private final Model<T> model;
    private final ObjectMapper objectMapper;

    public HibernateDao(SessionFactory sessionFactory, ObjectMapper objectMapper, Model<T> model) {
        this.sessionFactory = sessionFactory;
        this.model = model;
        this.objectMapper = objectMapper;
    }


    private Page<T> toPage(Query<T> query, int pageSize, long pageNumber, long amountOfResultsWithThatQuery) {
        final Query<T> q = query.setMaxResults(pageSize).setFirstResult(((int) pageNumber - 1) * pageSize);

        final List<T> values = q.list();

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
    public Page<T> search(com.elepy.dao.Query query, PageSettings settings) {
        try (Session session = sessionFactory.openSession()) {


            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(getType());

            final Root<T> root = criteriaQuery.from(getType());

            Predicate predicate = generateSearchQuery(cb, root, query);

            final List<Order> orders = generateOrderBy(cb, root, settings);

            Query<T> qry = session.createQuery(criteriaQuery.select(root).where(predicate).orderBy(orders));

            return toPage(qry, settings.getPageSize(), settings.getPageNumber(), count(query));
        }
    }

    private List<Order> generateOrderBy(CriteriaBuilder cb, Root<T> root, PageSettings settings) {
        return settings.getPropertySortList().stream().map(propertySort -> {
            if (propertySort.getSortOption().equals(SortOption.ASCENDING)) {
                return cb.asc(root.get(propertySort.getProperty()));
            } else {
                return cb.desc(root.get(propertySort.getProperty()));
            }
        }).collect(Collectors.toList());
    }

    private Predicate generateSearchQuery(CriteriaBuilder cb, Root<T> root, com.elepy.dao.Query query) {


        final List<Predicate> filterList = new ArrayList<>();
        for (Filter filter : query.getFilters()) {
            filterList.add(HibernatePredicateFactory.fromFilter(root, cb, filter));
        }


        return cb.and(cb.and(filterList.toArray(new Predicate[0])), cb.or(getSearchPredicates(root, cb, query.getSearchQuery()).toArray(new Predicate[0])));
    }

    private List<Predicate> getSearchPredicates(Root<T> root, CriteriaBuilder cb, String term) {


        if (term == null || term.trim().isEmpty()) {
            //Always true
            return Collections.singletonList(cb.and());
        }
        return getSearchableFields().stream()
                .map(field -> cb.like(cb.lower(root.get(getJPAFieldName(field))), cb.literal("%" + term.toLowerCase() + "%")))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> getById(Serializable id) {
        try (Session session = sessionFactory.openSession()) {

            final T t = session.get(getType(), id);

            loadLazyCollections(t);
            return Optional.ofNullable(t);
        }
    }


    @Override
    public List<T> searchInField(Field field, String qry) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(getType());

            final Root<T> root = criteriaQuery.from(getType());

            if (field.getType().equals(String.class)) {
                criteriaQuery.select(root).where(cb.like(root.get(getJPAFieldName(field)), qry));
            } else {
                criteriaQuery.select(root).where(cb.equal(root.get(getJPAFieldName(field)), Long.parseLong(qry)));
            }
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
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(getType());

            final Root<T> root = criteriaQuery.from(getType());

            return session.createQuery(criteriaQuery.select(root)).getResultList();
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

            List<Field> searchables = ReflectionUtils.searchForFieldsWithAnnotation(getType(), Searchable.class);
            searchables.add(ReflectionUtils.getIdField(getType()).orElseThrow(() -> new ElepyConfigException(String.format("%s does not have an identifying field", getType().getName()))));


            String hql = "select count(*) from " + getType().getName() +
                    (searchables.isEmpty() ? "" : (" WHERE " + searchables.stream().map(field -> field.getName() + " LIKE :searchTerm").collect(Collectors.joining(" OR "))));


            return session.createQuery(hql, Long.class).setParameter("searchTerm", "%" + q + "%")
                    .getSingleResult();
        }
    }

    @Override
    public Model<T> getModel() {
        return model;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public long count(com.elepy.dao.Query query) {

        try (Session session = sessionFactory.openSession()) {


            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);


            final Root<T> root = criteriaQuery.from(getType());

            criteriaQuery.select(cb.count(root));
            Predicate predicate = generateSearchQuery(cb, root, query);

            criteriaQuery.where(predicate);


            Query<Long> query1 = session.createQuery(criteriaQuery);


            query1.getResultList();
            return query1.getSingleResult();
        }
    }


    @Override
    public void deleteById(Serializable id) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final T item = session.get(getType(), id);
            if (item != null) {
                session.delete(item);
            }

            transaction.commit();
        }
    }


    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            final Query<Long> query = session.createQuery("select count(*) from " + getType().getName(), Long.class);
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

    private List<Field> getSearchableFields() {
        List<Field> fields = ReflectionUtils.searchForFieldsWithAnnotation(getType(), Searchable.class, Unique.class);

        Field idField = ReflectionUtils.getIdField(getType()).orElseThrow(() -> new ElepyConfigException("No id idField"));
        fields.add(idField);


        fields.removeIf(field -> !field.getType().equals(String.class));
        return fields;
    }
}
