package com.elepy.concepts;

import com.elepy.Base;
import com.elepy.exceptions.ElepyException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

public class ObjectUpdateTest extends Base {

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

            fail("Should not be able to update nonEditable");
        } catch (ElepyException e) {

        }
    }


}
