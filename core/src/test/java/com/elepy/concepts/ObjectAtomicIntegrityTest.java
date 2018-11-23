package com.elepy.concepts;

import com.elepy.BaseTest;
import com.elepy.exceptions.RestErrorMessage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.fail;

public class ObjectAtomicIntegrityTest extends BaseTest {
    @Test
    public void testAtomicIntegrity() throws IllegalAccessException {
        final Resource resource = validObject();
        final Resource resource1 = validObject();

        final AtomicIntegrityEvaluator<Resource> resourceAtomicIntegrityEvaluator = new AtomicIntegrityEvaluator<>();

        try {
            resourceAtomicIntegrityEvaluator.evaluate(Arrays.asList(resource, resource1));

        }catch (RestErrorMessage errorMessage){
            if(errorMessage.getMessage().contains("duplicate")){


            }else{
                fail("No duplicates found in arraylist");
            }
        }
    }
}
