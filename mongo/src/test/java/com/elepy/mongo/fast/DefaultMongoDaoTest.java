package com.elepy.mongo.fast;

import com.elepy.di.DefaultElepyContext;
import com.elepy.models.SchemaFactory;
import com.elepy.mongo.CustomJacksonModule;
import com.elepy.mongo.ElepyCodecRegistry;
import com.elepy.mongo.MongoCrudFactory;
import com.elepy.mongo.MongoDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mongojack.internal.MongoJackModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.elepy.dao.Filters.eq;
import static com.elepy.dao.Filters.search;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DefaultMongoDaoTest extends BaseFongo {

    private MongoDao<Resource> defaultMongoDao;
    private MongoCollection<Resource> collection;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        final var db = getDb();
        defaultElepyContext.registerDependency(MongoDatabase.class, db);
        defaultElepyContext.registerDependency(new ObjectMapper());

        defaultMongoDao = (MongoDao<Resource>) defaultElepyContext.initialize(MongoCrudFactory.class).crudFor(new SchemaFactory().createDeepSchema(Resource.class));

        final var objectMapper = new ObjectMapper();

        MongoJackModule.configure(objectMapper);
        CustomJacksonModule.configure(objectMapper);
        ElepyCodecRegistry jacksonCodecRegistry = new ElepyCodecRegistry(objectMapper, null);
        jacksonCodecRegistry.addCodecForClass(Resource.class);
        this.collection =
                db.getCollection("resources")
                        .withDocumentClass(Resource.class)
                        .withCodecRegistry(jacksonCodecRegistry);
    }

    @Test
    public void testCreate() {
        final Resource resource1 = validObject();
        resource1.setTextField("create");
        final Resource resource2 = validObject();
        defaultMongoDao.create(resource1);
        defaultMongoDao.create(resource2);

        final long resources = collection.count();

        assertThat(resources).isEqualTo(2);

        assertThat(collection.find(Filters.eq("_id", resource1.getId())).first().getTextField())
                .isEqualTo("create");
        assertThat(collection.find(Filters.eq("_id", resource1.getId())).first().getTextField())
                .isEqualTo("create");
    }

    @Test
    public void testDelete() {
        final Resource resource = validObject();
        defaultMongoDao.create(resource);
        defaultMongoDao.deleteById(resource.getId());
        assertThat(count()).isEqualTo(0);
    }


    @Test
    public void testSearch() {

        final Resource resource = validObject();
        defaultMongoDao.create(resource);


        final List<Resource> searchable = defaultMongoDao.findLimited(1, search("sear"));
        assertThat(searchable.size()).isEqualTo(1);

    }

    @Test
    public void testMultiCreate() {
        final Resource resource = validObject();
        final Resource resource2 = validObject();

        resource2.setUnique("Unique2");

        defaultMongoDao.create(Arrays.asList(resource, resource2));

        assertThat(count()).isEqualTo(2);


    }

    @Test
    public void testIndexCreation() {

        var indexes = StreamSupport.stream(defaultMongoDao.getMongoCollection().listIndexes().spliterator(), false)
                .collect(Collectors.toList());


        assertThat(indexes)
                .hasSize(4);
    }

    private long count() {
        return collection.count();
    }
}
