package com.elepy.evaluators;

import com.elepy.Base;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Resource;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;


public class ObjectAtomicIntegrityTest extends Base {
    @Test
    public void testAtomicIntegrity() throws IllegalAccessException {
        final Resource resource = validObject();
        final Resource resource1 = validObject();

        resource.setId(91);
        resource1.setId(91);
        final AtomicIntegrityEvaluator<Resource> resourceAtomicIntegrityEvaluator = new AtomicIntegrityEvaluator<>();

        try {
            resourceAtomicIntegrityEvaluator.evaluate(Arrays.asList(resource, resource1));

        } catch (ElepyException errorMessage) {
            if (errorMessage.getMessage().contains("duplicate")) {


            } else {
                fail("No duplicates found in arraylist");
            }
        }
    }
}
