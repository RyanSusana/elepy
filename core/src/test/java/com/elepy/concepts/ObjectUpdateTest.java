package com.elepy.concepts;

import com.elepy.BaseTest;
import com.elepy.exceptions.RestErrorMessage;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class ObjectUpdateTest extends BaseTest {

    @Test
    public void testSuccessfulUpdate() throws Exception {
        ObjectUpdateEvaluator<Resource> resourceObjectUpdateEvaluator = new ObjectUpdateEvaluatorImpl<>();

        Resource updatedEditable = validObject();
        updatedEditable.setNumberMax40(BigDecimal.valueOf(9));
        resourceObjectUpdateEvaluator.evaluate(validObject(), updatedEditable);
    }

    @Test
    public void testCantChangeNonEditable() throws Exception {

        try {
            ObjectUpdateEvaluator<Resource> resourceObjectUpdateEvaluator = new ObjectUpdateEvaluatorImpl<>();

            Resource updatedNonEditable = validObject();
            updatedNonEditable.setNonEditable(UUID.randomUUID().toString());
            resourceObjectUpdateEvaluator.evaluate(validObject(), updatedNonEditable);

            Assert.fail("Should not be able to update nonEditable");
        } catch (RestErrorMessage e) {

        }
    }


}
