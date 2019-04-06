package com.elepy.di;

import com.elepy.Base;
import com.elepy.Elepy;
import com.elepy.dao.Crud;
import com.elepy.dao.ResourceDao;
import com.elepy.dao.jongo.DefaultMongoDao;
import com.elepy.di.circular.Circular1;
import com.elepy.di.circular.Circular2;
import com.elepy.di.circular.Circular3;
import com.elepy.di.named.Named1;
import com.elepy.di.named.Named2;
import com.elepy.di.named.Unnamed1;
import com.elepy.di.threeway.Dependency1;
import com.elepy.di.threeway.Dependency2;
import com.elepy.di.threeway.Dependency3;
import com.elepy.di.threeway.Unsatisfiable;
import com.elepy.di.tree.*;
import com.elepy.di.unsatisfiedconstructor.DelegationAssistant;
import com.elepy.models.Resource;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.FongoDB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContextTest extends Base {

    @Test
    void testCrudFor() {
        Elepy elepy = new Elepy();
        Fongo fongo = new Fongo("test");
        final FongoDB db = fongo.getDB("test");
        DefaultMongoDao<Resource> defaultMongoDao = new DefaultMongoDao<>(db, "resources", Resource.class);


        elepy.registerDependency(DB.class, db).addModel(Resource.class).onPort(1111).start();

        Crud<Resource> crudFor = elepy.getContext().getCrudFor(Resource.class);

        assertNotNull(crudFor);
        assertTrue(crudFor instanceof ResourceDao);

        ResourceDao newDefaultMongoDao = (ResourceDao) crudFor;
        assertEquals(defaultMongoDao.modelType(), newDefaultMongoDao.modelType());
    }

    @Test
    void testDISuccessful() {

        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();


        defaultElepyContext.registerDependency(Dependency2.class);
        defaultElepyContext.registerDependency(Dependency3.class);
        defaultElepyContext.registerDependency(Dependency1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
    }

    @Test
    void testCircularDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Circular3.class);
        defaultElepyContext.registerDependency(Circular2.class);
        defaultElepyContext.registerDependency(Circular1.class);
        ElepyDependencyInjectionException elepyDependencyInjectionException =
                assertThrows(ElepyDependencyInjectionException.class, defaultElepyContext::resolveDependencies);

        assertEquals(2, elepyDependencyInjectionException.getAmount());
    }

    @Test
    void testUnsatisfiedDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Dependency3.class);
        defaultElepyContext.registerDependency(Dependency1.class);
        defaultElepyContext.registerDependency(Unsatisfiable.class);

        ElepyDependencyInjectionException elepyDependencyInjectionException =
                assertThrows(ElepyDependencyInjectionException.class, defaultElepyContext::resolveDependencies);

        assertEquals(2, elepyDependencyInjectionException.getAmount());
    }

    @Test
    void testCorrectStrictMode() {

        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);

        assertDoesNotThrow(() -> {
            defaultElepyContext.registerDependency(Dependency1.class);
            defaultElepyContext.registerDependency(Dependency2.class);
            defaultElepyContext.registerDependency(Dependency3.class);
        });
    }

    @Test
    void testIncorrectStrictMode() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);

        ElepyDependencyInjectionException elepyDependencyInjectionException =
                assertThrows(ElepyDependencyInjectionException.class, () -> {
                    defaultElepyContext.registerDependency(Unsatisfiable.class);
                });

        assertEquals(2, elepyDependencyInjectionException.getAmount());
    }

    @Test
    void testTaggedDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Named1.class);
        defaultElepyContext.registerDependency(Named2.class);
        defaultElepyContext.registerDependency(Unnamed1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
    }

    @Test
    void testTreeDependenciesPreFilled() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Node1.class);
        defaultElepyContext.registerDependency(Node2.class);
        defaultElepyContext.registerDependency(Node3.class);
        defaultElepyContext.registerDependency(Node4.class);
        defaultElepyContext.registerDependency(Node5.class);
        defaultElepyContext.registerDependency(Node6.class);
        defaultElepyContext.registerDependency(Node7.class);
        defaultElepyContext.registerDependency(Node8.class);
        defaultElepyContext.registerDependency(Node9.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
        assertEquals(9, defaultElepyContext.getDependencyKeys().size());
    }

    @Test
    void testTreeDependenciesBuild() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Node1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
        assertEquals(9, defaultElepyContext.getDependencyKeys().size());
    }

    @Test
    void testUnsatisfiedConstructorDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(DelegationAssistant.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);

    }
}
