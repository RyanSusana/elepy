package com.elepy.id;

import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.StringUtils;
import com.github.slugify.Slugify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SlugIdentityProvider<T> implements IdentityProvider<T> {
    private final String[] slugFieldNames;

    private static final Logger logger = LoggerFactory.getLogger(SlugIdentityProvider.class);
    private final int prefixLength;
    private final Slugify slugify;
    private final int maxLength;


    public SlugIdentityProvider() {
        this(3, 70, "name", "title", "slug");
    }

    public SlugIdentityProvider(int prefixLength, int maxLength, String... slugFieldNames) {
        this.prefixLength = prefixLength;
        this.maxLength = maxLength;
        this.slugify = new Slugify();
        this.slugFieldNames = slugFieldNames;
    }

    private Optional<String> getSlug(T obj, List<String> slugFieldNames) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
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
            throw new ElepyException("There is no available slug");
        }

        return getSlug(slug.get(), dao);
    }

    private String getSlug(String slug, Crud crud) {

        final String generatedId = StringUtils.getRandomHexString(prefixLength) + "-" + slug;

        if (crud.getById(generatedId).isPresent()) {
            return getSlug(slug, crud);
        }

        return generatedId;
    }
}
