package com.elepy.models;

import com.elepy.dao.FilterTypeDescription;
import com.elepy.utils.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static com.elepy.dao.FilterType.*;
import static com.elepy.models.FieldType.*;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

/*
 * A lot of the desired tests are already covered by the SchemaFactoryTest
 * */
public class PropertyFactoryTest {

    private final PropertyFactory propertyFactory = new PropertyFactory();

    @Test
    void Properties_ShouldHave_CorrectNames() {
        final var properties = propertyFactory.describeClass(SimpleObject.class);

        final var actualPropertyNames = properties.stream().map(Property::getName).collect(Collectors.toSet());

        final var expectedPropertyNames = ReflectionUtils.getAllFields(SimpleObject.class).stream().map(ReflectionUtils::getJavaName).collect(Collectors.toSet());

        assertThat(actualPropertyNames).isNotEmpty();
        assertThat(actualPropertyNames).isEqualTo(expectedPropertyNames);
    }

    @Test
    void Properties_ShouldHave_CorrectTypes() {

        final var properties = propertyFactory.describeClass(SimpleObject.class);

        final var actualPropertyToType = properties.stream().collect(Collectors.toMap(Property::getName, Property::getType));

        final var actualArrayPropertyTypes =
                properties.stream().filter(property -> property.getType().equals(FieldType.ARRAY)).map(Property::getName).collect(Collectors.toSet());

        final var expectedArrayPropertyTypes = properties.stream().filter(property -> property.getName().contains("Of")).map(Property::getName).collect(Collectors.toSet());

        final var expectedPropertyToType = Map.ofEntries(
                entry("inputNotAnnotated", INPUT),
                entry("inputAnnotated", INPUT),
                entry("number", NUMBER),
                entry("numberAnnotated", NUMBER),
                entry("markdown", MARKDOWN),
                entry("fileReference", FILE_REFERENCE),
                entry("html", HTML),
                entry("bool", BOOLEAN),
                entry("boolAnnotated", BOOLEAN),
                entry("boolWrappedAnnotated", BOOLEAN),
                entry("enumType", ENUM)
        );


        ReflectionUtils.getAllFields(SimpleObject.class);
        assertThat(actualArrayPropertyTypes).isEqualTo(expectedArrayPropertyTypes);
        assertThat(actualPropertyToType).containsAllEntriesOf(expectedPropertyToType);
    }

    @Test
    void testCanFindProperFilter() {

        final var properties = propertyFactory.describeClass(SimpleObject.class);

        final var numberProperty = properties.stream().filter(property -> property.getName().equals("numberAnnotated")).findAny().orElseThrow();
        final var numberFilterTypes = numberProperty.getAvailableFilters().stream().map(FilterTypeDescription::filterType).collect(Collectors.toSet());

        final var stringProperty = properties.stream().filter(property -> property.getName().equals("inputAnnotated")).findAny().orElseThrow();
        final var stringFilterTypes = stringProperty.getAvailableFilters().stream().map(FilterTypeDescription::filterType).collect(Collectors.toSet());

        assertThat(numberFilterTypes)
                .containsExactlyInAnyOrder(
                        GREATER_THAN,
                        GREATER_THAN_OR_EQUALS,
                        LESSER_THAN,
                        LESSER_THAN_OR_EQUALS,
                        EQUALS,
                        NOT_EQUALS);

        assertThat(stringFilterTypes)
                .containsExactlyInAnyOrder(
                        CONTAINS,
                        EQUALS,
                        STARTS_WITH,
                        NOT_EQUALS);
    }
}
