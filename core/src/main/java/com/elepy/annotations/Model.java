package com.elepy.annotations;


import com.elepy.dao.querymodel.SortOption;

import java.lang.annotation.*;

/**
 * This signifies that this is a RestModel that can be used within Elepy.
 * <p>
 * Rest Models must have one identifying field that can be marked with {@link Identifier}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})

@Inherited
public @interface Model {

    /**
     * @return What do you call this model? e.g 'Products'
     */
    String name();

    /**
     * @return What is the URI of this model? e.g '/products'
     */
    String path ();

    /**
     * @return A brief description of this model
     */
    String description() default "";

    /**
     * @return How do you sort this model by default? ASCENDING or DESCENDING?
     */
    SortOption defaultSortDirection() default SortOption.ASCENDING;

    /**
     * @return What field do you search on by default?
     */
    String defaultSortField() default "";

}
