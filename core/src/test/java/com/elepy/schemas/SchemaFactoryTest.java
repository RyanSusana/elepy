package com.elepy.schemas;

import com.elepy.Category;
import com.elepy.Product;
import com.elepy.Resource;
import com.elepy.ResourceArray;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.schemas.options.*;
import com.elepy.uploads.FileUploadEvaluator;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.elepy.schemas.FieldType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SchemaFactoryTest {

    @Test
    void testHiddenField() {

        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        assertThatExceptionOfType(ElepyConfigException.class)
                .isThrownBy(() -> schemaFromClass.getProperty("hidden"))
                .withMessageContaining("hidden");
    }

    @Test
    void testCorrectOrderingAndPropertySizeOfModel() {

        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        //Keep in mind that there is one hidden field
        assertThat(schemaFromClass.getProperties().size()).isEqualTo(Resource.class.getDeclaredFields().length);
        assertThat(schemaFromClass.getProperties().get(0).getName()).isEqualTo("id");
        assertThat(schemaFromClass.getProperties().get(schemaFromClass.getProperties().size() - 1).getName()).isEqualTo("generated");
    }


    @Test
    void testCorrectDate() throws ParseException {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);
        final Property property = schemaFromClass.getProperty("date");
        final DateOptions of = property.getOptions();

        assertThat(of.getMinimumDate()).isEqualTo(new Date(0));
        assertThat(of.getMaximumDate()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").parse("2019-22-12"));
        assertThat(property.getType())
                .isEqualTo(DATE);
    }

    @Test
    void testCorrectText() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        final Property property = schemaFromClass.getProperty("minLen10MaxLen50");
        assertThat(property.getType())
                .isEqualTo(TEXTAREA);
    }

    @Test
    void testCorrectEnum() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        final Property property = schemaFromClass.getProperty("textType");
        final EnumOptions of = property.getOptions();

        assertThat(of.getAvailableValues().stream().map(map -> map.get("enumValue")).collect(Collectors.toList()).contains("HTML")).isTrue();
        assertThat(property.getType())
                .isEqualTo(ENUM);
    }

    @Test
    void testCorrectObject() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        final Property property = schemaFromClass.getProperty("resourceCustomObject");
        final ObjectOptions of = property.getOptions();

        assertThat(property.getType())
                .isEqualTo(OBJECT);

        assertThat(of.getObjectName())
                .isEqualTo("ResourceCustomObject");
        assertThat(of.getFeaturedProperty())
                .isEqualTo("featured");
        assertThat(of.getProperties().size())
                .isEqualTo(1);
    }

    @Test
    void testCorrectNumber() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        final Property property = schemaFromClass.getProperty("numberMin10Max50");

        assertThat(property.getType())
                .isEqualTo(NUMBER);
    }

    @Test
    void testCorrectUnique() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        assertThat(schemaFromClass.getProperty("unique").isUnique()).isTrue();

    }

    @Test
    void testCorrectFileReference() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        final Property property = schemaFromClass.getProperty("fileReference");
        final FileReferenceOptions fileReference = property.getOptions();

        assertThat(fileReference.getAllowedMimeType())
                .isEqualTo("image/png");
        assertThat(fileReference.getMaximumFileSize())
                .isEqualTo(FileUploadEvaluator.DEFAULT_MAX_FILE_SIZE);

        assertThat(property.getType())
                .isEqualTo(FILE_REFERENCE);
    }

    @Test
    void testCorrectReference_viaCustomAnnotation() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);
        final var property = schemaFromClass.getProperty("customAnnotation");
        final ReferenceOptions options = property.getOptions();

        assertThat(options.getReferenceSchema().getJavaClass())
                .isEqualTo(Category.class);

        assertThat(property.getType())
                .isEqualTo(REFERENCE);

    }

    @Test
    void testCorrectUneditable() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        assertThat(schemaFromClass.getProperty("nonEditable").isEditable()).isFalse();
    }

    @Test
    void testCorrectIdProperty() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        assertThat(schemaFromClass.getIdProperty()).isEqualTo("id");
    }

    @Test
    void testCorrectFeaturedProperty() {
        final Schema<Resource> schemaFromClass = new SchemaFactory().createDeepSchema(Resource.class);

        assertThat(schemaFromClass.getFeaturedProperty()).isEqualTo("featuredProperty");
    }

    @Test
    void testCorrectArray() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final Property arrayEnum = schema.getProperty("arrayEnum");

        assertThat(arrayEnum.getType()).isEqualTo(ARRAY);
    }

    @Test
    void testCorrectArray_ENUM() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayEnum = schema.getProperty("arrayEnum").getOptions();

        assertThat(arrayEnum.getArrayType()).isEqualTo(FieldType.ENUM);
    }

    @Test
    void testCorrectArray_TEXT() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayString = schema.getProperty("arrayString").getOptions();

        assertThat(arrayString.getArrayType()).isEqualTo(FieldType.INPUT);
    }

    @Test
    void testCorrectArray_FILE_REFERENCE() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayString = schema.getProperty("arrayFileReference").getOptions();

        assertThat(arrayString.getArrayType())
                .isEqualTo(FILE_REFERENCE);
    }


    @Test
    void testCorrectArray_NUMBER() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayNumber = schema.getProperty("arrayNumber").getOptions();

        assertThat(arrayNumber.getArrayType()).isEqualTo(FieldType.NUMBER);
    }

    @Test
    void testCorrectArray_DATE() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayDate = schema.getProperty("arrayDate").getOptions();

        assertThat(arrayDate.getArrayType()).isEqualTo(FieldType.DATE);
    }

    @Test
    void testCorrectArray_OBJECT() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayObject = schema.getProperty("arrayObject").getOptions();

        assertThat(arrayObject.getArrayType()).isEqualTo(FieldType.OBJECT);

    }

    @Test
    void testCorrectArray_BOOLEAN() {
        final Schema<ResourceArray> schema = new SchemaFactory().createDeepSchema(ResourceArray.class);

        final ArrayOptions arrayBoolean = schema.getProperty("arrayBoolean").getOptions();

        assertThat(arrayBoolean.getArrayType()).isEqualTo(FieldType.BOOLEAN);

    }

    @Test
    void testStrongRecursiveObject() {
        final int MAX_RECURSION_DEPTH = 8;
        final List<Property> properties = new PropertyFactory().describeClass(StrongRecursiveModel.class);
        List<Property> currentProperties = properties;

        for (int i = 0; i < MAX_RECURSION_DEPTH; i++) {
            currentProperties = goDeeper("recursiveObject", currentProperties);
        }

        final List<Property> theDeepestRecursiveObject = currentProperties;
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> goDeeper("recursiveObject", theDeepestRecursiveObject));
    }

    @Test
    void testReferences() {
        final var schemaFromClass = new SchemaFactory().createDeepSchema(Product.class);

        final var categoryId = schemaFromClass.getProperty("categoryId");

        final ReferenceOptions options = categoryId.getOptions();

        assertThat(categoryId)
                .extracting(Property::getType)
                .isEqualTo(REFERENCE);

        assertThat(options.getReferenceSchema().getJavaClass())
                .isEqualTo(Category.class);

    }

    @Test
    void testStrongRecursiveArray() {
        final int MAX_RECURSION_DEPTH = 20;
        final List<Property> properties = new PropertyFactory().describeClass(NavigationMenu.class);
        List<Property> currentProperties = properties;

        for (int i = 0; i < MAX_RECURSION_DEPTH; i++) {
            if (i == 0) {
                currentProperties = goDeeper("menuItems", currentProperties);
            } else {
                currentProperties = goDeeper("children", currentProperties);
            }
        }

        final List<Property> theDeepestRecursiveObject = currentProperties;
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> goDeeper("children", theDeepestRecursiveObject));
    }

    private List<Property> goDeeper(String propertyName, List<Property> currentProperties) {
        final Property recursiveObject = currentProperties.stream().filter(property -> property.getName()
                .equals(propertyName))
                .findAny()
                .orElseThrow();

        if (recursiveObject.getOptions() instanceof ArrayOptions) {
            final ArrayOptions<ObjectOptions> options = recursiveObject.getOptions();

            return options.getGenericOptions().getProperties();
        } else {

            final ObjectOptions options = recursiveObject.getOptions();

            return options.getProperties();
        }
    }
}
