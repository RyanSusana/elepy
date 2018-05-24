package com.elepy.dao;


import com.elepy.exceptions.RestErrorMessage;
import com.elepy.utils.ClassUtils;

import java.util.Optional;

public interface Crud<T> {

    default Page<T> get() {
        return get(new PageSetup(Integer.MAX_VALUE, 1));
    }

    Page<T> get(PageSetup pageSetup);


    Optional<T> getById(final String id);

    Page<T> search(final SearchSetup search, PageSetup pageSetup);

    Page<T> search(final String query, Object... params);

    void delete(final String id);

    void update(final T item);

    void create(final T item);

    default String getId(final T item) {
        Optional<String> id = ClassUtils.getId(item);

        if (id.isPresent()) {
            return id.get();
        }
        throw new IllegalStateException(item.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");

    }

    default Page<T> getNextPage(Page<T> page) {

        if (page.getCurrentPageNumber() == page.getLastPageNumber()) {
            throw new RestErrorMessage("No more results");
        }
        PageSetup pageSetup = new PageSetup(page.getOriginalSize(), page.getCurrentPageNumber() + 1);

        return get(pageSetup);

    }

    long count(String query, Object... parameters);

}
