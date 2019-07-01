package com.elepy.mongo;

import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObjectIntegrityTest extends BaseFongo {

    @Test
    public void testIntegrityUnique() throws Exception {
        super.setUp();
        DefaultMongoDao<Resource> defaultMongoDao = new DefaultMongoDao<>(getDb(), "resources", ModelUtils.createModelFromClass(Resource.class));
        final DefaultIntegrityEvaluator<Resource> evaluator = new DefaultIntegrityEvaluator<>();
        defaultMongoDao.create(validObject());


        defaultMongoDao.create(validObject());
        try {
            evaluator.evaluate(validObject(), defaultMongoDao);
            fail("Was supposed to throw a rest error message");
        } catch (ElepyException ignored) {

        }
    }
}
