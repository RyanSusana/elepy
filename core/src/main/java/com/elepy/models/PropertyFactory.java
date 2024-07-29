package com.elepy.models;

import com.elepy.annotations.*;
import com.elepy.dao.FilterType;
import com.elepy.models.options.OptionFactory;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;

import javax.persistence.Column;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyFactory {

    private final OptionFactory optionFactory;

    public PropertyFactory() {
        optionFactory = new OptionFactory();
    }

    /**
     * Gets a list of properties from a class
     */
    public List<Property> describeClass(Class cls) {
        return getAccessibleObjects(cls).stream()
                .map(this::describeAccessibleObject)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    /**
     * Gets all fields that are not marked as hidden and all methods annotated with @Generated
     */
    public List<AccessibleObject> getAccessibleObjects(Class<?> cls) {
        return Stream.concat(
                ReflectionUtils.getAllFields(cls).stream()
                        .filter(field -> !field.isAnnotationPresent(Hidden.class)),
                Stream.of(cls.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Generated.class))
        ).collect(Collectors.toList());
    }

    public Property describeAccessibleObject(AccessibleObject accessibleObject) {

        final boolean idProperty;

        if (accessibleObject instanceof Field) {
            idProperty = ReflectionUtils
                    .getIdField(((Field) accessibleObject).getDeclaringClass()).map(field1 -> field1.getName().equals(((Field) accessibleObject).getName())).orElse(false);
        } else {
            idProperty = false;
        }

        Property property = createProperty(accessibleObject, idProperty);

        setupSearch(accessibleObject, property, idProperty);
        return property;
    }

    private Property createProperty(AccessibleObject accessibleObject, boolean idProperty) {
        Property property = createTypedProperty(accessibleObject);

        setupPropertyBasics(accessibleObject, idProperty, property);

        return property;
    }


    public void setupPropertyBasics(AccessibleObject accessibleObject, boolean idProperty, Property property) {
        final Column column = Annotations.get(accessibleObject, Column.class);
        final Importance importance = Annotations.get(accessibleObject, Importance.class);
        final Description description = Annotations.get(accessibleObject, Description.class);
        property.setHiddenFromCMS(accessibleObject.isAnnotationPresent(Hidden.class));

        property.setName(ReflectionUtils.getPropertyName(accessibleObject));
        property.setDescription(Optional.ofNullable(description).map(Description::value).orElse(null));
        property.setShowIf(Optional.ofNullable(accessibleObject.getAnnotation(ShowIf.class)).map(ShowIf::value).orElse("true"));
        property.setJavaName(ReflectionUtils.getJavaName(accessibleObject));
        property.setLabel(ReflectionUtils.getLabel(accessibleObject));
        property.setEditable(!idProperty && (!accessibleObject.isAnnotationPresent(Uneditable.class) || (column != null && !column.updatable())));
        property.setImportance(importance == null ? 0 : importance.value());
        property.setUnique(idProperty || accessibleObject.isAnnotationPresent(Unique.class) || (column != null && column.unique()));
        property.setGenerated(accessibleObject.isAnnotationPresent(Generated.class) || (idProperty && !accessibleObject.isAnnotationPresent(Identifier.class)) || (idProperty && accessibleObject.isAnnotationPresent(Identifier.class) && Annotations.get(accessibleObject, Identifier.class).generated()));
    }

    private void setupSearch(AccessibleObject accessibleObject, Property property, boolean idProperty) {
        property.setSearchable(accessibleObject.isAnnotationPresent(Searchable.class) || idProperty);
        Set<Map<String, Object>> availableFilters = FilterType.getForFieldType(property.getType()).stream().map(FilterType::toMap).collect(Collectors.toSet());

        property.setAvailableFilters(availableFilters);
    }


    private Property createTypedProperty(AccessibleObject field) {
        Property property = new Property();
        FieldType fieldType = FieldType.guessFieldType(field);


        property.setType(fieldType);
        property.setOptions(optionFactory.getOptions(field, fieldType));

        return property;
    }
}
