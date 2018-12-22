package com.elepy;

import com.elepy.concepts.Resource;
import com.elepy.dao.Page;
import com.elepy.dao.jongo.MongoDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fakemongo.Fongo;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.DB;
import com.mongodb.FongoDB;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ElepyEndToEndTest extends BaseTest {

    private static Elepy elepy;
    private static MongoDao<Resource> mongoDao;

    @BeforeAll
    public static void beforeAll() throws Exception {


        elepy = new Elepy();
        Fongo fongo = new Fongo("test");


        final FongoDB db = fongo.getDB("test");


        elepy.attachSingleton(DB.class, db);

        mongoDao = new MongoDao<>(db, "resources", Resource.class);

        elepy.addModel(Resource.class);

        elepy.onPort(7357);

        elepy.start();
    }


    @Test
    public void testFind() throws IOException, UnirestException {


        final long count = mongoDao.count();
        mongoDao.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources").asString();


        Page resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), Page.class);

        assertEquals(count + 1, resourcePage.getValues().size());

    }

    @Test
    public void testFindOne() throws IOException, UnirestException {


        mongoDao.create(validObject());

        final Resource resource = mongoDao.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        assertEquals(foundResource.getId(), resource.getId());


    }

    @Test
    public void testCreate() throws UnirestException, JsonProcessingException {

        final long count = mongoDao.count();
        final Resource resource = validObject();
        resource.setUnique("uniqueCreate");
        final String s = elepy.getObjectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count + 1, mongoDao.count());
    }

    @Test
    void testDelete() throws UnirestException {

        final long beginningCount = mongoDao.count();
        final Resource resource = validObject();

        resource.setId("deleteId");

        mongoDao.create(resource);

        assertEquals(beginningCount + 1, mongoDao.count());
        final HttpResponse<String> delete = Unirest.delete("http://localhost:7357/resources/deleteId").asString();

        assertEquals(beginningCount, mongoDao.count());

    }

    @Test
    void testUpdatePartial() throws UnirestException {

        final long beginningCount = mongoDao.count();
        final Resource resource = validObject();

        resource.setId("updatePartialId");
        resource.setMARKDOWN("ryan");


        mongoDao.create(resource);

        assertEquals(beginningCount + 1, mongoDao.count());
        final HttpResponse<String> patch = Unirest.patch("http://localhost:7357/resources/updatePartialId").field("unique", "uniqueUpdate").asString();

        assertEquals(beginningCount + 1, mongoDao.count());


        Optional<Resource> updatePartialId = mongoDao.getById("updatePartialId");

        assertTrue(updatePartialId.isPresent());
        assertEquals("uniqueUpdate", updatePartialId.get().getUnique());
        assertEquals("ryan", updatePartialId.get().getMARKDOWN());

    }
}
