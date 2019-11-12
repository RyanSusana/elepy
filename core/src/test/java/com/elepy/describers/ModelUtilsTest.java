package com.elepy.describers;

import com.elepy.Resource;
import com.elepy.ResourceArray;
import com.elepy.models.FieldType;
import com.elepy.models.Model;
import com.elepy.models.Property;
import com.elepy.models.TextType;
import com.elepy.models.options.*;
import com.elepy.uploads.FileUploadEvaluator;
import com.elepy.utils.ModelUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.elepy.models.FieldType.*;
import static com.google.common.truth.Truth.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ModelUtilsTest {

    @Test
    void testCorrectOrderingAndPropertySizeOfModel() {

        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        //Should be + 1 because of the   @GeneratedField method
        assertThat(modelFromClass.getProperties().size()).isEqualTo(Resource.class.getDeclaredFields().length + 1);
        assertThat(modelFromClass.getProperties().get(0).getName()).isEqualTo("id");
        assertThat(modelFromClass.getProperties().get(modelFromClass.getProperties().size() - 1).getName()).isEqualTo("generated");
    }


    @Test
    void testCorrectDate() throws ParseException {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);
        final Property property = modelFromClass.getProperty("date");
        final DateOptions of = property.getOptions();

        assertThat(of.getMinimumDate()).isEqualTo(new Date(0));
        assertThat(of.getMaximumDate()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").parse("2019-22-12"));
        assertThat(property.getType())
                .isEqualTo(DATE);
    }

    @Test
    void testCorrectText() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("minLen10MaxLen50");
        final TextOptions of = property.getOptions();
        assertThat(of.getTextType()).isEqualTo(TextType.TEXTAREA);
        assertThat(of.getMinimumLength()).isEqualTo(10);
        assertThat(of.getMaximumLength()).isEqualTo(50);
        assertThat(property.getType())
                .isEqualTo(TEXT);
    }

    @Test
    void testCorrectEnum() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("textType");
        final EnumOptions of = property.getOptions();

        assertThat(of.getAvailableValues().stream().map(map -> map.get("enumValue")).collect(Collectors.toList()).contains("HTML")).isTrue();
        assertThat(property.getType())
                .isEqualTo(ENUM);
    }

    @Test
    void testCorrectObject() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("resourceCustomObject");
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
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("numberMin10Max50");
        final NumberOptions of = property.getOptions();

        assertThat(of.getMinimum()).isEqualTo(10);
        assertThat(of.getMaximum()).isEqualTo(50);
        assertThat(property.getType())
                .isEqualTo(NUMBER);
    }

    @Test
    void testCorrectUnique() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertThat(modelFromClass.getProperty("unique").isUnique()).isTrue();

    }

    @Test
    void testCorrectFileReference() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("fileReference");
        final FileReferenceOptions reference = property.getOptions();

        assertThat(reference.getAllowedMimeType())
                .isEqualTo("image/png");
        assertThat(reference.getMaximumFileSize())
                .isEqualTo(FileUploadEvaluator.DEFAULT_MAX_FILE_SIZE);

        assertThat(property.getType())
                .isEqualTo(FILE_REFERENCE);

    }


    @Test
    void testCorrectRequired() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertThat(modelFromClass.getProperty("required").isRequired()).isTrue();
    }

    @Test
    void testCorrectUneditable() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertThat(modelFromClass.getProperty("nonEditable").isEditable()).isFalse();
    }

    @Test
    void testCorrectIdProperty() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertThat(modelFromClass.getIdProperty()).isEqualTo("id");
    }

    @Test
    void testCorrectFeaturedProperty() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertThat(modelFromClass.getFeaturedProperty()).isEqualTo("featuredProperty");
    }

    @Test
    void testCorrectArray() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final Property arrayEnum = model.getProperty("arrayEnum");

        assertThat(arrayEnum.getType()).isEqualTo(ARRAY);
    }

    @Test
    void testCorrectArray_ENUM() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final ArrayOptions arrayEnum = model.getProperty("arrayEnum").getOptions();

        assertThat(arrayEnum.getArrayType()).isEqualTo(FieldType.ENUM);
    }

    @Test
    void testCorrectArray_TEXT() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final ArrayOptions arrayString = model.getProperty("arrayString").getOptions();

        assertThat(arrayString.getArrayType()).isEqualTo(FieldType.TEXT);
    }


    @Test
    void testCorrectArray_NUMBER() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final ArrayOptions arrayNumber = model.getProperty("arrayNumber").getOptions();

        assertThat(arrayNumber.getArrayType()).isEqualTo(FieldType.NUMBER);
    }

    @Test
    void testCorrectArray_DATE() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final ArrayOptions arrayDate = model.getProperty("arrayDate").getOptions();

        assertThat(arrayDate.getArrayType()).isEqualTo(FieldType.DATE);
    }

    @Test
    void testCorrectArray_OBJECT() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final ArrayOptions arrayObject = model.getProperty("arrayObject").getOptions();

        assertThat(arrayObject.getArrayType()).isEqualTo(FieldType.OBJECT);

    }

    @Test
    void testCorrectArray_BOOLEAN() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);

        final ArrayOptions arrayBoolean = model.getProperty("arrayBoolean").getOptions();

        assertThat(arrayBoolean.getArrayType()).isEqualTo(FieldType.BOOLEAN);

    }

    @Test
    void testStrongRecursiveObject() {
        final int MAX_RECURSION_DEPTH = 8;
        final List<Property> properties = ModelUtils.describeClass(StrongRecursiveModel.class);
        List<Property> currentProperties = properties;

        for (int i = 0; i < MAX_RECURSION_DEPTH; i++) {
            currentProperties = goDeeper("recursiveObject", currentProperties);
        }

        final List<Property> theDeepestRecursiveObject = currentProperties;
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> goDeeper("recursiveObject", theDeepestRecursiveObject));
    }

    @Test
    void testStrongRecursiveArray() {
        final int MAX_RECURSION_DEPTH = 20;
        final List<Property> properties = ModelUtils.describeClass(NavigationMenu.class);
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
        final Property recursiveObject = currentProperties.stream().filter(property -> property.getName().equals(propertyName)).findAny().orElseThrow();

        if (recursiveObject.getOptions() instanceof ArrayOptions) {
            final ArrayOptions<ObjectOptions> options = recursiveObject.getOptions();

            return options.getGenericOptions().getProperties();
        } else {

            final ObjectOptions options = recursiveObject.getOptions();

            return options.getProperties();
        }
    }
}
