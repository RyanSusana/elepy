package com.elepy.di;

import com.elepy.Base;
import com.elepy.Elepy;
import com.elepy.concepts.Resource;
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


        elepy.attachSingleton(DB.class, db).addModel(Resource.class).onPort(1111).start();

        Crud<Resource> crudFor = elepy.getContext().getCrudFor(Resource.class);

        assertNotNull(crudFor);
        assertTrue(crudFor instanceof ResourceDao);

        ResourceDao newDefaultMongoDao = (ResourceDao) crudFor;
        assertEquals(defaultMongoDao.modelClassType(), newDefaultMongoDao.modelClassType());
    }

    @Test
    void testDISuccessful() {

        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();


        defaultElepyContext.requireDependency(Dependency2.class);
        defaultElepyContext.requireDependency(Dependency3.class);
        defaultElepyContext.requireDependency(Dependency1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
    }

    @Test
    void testCircularDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.requireDependency(Circular3.class);
        defaultElepyContext.requireDependency(Circular2.class);
        defaultElepyContext.requireDependency(Circular1.class);
        ElepyDependencyInjectionException elepyDependencyInjectionException =
                assertThrows(ElepyDependencyInjectionException.class, defaultElepyContext::resolveDependencies);

        assertEquals(2, elepyDependencyInjectionException.getAmount());
    }

    @Test
    void testUnsatisfiedDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.requireDependency(Dependency3.class);
        defaultElepyContext.requireDependency(Dependency1.class);
        defaultElepyContext.requireDependency(Unsatisfiable.class);

        ElepyDependencyInjectionException elepyDependencyInjectionException =
                assertThrows(ElepyDependencyInjectionException.class, defaultElepyContext::resolveDependencies);

        assertEquals(2, elepyDependencyInjectionException.getAmount());
    }

    @Test
    void testCorrectStrictMode() {

        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);

        assertDoesNotThrow(() -> {
            defaultElepyContext.requireDependency(Dependency1.class);
            defaultElepyContext.requireDependency(Dependency2.class);
            defaultElepyContext.requireDependency(Dependency3.class);
        });
    }

    @Test
    void testIncorrectStrictMode() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);

        ElepyDependencyInjectionException elepyDependencyInjectionException =
                assertThrows(ElepyDependencyInjectionException.class, () -> {
                    defaultElepyContext.requireDependency(Unsatisfiable.class);
                });

        assertEquals(2, elepyDependencyInjectionException.getAmount());
    }

    @Test
    void testTaggedDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.requireDependency(Named1.class);
        defaultElepyContext.requireDependency(Named2.class);
        defaultElepyContext.requireDependency(Unnamed1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
    }

    @Test
    void testTreeDependenciesPreFilled() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.requireDependency(Node1.class);
        defaultElepyContext.requireDependency(Node2.class);
        defaultElepyContext.requireDependency(Node3.class);
        defaultElepyContext.requireDependency(Node4.class);
        defaultElepyContext.requireDependency(Node5.class);
        defaultElepyContext.requireDependency(Node6.class);
        defaultElepyContext.requireDependency(Node7.class);
        defaultElepyContext.requireDependency(Node8.class);
        defaultElepyContext.requireDependency(Node9.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
        assertEquals(9, defaultElepyContext.getDependencyKeys().size());
    }

    @Test
    void testTreeDependenciesBuild() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.requireDependency(Node1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
        assertEquals(9, defaultElepyContext.getDependencyKeys().size());
    }
}
