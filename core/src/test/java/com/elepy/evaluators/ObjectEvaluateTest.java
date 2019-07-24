package com.elepy.evaluators;

import com.elepy.Base;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;


public class ObjectEvaluateTest extends Base {

    private ObjectEvaluator<Resource> resourceObjectEvaluator;

    @BeforeEach
    public void setUp() {
        this.resourceObjectEvaluator = new DefaultObjectEvaluator<>();
    }


    @Test
    public void testValidObject() {
        assertDoesNotThrow(() -> resourceObjectEvaluator.evaluate(validObject()));

    }

    @Test
    public void testNumberAnnotation() throws Exception {


        Resource resource = validObject();

        resource.setNumberMin20(BigDecimal.valueOf(19));
        exceptionTest(resource);

        resource = validObject();
        resource.setNumberMax40(BigDecimal.valueOf(41));
        exceptionTest(resource);

    }

    @Test
    public void testRequiredAnnotation() throws Exception {
        Resource resource = validObject();
        resource.setRequired(null);
        exceptionTest(resource);
    }


    private void exceptionTest(Resource resource) throws Exception {
        try {
            resourceObjectEvaluator.evaluate(resource);
            fail("This object should not be considered valid");
        } catch (ElepyException ignored) {
        }
    }
}
