package com.elepy.hibernate;

import com.elepy.dao.Crud;
import com.elepy.dao.Expression;
import com.elepy.dao.SortOption;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernateDao<T> implements Crud<T> {
    private static final Logger logger = LoggerFactory.getLogger(HibernateDao.class);
    private final SessionFactory sessionFactory;
    private final Schema<T> schema;
    private final ObjectMapper objectMapper;

    public HibernateDao(SessionFactory sessionFactory, ObjectMapper objectMapper, Schema<T> schema) {
        this.sessionFactory = sessionFactory;
        this.schema = schema;
        this.objectMapper = objectMapper;

    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private List<Order> generateOrderBy(CriteriaBuilder cb, Root<T> root, com.elepy.dao.Query settings) {
        return settings.getSortingSpecification().getMap().entrySet().stream().map(sortEntry -> {
            if (sortEntry.getValue().equals(SortOption.ASCENDING)) {
                return cb.asc(root.get(sortEntry.getKey()));
            } else {
                return cb.desc(root.get(sortEntry.getKey()));
            }
        }).collect(Collectors.toList());
    }


    @Override
    public List<T> find(com.elepy.dao.Query query) {
        try (Session session = sessionFactory.openSession()) {


            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(getType());

            final Root<T> root = criteriaQuery.from(getType());


            Predicate predicate = new HibernateQueryFactory<>(schema, root, cb)
                    .generatePredicate(query.getExpression());

            final List<Order> orders = generateOrderBy(cb, root, query);

            Query<T> qry = session.createQuery(criteriaQuery
                    .select(root)
                    .where(predicate)
                    .orderBy(orders))
                    .setFirstResult(query.getSkip())
                    .setMaxResults(query.getLimit());


            return loadLazyCollections(qry.list());
        }
    }

    @Override
    public Optional<T> getById(Serializable id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = sessionFactory.openSession()) {

            final T t = session.get(getType(), id);

            loadLazyCollections(t);
            return Optional.ofNullable(t);
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
            throw ElepyException.internalServerError(e);
        }
    }

    @Override
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(getType());

            final Root<T> root = criteriaQuery.from(getType());

            return loadLazyCollections(session.createQuery(criteriaQuery.select(root)).getResultList());
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
            throw ElepyException.internalServerError(e);
        }
    }


    @Override
    public Schema<T> getSchema() {
        return schema;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public long count(com.elepy.dao.Query query) {

        try (Session session = sessionFactory.openSession()) {


            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);


            final Root<T> root = criteriaQuery.from(getType());

            criteriaQuery.select(cb.count(root));
            Predicate predicate = new HibernateQueryFactory<>(schema, root, cb).generatePredicate(query.getExpression());

            criteriaQuery.where(predicate);


            Query<Long> query1 = session.createQuery(criteriaQuery);


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
    public void delete(Expression expression) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();

            final var criteriaDelete = cb.createCriteriaDelete(getType());

            final Root<T> root = criteriaDelete.from(getType());

            Predicate predicate = new HibernateQueryFactory<>(schema, root, cb)
                    .generatePredicate(expression);

            session.createQuery(criteriaDelete.where(predicate)).executeUpdate();

            transaction.commit();
        }
    }


    private <R> R loadLazyCollections(R object) {

        try {
            objectMapper.writeValueAsString(object);
            return object;
        } catch (JsonProcessingException e) {

            logger.error(e.getMessage(), e);
            throw new ElepyException("Error loading object's collections.");
        }
    }

}
