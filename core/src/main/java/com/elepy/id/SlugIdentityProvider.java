package com.elepy.id;

import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SlugIdentityProvider implements IdentityProvider {
    private final String[] slugFieldNames;

    private final int prefixLength;

    public SlugIdentityProvider(){
        this(5, "name","title","slug");
    }
    public SlugIdentityProvider(int prefixLength, String... slugFieldNames) {
        this.prefixLength = prefixLength;
        this.slugFieldNames = slugFieldNames;
    }

    private Optional<String> getSlug(Object obj, List<String> slugFieldNames) {
        List<Field> fields = new ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            if (slugFieldNames.contains(field.getName()) && (obj instanceof String)) {
                return Optional.of(field.getName());
            }
        }
        return Optional.empty();
    }

    @Override
    public String getId(Object item, Crud dao) {

        final Optional<String> slug = getSlug(item, Arrays.asList(slugFieldNames));

        if (!slug.isPresent()) {
            throw new RestErrorMessage("There is no available slug");
        }

        return getSlug(slug.get(), dao);
    }

    private String getSlug(String slug, Crud crud) {

        String generatedId = getRandomHexString(prefixLength) + "-" + slug;

        if (crud.getById(generatedId).isPresent()) {
            return getSlug(slug, crud);
        }

        return generatedId;
    }
}
