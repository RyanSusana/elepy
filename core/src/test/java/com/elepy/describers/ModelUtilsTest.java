package com.elepy.describers;

import com.elepy.Resource;
import com.elepy.ResourceArray;
import com.elepy.models.FieldType;
import com.elepy.models.Model;
import com.elepy.models.Property;
import com.elepy.models.TextType;
import com.elepy.models.props.*;
import com.elepy.uploads.FileUploadEvaluator;
import com.elepy.utils.ModelUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.elepy.models.FieldType.*;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ModelUtilsTest {

    @Test
    void testCorrectOrderingAndPropertySizeOfModel() {

        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        //Should be + 1 because of the   @GeneratedField method
        assertEquals(Resource.class.getDeclaredFields().length + 1, modelFromClass.getProperties().size());
        assertEquals("id", modelFromClass.getProperties().get(0).getName());
        assertEquals("generated", modelFromClass.getProperties().get(modelFromClass.getProperties().size() - 1).getName());
    }


    @Test
    void testCorrectDate() throws ParseException {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);
        final Property property = modelFromClass.getProperty("date");
        final DatePropertyConfig of = DatePropertyConfig.of(property);

        assertEquals(new Date(0), of.getMinimumDate());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2019-22-12"), of.getMaximumDate());
        assertThat(property.getType())
                .isEqualTo(DATE);
    }

    @Test
    void testCorrectText() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("minLen10MaxLen50");
        final TextPropertyConfig of = TextPropertyConfig.of(property);
        assertEquals(TextType.TEXTAREA, of.getTextType());
        assertEquals(10, of.getMinimumLength());
        assertEquals(50, of.getMaximumLength());
        assertThat(property.getType())
                .isEqualTo(TEXT);
    }

    @Test
    void testCorrectEnum() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("textType");
        final EnumPropertyConfig of = EnumPropertyConfig.of(property);

        assertTrue(of.getAvailableValues().stream().map(map -> map.get("enumValue")).collect(Collectors.toList()).contains("HTML"));
        assertThat(property.getType())
                .isEqualTo(ENUM);
    }

    @Test
    void testCorrectObject() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("resourceCustomObject");
        final ObjectPropertyConfig of = ObjectPropertyConfig.of(property);

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
        final NumberPropertyConfig of = NumberPropertyConfig.of(property);

        assertEquals(10, of.getMinimum());
        assertEquals(50, of.getMaximum());
        assertThat(property.getType())
                .isEqualTo(NUMBER);
    }

    @Test
    void testCorrectUnique() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertTrue(modelFromClass.getProperty("unique").isUnique());

    }

    @Test
    void testCorrectFileReference() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final Property property = modelFromClass.getProperty("fileReference");
        final var reference = FileReferencePropertyConfig.of(property);

        assertThat(reference.getAllowedMimeType())
                .isEqualTo("image/png");
        assertThat(reference.getMaxSizeInBytes())
                .isEqualTo(FileUploadEvaluator.DEFAULT_MAX_FILE_SIZE);

        assertThat(property.getType())
                .isEqualTo(FILE_REFERENCE);

    }


    @Test
    void testCorrectRequired() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertTrue(modelFromClass.getProperty("required").isRequired());
    }

    @Test
    void testCorrectUneditable() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertFalse(modelFromClass.getProperty("nonEditable").isEditable());
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

    //TODO consider splitting this into multiple tests and testing the extras
    @Test
    void testCorrectArray() {
        final Model<ResourceArray> model = ModelUtils.createModelFromClass(ResourceArray.class);


        final Property arrayString = model.getProperty("arrayString");
        final Property arrayNumber = model.getProperty("arrayNumber");
        final Property arrayDate = model.getProperty("arrayDate");
        final Property arrayObject = model.getProperty("arrayObject");
        final Property arrayBoolean = model.getProperty("arrayBoolean");
        final Property arrayEnum = model.getProperty("arrayEnum");


        assertThat(arrayString.getType()).isEqualTo(FieldType.ARRAY);
        assertThat(arrayNumber.getType()).isEqualTo(FieldType.ARRAY);
        assertThat(arrayDate.getType()).isEqualTo(FieldType.ARRAY);
        assertThat(arrayObject.getType()).isEqualTo(FieldType.ARRAY);
        assertThat(arrayBoolean.getType()).isEqualTo(FieldType.ARRAY);
        assertThat(arrayEnum.getType()).isEqualTo(FieldType.ARRAY);

        assertThat((FieldType) arrayString.getExtra("arrayType")).isEqualTo(FieldType.TEXT);
        assertThat((FieldType) arrayNumber.getExtra("arrayType")).isEqualTo(FieldType.NUMBER);
        assertThat((FieldType) arrayDate.getExtra("arrayType")).isEqualTo(FieldType.DATE);
        assertThat((FieldType) arrayObject.getExtra("arrayType")).isEqualTo(FieldType.OBJECT);
        assertThat((FieldType) arrayBoolean.getExtra("arrayType")).isEqualTo(FieldType.BOOLEAN);
        assertThat((FieldType) arrayEnum.getExtra("arrayType")).isEqualTo(FieldType.ENUM);
    }

    @Test
    void testRecursive() throws JsonProcessingException {

        final List<Property> properties = ModelUtils.describeClass(RecursiveModel.class);


        final var mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();

        System.out.println(mapper.writeValueAsString(properties));


    }
}
