package com.elepy.concepts;

import com.elepy.BaseFongoTest;
import com.elepy.dao.jongo.MongoDao;
import com.elepy.exceptions.ElepyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class ObjectIntegrityTest extends BaseFongoTest {

    @Test
    public void testIntegrityUnique() throws Exception {
        super.setUp();
        MongoDao<Resource> mongoDao = new MongoDao<>(getDb(), "resources", Resource.class);
        final IntegrityEvaluatorImpl<Resource> evaluator = new IntegrityEvaluatorImpl<>();
        mongoDao.create(validObject());


        mongoDao.create(validObject());
        try {
            evaluator.evaluate(validObject(), mongoDao);
            fail("Was supposed to throw a rest error message");
        } catch (ElepyException ignored) {

        }
    }
}
