package com.elepy.describers;

import com.elepy.describers.props.DatePropertyConfig;
import com.elepy.describers.props.EnumPropertyConfig;
import com.elepy.describers.props.NumberPropertyConfig;
import com.elepy.describers.props.TextPropertyConfig;
import com.elepy.models.Resource;
import com.elepy.models.TextType;
import com.elepy.utils.ModelUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ModelUtilsTest {

    @Test
    void testCorrectOrderingAndPropertySizeOfModel() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertEquals(18, modelFromClass.getProperties().size());
        assertEquals("id", modelFromClass.getProperties().get(0).getName());
        assertEquals("generated", modelFromClass.getProperties().get(modelFromClass.getProperties().size() - 1).getName());
    }


    @Test
    void testCorrectDate() throws ParseException {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);
        final DatePropertyConfig of = DatePropertyConfig.of(modelFromClass.getProperty("date"));

        assertEquals(new Date(0), of.getMinimumDate());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2019-22-12"), of.getMaximumDate());
    }

    @Test
    void testCorrectText() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final TextPropertyConfig of = TextPropertyConfig.of(modelFromClass.getProperty("minLen10MaxLen50"));
        assertEquals(TextType.TEXTAREA, of.getTextType());
        assertEquals(10, of.getMinimumLength());
        assertEquals(50, of.getMaximumLength());
    }

    @Test
    void testCorrectEnum() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final EnumPropertyConfig of = EnumPropertyConfig.of(modelFromClass.getProperty("textType"));
        assertTrue(of.getAvailableValues().stream().map(map -> map.get("enumValue")).collect(Collectors.toList()).contains("HTML"));
    }

    @Test
    void testCorrectNumber() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        final NumberPropertyConfig of = NumberPropertyConfig.of(modelFromClass.getProperty("numberMin10Max50"));

        assertEquals(10, of.getMinimum());
        assertEquals(50, of.getMaximum());
    }

    @Test
    void testCorrectUnique() {
        final Model<Resource> modelFromClass = ModelUtils.createModelFromClass(Resource.class);

        assertTrue(modelFromClass.getProperty("unique").isUnique());
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
}
