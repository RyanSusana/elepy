package com.elepy.evaluators;

import com.elepy.Base;
import com.elepy.Resource;
import com.elepy.ResourceArray;
import com.elepy.exceptions.ElepyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;


public class ObjectEvaluateTest extends Base {

    private ObjectEvaluator<Resource> evaluator;

    @BeforeEach
    public void setUp() {
        this.evaluator = new DefaultObjectEvaluator<>();
    }


    @Test
    public void testValidObject() {
        assertDoesNotThrow(() -> evaluator.evaluate(validObject()));
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
    void array_WithInvalidLength_ShouldThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = new ResourceArray();

        resourceArray.setArrayStringMax2Min1TextWithMinimumLengthOf10(

                List.of("hihihihihihihi", "hihihihihihihi", "hihihihihihihi")

        );

        assertThatExceptionOfType(ElepyException.class).isThrownBy(() -> evaluator.evaluate(resourceArray));
    }

    @Test
    void arrayString_WithValidLength_and_InvalidString_ShouldThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = validResourceWithArrays();

        resourceArray.setArrayStringMax2Min1TextWithMinimumLengthOf10(
                List.of("hi")
        );

        assertThatExceptionOfType(ElepyException.class).isThrownBy(() -> evaluator.evaluate(resourceArray));
    }


    @Test
    void arrayString_WithValidLength_and_ValidString_ShouldNotThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = validResourceWithArrays();

        resourceArray.setArrayStringMax2Min1TextWithMinimumLengthOf10(

                List.of("hihihihihihihi", "hihihihihihihi")

        );

        assertDoesNotThrow(() -> evaluator.evaluate(resourceArray));
    }

    @Test
    void arrayNumber_WithValidLength_and_ValidNumbers_ShouldNotThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = validResourceWithArrays();

        resourceArray.setArrayNumberMax2Min1NumberWithMinimumOf10(
                List.of(10, Integer.MAX_VALUE)
        );

        assertDoesNotThrow(() -> evaluator.evaluate(resourceArray));
    }

    @Test
    void arrayNumber_WithValidLength_and_InvalidNumbers_ShouldThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = validResourceWithArrays();

        resourceArray.setArrayNumberMax2Min1NumberWithMinimumOf10(
                List.of(1)
        );

        assertThatExceptionOfType(ElepyException.class).isThrownBy(() -> evaluator.evaluate(resourceArray));
    }

    @Test
    void arrayObject_WithInvalidObject_ShouldThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = validResourceWithArrays();

        final Resource resource = validObject();

        resource.setNumberMax40(BigDecimal.valueOf(50));
        resourceArray.setArrayObject(List.of(resource));
        assertThatExceptionOfType(ElepyException.class).isThrownBy(() -> evaluator.evaluate(resourceArray));
    }

    @Test
    void arrayObject_With_ValidObject_ShouldNotThrow() {

        final var evaluator = new DefaultObjectEvaluator<>();
        final ResourceArray resourceArray = validResourceWithArrays();

        resourceArray.setArrayObject(List.of(validObject()));
        assertDoesNotThrow(() -> evaluator.evaluate(resourceArray));
    }


    private ResourceArray validResourceWithArrays() {
        final ResourceArray resourceArray = new ResourceArray();

        resourceArray.setArrayNumberMax2Min1NumberWithMinimumOf10(List.of(10));
        resourceArray.setArrayStringMax2Min1TextWithMinimumLengthOf10(List.of("hihihihihihi"));

        return resourceArray;
    }

    private void exceptionTest(Resource resource) throws Exception {
        try {
            evaluator.evaluate(resource);
            fail("This object should not be considered valid");
        } catch (ElepyException ignored) {
        }
    }
}
