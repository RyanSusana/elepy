package com.elepy;

import com.elepy.concepts.Resource;
import com.elepy.dao.Page;
import com.elepy.dao.ResourceDao;
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


public class ElepyEndToEndTest extends Base {

    private static Elepy elepy;
    private static ResourceDao defaultMongoDao;

    @BeforeAll
    public static void beforeAll() throws Exception {


        elepy = new Elepy();
        Fongo fongo = new Fongo("test");


        final FongoDB db = fongo.getDB("test");


        elepy.attachSingleton(DB.class, db);


        elepy.addModel(Resource.class);

        elepy.onPort(7357);

        elepy.start();

        defaultMongoDao = (ResourceDao) elepy.getCrudFor(Resource.class);
    }


    @Test
    public void testFind() throws IOException, UnirestException {


        final long count = defaultMongoDao.count();
        defaultMongoDao.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources").asString();


        Page resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), Page.class);

        assertEquals(count + 1, resourcePage.getValues().size());

        assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testFindOne() throws IOException, UnirestException {


        defaultMongoDao.create(validObject());

        final Resource resource = defaultMongoDao.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        assertEquals(foundResource.getId(), resource.getId());

        assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testCreate() throws UnirestException, JsonProcessingException {

        final long count = defaultMongoDao.count();
        final Resource resource = validObject();
        resource.setUnique("uniqueCreate");
        final String s = elepy.getObjectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count + 1, defaultMongoDao.count());
        assertEquals(200, postRequest.getStatus());
    }


    @Test
    public void testMultiCreate_atomicCreateInsertsNone_OnIntegrityFailure() throws UnirestException, JsonProcessingException {

        final long count = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setUnique("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUnique("uniqueMultiCreate");

        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count, defaultMongoDao.count());
    }

    @Test
    public void testMultiCreate() throws UnirestException, JsonProcessingException {

        final long count = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setUnique("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUnique("uniqueMultiCreate1");

        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count + 2, defaultMongoDao.count());
        assertEquals(200, postRequest.getStatus());
    }

    @Test
    void testDelete() throws UnirestException {

        final long beginningCount = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setId("deleteId");

        defaultMongoDao.create(resource);

        assertEquals(beginningCount + 1, defaultMongoDao.count());
        final HttpResponse<String> delete = Unirest.delete("http://localhost:7357/resources/deleteId").asString();

        assertEquals(beginningCount, defaultMongoDao.count());
        assertEquals(200, delete.getStatus());

    }

    @Test
    void testUpdatePartial() throws UnirestException {
        final long beginningCount = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setId("updatePartialId");
        resource.setMARKDOWN("ryan");


        defaultMongoDao.create(resource);

        assertEquals(beginningCount + 1, defaultMongoDao.count());
        final HttpResponse<String> patch = Unirest.patch("http://localhost:7357/resources/updatePartialId").body("{\"id\":\"" + resource.getId() + "\",\"unique\": \"uniqueUpdate\"}").asString();

        assertEquals(beginningCount + 1, defaultMongoDao.count());


        Optional<Resource> updatePartialId = defaultMongoDao.getById("updatePartialId");

        assertTrue(updatePartialId.isPresent());
        assertEquals("uniqueUpdate", updatePartialId.get().getUnique());
        assertEquals("ryan", updatePartialId.get().getMARKDOWN());
        assertEquals(200, patch.getStatus());
    }

    @Test
    void testExtraRouteInService() throws UnirestException {

        final String shouldReturn = "I am here";
        Resource resource1 = validObject();
        resource1.setId("extra");
        resource1.setTextField(shouldReturn);
        defaultMongoDao.create(resource1);
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources/" + resource1.getId() + "/extra").asString();

        assertEquals(200, getRequest.getStatus());
        assertEquals(shouldReturn, getRequest.getBody());
    }

    @Test
    void testExtraRoute() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources-extra").asString();

        assertEquals(201, getRequest.getStatus());
        assertEquals("generated", getRequest.getBody());
    }
}
