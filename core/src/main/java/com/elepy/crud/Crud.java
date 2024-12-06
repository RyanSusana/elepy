package com.elepy.crud;

import com.elepy.annotations.Model;
import com.elepy.query.Expression;
import com.elepy.query.Filters;
import com.elepy.query.Queries;
import com.elepy.query.Query;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.schemas.Schema;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the CRUD interface of Elepy. It is the Core of the Data Access Layer. A crud can be anything from
 * a Database Client to a in-memory collection of objects to another Restful API.
 *
 * @param <T> The type of the Model
 */
public interface Crud<T> {


    List<T> find(Query query);

    default List<T> find(Expression expression) {
        return find(Queries.create(expression));
    }

    default List<T> findLimited(Integer limit, Expression expression) {
        return find(Queries.create(expression).limit(Optional.ofNullable(limit).orElse(100)));
    }

    default Optional<T> findOne(Expression expression) {
        return findLimited(1, expression).stream().findFirst();
    }

    /**
     * Queries a database in search of a model item with a specific ID
     *
     * @param id The id of the model item
     * @return An optional item.
     */
    Optional<T> getById(final Serializable id);

    default List<T> getByIds(final Iterable<? extends Serializable> ids) {
        return Lists.newArrayList(ids).stream().map(this::getById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    /**
     * This method is used to update items in a model schema.
     *
     * @param item The item to update
     */
    void update(final T item);

    /**
     * This method is used to update items in a model schema.
     *
     * @param item The item to update
     */
    void create(final T item);

    /**
     * @return all the items from the Database
     */
    List<T> getAll();

    /**
     * Gets an ID from a given item. Used for internal functionality.
     *
     * @param item The Item you want to GET the ID of.
     * @return The found ID
     * @throws ElepyConfigException gets thrown when no ID has been found
     */
    default Serializable getId(final T item) {
        Optional<Serializable> id = ReflectionUtils.getId(item);

        if (id.isPresent()) {
            return id.get();
        }
        throw new ElepyConfigException(item.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");

    }

    /**
     * Updates multiple items in the CRUD.
     *
     * @param items The collection of items you want to update.
     */
    default void update(final Iterable<T> items) {
        for (T item : items) {
            update(item);
        }
    }

    /**
     * Creates multiple items in the CRUD.
     *
     * @param items The items you want to singleCreate
     */
    default void create(final T... items) {
        for (T item : items) {
            create(item);
        }
    }

    /**
     * Creates multiple items in the CRUD.
     *
     * @param items The items you want to singleCreate
     */
    default void create(final Iterable<T> items) {
        for (T item : items) {
            create(item);
        }
    }


    /**
     * Deletes an item from the CRUD.
     *
     * @param id The ID of the item that you want to DELETE
     */
    void deleteById(final Serializable id);

    void delete(Expression expression);

    default void delete(Iterable<Serializable> ids) {
        ids.forEach(this::deleteById);
    }

    default void delete(Serializable... ids) {
        delete(Arrays.asList(ids));
    }

    long count(Expression query);

    default long count() {
        return count(Expression.empty());
    }

    /**
     * @return The type of the {@link Model}. For use in reflection
     */
    default Class<T> getType() {
        return getSchema().getJavaClass();
    }

    Schema<T> getSchema();

}
