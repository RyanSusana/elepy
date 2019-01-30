package com.elepy.dao;

import com.elepy.annotations.RestModel;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

public interface Crud<T> {

    /**
     * Search is how Elepy queries a database based on a {@link QuerySetup} it should provide basic functionality for a search that stays database agnostic.
     *
     * @param querySetup A query setup including all information needed to return the results of a search
     * @return a {@link Page} with objects
     */
    Page<T> search(QuerySetup querySetup);

    /**
     * Queries a database in search of a model item with a specific ID
     *
     * @param id The id of the model item
     * @return An optional item.
     */
    Optional<T> getById(final String id);

    /**
     * This method is used to look for model items based on a specific field name. It is used to help Elepy handle
     * {@link com.elepy.annotations.Unique} Identity constraints. This method is the Elepy equivalent to SQL's:
     * <p>
     * 'SELECT * FROM Item item WHERE item.field LIKE :qry'
     *
     * @param field The field of the model that you want to search
     * @param qry   The search term.
     * @return A list of all found model items.
     * @see com.elepy.concepts.IntegrityEvaluator
     */
    List<T> searchInField(Field field, String qry);

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
    default List<T> getAll() {
        return search(new QuerySetup(null, null, null, null, null)).getValues();
    }

    /**
     * Gets an ID from a given item. Used for internal functionality.
     *
     * @param item The Item you want to get the ID of.
     * @return The found ID
     * @throws ElepyConfigException gets thrown when no ID has been found
     */
    default String getId(final T item) {
        Optional<String> id = ClassUtils.getId(item);

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
     * @param items The items you want to create
     */
    default void create(final T... items) {
        for (T item : items) {
            create(item);
        }
    }

    /**
     * Creates multiple items in the CRUD.
     *
     * @param items The items you want to create
     */
    default void create(final Iterable<T> items) {
        for (T item : items) {
            create(item);
        }
    }


    /**
     * Deletes an item from the CRUD.
     *
     * @param id The ID of the item that you want to delete
     */
    void delete(final String id);

    /**
     *
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
