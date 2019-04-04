package com.elepy.dao;

import com.elepy.annotations.RestModel;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This is the CRUD interface of Elepy. It is the Core of the Data Access Layer. A crud can be anything from
 * a Database Client to a in-memory collection of objects to another Restful API.
 *
 * @param <T> The type of the Model
 */
public interface Crud<T> {


    Page<T> search(Query query, PageSettings settings);

    /**
     * Queries a database in search of a model item with a specific ID
     *
     * @param id The id of the model item
     * @return An optional item.
     */
    Optional<T> getById(final Serializable id);

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
        return searchInField(ClassUtils.getPropertyField(getType(), fieldName), qry);
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
        Optional<Serializable> id = ClassUtils.getId(item);

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

    default void delete(Iterable<Serializable> ids) {
        ids.forEach(this::deleteById);
    }

    default void delete(Serializable... ids) {
        delete(Arrays.asList(ids));
    }

    /**
     * @param query The searchTerm
     * @return The number of items in the search.
     */
    long count(String query);

    /**
     * @return A count of all the items in the CRUD
     */
    default long count() {
        return count("");
    }

    /**
     * @return The type of the {@link RestModel}. For use in reflection
     */
    Class<T> getType();


    /**
     * @return The default sorting. DO NOT OVERRIDE THIS.
     */
    default AbstractMap.SimpleEntry<String, SortOption> defaultSort() {
        final Class<T> type = getType();
        final RestModel annotation = type.getAnnotation(RestModel.class);

        if (annotation == null) {
            Optional<Field> idField = ClassUtils.getIdField(type);
            return idField.map(field -> new AbstractMap.SimpleEntry<>(ClassUtils.getPropertyName(field), SortOption.ASCENDING))
                    .orElseGet(() -> new AbstractMap.SimpleEntry<>("_id", SortOption.ASCENDING));
        }
        return new AbstractMap.SimpleEntry<>(annotation.defaultSortField(), annotation.defaultSortDirection());
    }
}
