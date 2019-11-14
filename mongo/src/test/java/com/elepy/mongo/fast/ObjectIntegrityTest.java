package com.elepy.mongo.fast;

import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.ModelContext;
import com.elepy.mongo.DefaultMongoDao;
import com.elepy.utils.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObjectIntegrityTest extends BaseFongo {

    @Test
    public void testIntegrityUnique() throws Exception {
        super.setUp();
        DefaultMongoDao<Resource> defaultMongoDao = new DefaultMongoDao<>(getDb(), "resources", ModelUtils.createModelFromClass(Resource.class));
        final DefaultIntegrityEvaluator<Resource> evaluator = new DefaultIntegrityEvaluator<>(new ModelContext<>(ModelUtils.createModelFromClass(Resource.class), defaultMongoDao, null, null));
        defaultMongoDao.create(validObject());


        defaultMongoDao.create(validObject());
        try {
            evaluator.evaluate(validObject(), EvaluationType.UPDATE);
            fail("Was supposed to throw an ElepyException");
        } catch (ElepyException ignored) {

        }
    }
}
