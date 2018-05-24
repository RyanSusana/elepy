package com.ryansusana.elepy.concepts;

import com.ryansusana.elepy.annotations.Text;
import com.ryansusana.elepy.exceptions.RestErrorMessage;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.fail;


public class ObjectEvaluateTest extends  BaseTest{

    private ObjectEvaluator<Resource> resourceObjectEvaluator;

    @Before
    public void setup() {

        this.resourceObjectEvaluator = new ObjectEvaluatorImpl<>();

    }



    @Test
    public void testValidObject() throws Exception {
        resourceObjectEvaluator.evaluate(validObject());
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

    @Test
    public void testRecursiveEvaluation() throws Exception {

        Resource resource = validObject();
        Resource inner = validObject();

        inner.setNumberMin10Max50(BigDecimal.valueOf(9));
        resource.setInnerObject(inner);

        exceptionTest(resource);
    }

    private void exceptionTest(Resource resource) throws Exception {
        try {

            resourceObjectEvaluator.evaluate(resource);
            fail("This object should not be considered valid");
        } catch (RestErrorMessage ignored) {
        }
    }
}
