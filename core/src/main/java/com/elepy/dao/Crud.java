package com.elepy.dao;

import com.elepy.annotations.RestModel;
import com.elepy.annotations.Unique;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.Model;
import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the CRUD interface of Elepy. It is the Core of the Data Access Layer. A crud can be anything from
 * a Database Client to a in-memory collection of objects to another Restful API.
 *
 * @param <T> The type of the Model
 */
public interface Crud<T> {


    Page<T> search(Query query, PageSettings settings);

    default Page<T> search(Query query) {
        return search(query, new PageSettings(1L, Integer.MAX_VALUE, List.of()));
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
     * This method is used to look for model items based on a specific field name. It is used to help Elepy authenticate
     * {@link com.elepy.annotations.Unique} Identity constraints. This method is the Elepy equivalent to SQL's:
     * <p>
     * 'SELECT * FROM Item item WHERE item.field LIKE :qry'
     *
     * @param field The field of the model that you want to search
     * @param qry   The search term.
     * @return A list of all found model items.
     * @see com.elepy.evaluators.IntegrityEvaluator
     */
    List<T> searchInField(Field field, String qry);

    /**
     * @param fieldName The name of the field
     * @param qry       The search term
     * @return A list of found items
     * @see #searchInField(Field, String)
     */
    default List<T> searchInField(String fieldName, String qry) {
        return searchInField(ReflectionUtils.getPropertyField(getType(), fieldName), qry);
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

    default void updateWithPrototype(Map<String, Object> prototype, Serializable... ids) {
        List<T> toUpdate = new ArrayList<>();

        // remove unique keys from prototype
        ReflectionUtils
                .searchForFieldsWithAnnotation(getType(), Unique.class)
                .stream()
                .map(ReflectionUtils::getPropertyName)
                .forEach(prototype::remove);

        for (Serializable id : ids) {
            getById(id).ifPresent(item -> {
                final Map<String, Object> beforeMap = getObjectMapper().convertValue(item, new TypeReference<Map<String, Object>>() {
                });

                final T t = MapperUtils.objectFromMaps(getObjectMapper(), beforeMap, prototype, getType());

                toUpdate.add(t);
            });
        }

        update(toUpdate);
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

    default void delete(Iterable<Serializable> ids) {
        ids.forEach(this::deleteById);
    }

    default void delete(Serializable... ids) {
        delete(Arrays.asList(ids));
    }

    default long count(Query query) {
        return search(query, new PageSettings(1, Integer.MAX_VALUE, List.of())).getValues().size();
    }

    default long count() {
        return count(new Query(null, List.of()));
    }

    /**
     * @return The type of the {@link RestModel}. For use in reflection
     */
    default Class<T> getType() {
        return getModel().getJavaClass();
    }

    Model<T> getModel();


    ObjectMapper getObjectMapper();
}
