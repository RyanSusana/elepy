package com.elepy.evaluators;

import com.elepy.Base;
import com.elepy.Resource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ObjectUpdateTest extends Base {

    @Test
    public void testSuccessfulUpdate() {
        ObjectUpdateEvaluator<Resource> resourceObjectUpdateEvaluator = new DefaultObjectUpdateEvaluator<>();

        Resource updatedEditable = validObject();
        updatedEditable.setNumberMax40(BigDecimal.valueOf(9));

        Resource resource = validObject();
        resource.setId(updatedEditable.getId());

        assertDoesNotThrow(() -> resourceObjectUpdateEvaluator.evaluate(resource, updatedEditable));
    }

    @Test
    public void testCantChangeNonEditable() {
        ObjectUpdateEvaluator<Resource> resourceObjectUpdateEvaluator = new DefaultObjectUpdateEvaluator<>();

        Resource updatedNonEditable = validObject();
        updatedNonEditable.setNonEditable(UUID.randomUUID().toString());

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> resourceObjectUpdateEvaluator.evaluate(validObject(), updatedNonEditable));
    }


}
