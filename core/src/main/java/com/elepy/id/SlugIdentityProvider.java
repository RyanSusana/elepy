package com.elepy.id;

import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.github.slugify.Slugify;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SlugIdentityProvider<T> implements IdentityProvider<T> {
    private final String[] slugFieldNames;

    private final int prefixLength, maxLength;
    private final Slugify slugify;

    public SlugIdentityProvider() {
        this(5, 33, "name", "title", "slug");
    }

    public SlugIdentityProvider(int prefixLength, int maxLength, String... slugFieldNames) {
        this.prefixLength = prefixLength;
        this.maxLength = maxLength;
        this.slugify = new Slugify();
        this.slugFieldNames = slugFieldNames;
    }

    private Optional<String> getSlug(T obj, List<String> slugFieldNames) {
        final List<Field> fields = new ArrayList<>();


        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            if (slugFieldNames.contains(field.getName()) && (value instanceof String)) {
                return Optional.of(slugify.slugify(getSubStrVersion((String) value, maxLength)));
            }
        }
        return Optional.empty();
    }

    private String getSubStrVersion(String s, int maxLen) {
        if (s.length() < maxLen) {
            return s;
        }

        return s.substring(0, maxLen);

    }

    @Override
    public String getId(T item, Crud<T> dao) {

        final Optional<String> slug = getSlug(item, Arrays.asList(slugFieldNames));

        if (!slug.isPresent()) {
            throw new RestErrorMessage("There is no available slug");
        }

        return getSlug(slug.get(), dao);
    }

    private String getSlug(String slug, Crud crud) {

        final String generatedId = getRandomHexString(prefixLength) + "-" + slug;

        if (crud.getById(generatedId).isPresent()) {
            return getSlug(slug, crud);
        }

        return generatedId;
    }
}
