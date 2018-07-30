package com.elepy.hibernate;

import com.elepy.annotations.Searchable;
import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.QuerySetup;
import com.elepy.exceptions.RestErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import spark.utils.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HibernateDao<T> implements Crud<T> {
    private final SessionFactory sessionFactory;
    private final IdentityProvider<T> identityProvider;
    private final Class<T> aClass;

    private final ObjectMapper objectMapper;

    public HibernateDao(SessionFactory sessionFactory, IdentityProvider<T> identityProvider, ObjectMapper objectMapper, Class<T> aClass) {
        this.sessionFactory = sessionFactory;
        this.identityProvider = identityProvider;
        this.aClass = aClass;
        this.objectMapper = objectMapper;
    }


    @Override
    public Page<T> search(QuerySetup querySetup) {
        try (Session session = sessionFactory.openSession()) {

            final String className = aClass.getName();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(aClass);

            final Root<T> root = criteriaQuery.from(aClass);
            final Long count;
            if (StringUtils.isEmpty(querySetup.getQuery())) {
                criteriaQuery.select(root);
                final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
                count = session.createQuery("select count(*) from " + aClass.getName(), Long.class).getSingleResult();

            } else {
                criteriaQuery.select(root);

                final List<Field> searchableFields = com.elepy.utils.ClassUtils.searchForFieldsWithAnnotation(aClass, Searchable.class);

                final List<Predicate> predicatelist = new ArrayList<>();
                for (Field searchableField : searchableFields) {
                    final Predicate like = cb.like(root.get(searchableField.getName()), "%" + querySetup.getQuery() + "%");
                    predicatelist.add(like);

                }
                criteriaQuery.where(cb.or(predicatelist.toArray(new Predicate[0])));
                count = count(querySetup.getQuery());

            }

            Query<T> query = session.createQuery(criteriaQuery);
            List<T> list = query.getResultList();


            return toPage(query, querySetup, count);
        }
    }


    private Page<T> toPage(Query<T> query, QuerySetup pageSearch, long amountOfResultsWithThatQuery) {
        final List<T> values = query.setMaxResults(pageSearch.getPageSize()).setFirstResult(((int) pageSearch.getPageNumber() - 1) * pageSearch.getPageSize()).list();

        loadLazyCollections(values);

        final long remainder = amountOfResultsWithThatQuery % pageSearch.getPageSize();
        long amountOfPages = amountOfResultsWithThatQuery / pageSearch.getPageSize();
        if (remainder > 0) amountOfPages++;

        return new Page<T>(pageSearch.getPageNumber(), amountOfPages, values);
    }

    @Override
    public Optional<T> getById(String id) {
        try (Session session = sessionFactory.openSession()) {

            final T t = session.get(aClass, id);
            loadLazyCollections(t);
            return Optional.ofNullable(t);
        }
    }

    @Override
    public List<T> searchInField(Field field, String qry) {
        try (Session session = sessionFactory.openSession()) {

            final String className = aClass.getSimpleName();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(aClass);

            final Root<T> root = criteriaQuery.from(aClass);

            criteriaQuery.select(root).where(cb.like(root.get(field.getName()), qry));

            Query<T> query = session.createQuery(criteriaQuery);
            final List<T> resultList = query.list();
            loadLazyCollections(resultList);
            return resultList;
        }
    }

    @Override
    public void update(T item) {
        try (Session session = sessionFactory.openSession()) {
            session.update(item);
        }
    }

    @Override
    public void create(T item) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final Field idField = com.elepy.utils.ClassUtils.getIdField(aClass);
            idField.setAccessible(true);

            idField.set(item, identityProvider.getId(item, this));

            session.save(item);
            transaction.commit();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RestErrorMessage(e.getMessage());
        }
    }


    @Override
    public long count(String q) {

        try (Session session = sessionFactory.openSession()) {
            if (StringUtils.isEmpty(q)) {
                String hql = "select count(*) from " + aClass.getName();

                return session.createQuery(hql, Long.class)
                        .getSingleResult();
            }
            StringBuilder sb = new StringBuilder();

            sb.append("false");
            for (Field searchable : com.elepy.utils.ClassUtils.searchForFieldsWithAnnotation(aClass, Searchable.class)) {
                sb.append(" OR ");
                sb.append(searchable.getName());
                sb.append(" LIKE ");
                sb.append(":query");
            }

            String hql = "select count(*) from " + aClass.getName() + " WHERE " + sb.toString().replaceAll("false OR", "");

            return session.createQuery(hql, Long.class).setParameter("query", q)
                    .getSingleResult();
        }
    }


    @Override
    public Class<T> getType() {
        return aClass;
    }

    @Override
    public void delete(String id) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            final T item = session.get(aClass, id);
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


    public void loadLazyCollections(Object object) {

        try {
            objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RestErrorMessage("Error loading object's collections.");
        }
    }


}
