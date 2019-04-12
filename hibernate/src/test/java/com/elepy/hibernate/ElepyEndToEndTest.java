package com.elepy.hibernate;

import com.elepy.Elepy;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ElepyEndToEndTest {

    private static Elepy elepy;
    private static Crud<Resource> crud;
    private static int counter = -100;

    @BeforeAll
    public static void beforeAll() throws Exception {


        Configuration hibernateConfiguration = new Configuration().configure();

        elepy = new Elepy()
                .addConfiguration(HibernateConfiguration.of(hibernateConfiguration))
                .addModel(Resource.class)
                .onPort(7357);


        elepy.start();
        crud = elepy.getCrudFor(Resource.class);
    }

    @Test
    public void testFind() throws IOException, UnirestException {


        final long count = crud.count();
        crud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources").asString();


        Page resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), Page.class);

        assertEquals(count + 1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testFilterAndSearch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUnique("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        crud.create(resource);
        crud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources?id_equals=4&unique_contains=filter&numberMax40_equals=25&q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
        assertEquals("filterUnique", resourcePage.getValues().get(0).getUnique());
    }

    @Test
    public void testSearchNotFindingAnything() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUnique("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        crud.create(resource);
        crud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources?q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(0, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testSearch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUnique("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        crud.create(resource);
        crud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources?q=testsearchto").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testFindOne() throws IOException, UnirestException {


        crud.create(validObject());

        final Resource resource = crud.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        assertEquals(foundResource.getId(), resource.getId());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testCreate() throws UnirestException, JsonProcessingException {

        final long count = crud.count();
        final Resource resource = validObject();
        resource.setUnique("uniqueCreate");
        final String s = elepy.getObjectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count + 1, crud.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }

    @Test
    public void testMultiCreate_atomicCreateInsertsNone_OnIntegrityFailure() throws UnirestException, JsonProcessingException {

        final long count = crud.count();

        final Resource resource = validObject();

        resource.setUnique("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUnique("uniqueMultiCreate");


        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count, crud.count());
    }

    @Test
    public void testMultiCreate() throws UnirestException, JsonProcessingException {

        final long count = crud.count();
        final Resource resource = validObject();

        resource.setUnique("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUnique("uniqueMultiCreate1");

        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post("http://localhost:7357/resources").body(s).asString();

        assertEquals(count + 2, crud.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }

    @Test
    void testDelete() throws UnirestException {

        final long beginningCount = crud.count();
        final Resource resource = validObject();

        resource.setId(55);

        crud.create(resource);

        assertEquals(beginningCount + 1, crud.count());
        final HttpResponse<String> delete = Unirest.delete("http://localhost:7357/resources/55").asString();

        assertEquals(beginningCount, crud.count());
        Assertions.assertEquals(200, delete.getStatus());

    }

    @Test
    void testUpdatePartial() throws UnirestException {
        final long beginningCount = crud.count();
        final Resource resource = validObject();

        resource.setId(66);


        crud.create(resource);

        assertEquals(beginningCount + 1, crud.count());
        final HttpResponse<String> patch = Unirest.patch("http://localhost:7357/resources/66").body("{\"id\":" + resource.getId() + ",\"unique\": \"uniqueUpdate\"}").asString();

        assertEquals(beginningCount + 1, crud.count());


        Optional<Resource> updatePartialId = crud.getById(66);

        assertTrue(updatePartialId.isPresent());
        assertEquals("uniqueUpdate", updatePartialId.get().getUnique());
        Assertions.assertEquals(200, patch.getStatus());
    }

    public synchronized Resource validObject() {
        Resource resource = new Resource();

        resource.setId(counter++);
        resource.setMaxLen40("230428");
        resource.setMinLen20("My name is ryan and this is a string  with more than 20 chars");
        resource.setMinLen10MaxLen50("12345678910111213");
        resource.setNumberMax40(BigDecimal.valueOf(40));
        resource.setNumberMin20(BigDecimal.valueOf(20));
        resource.setNumberMin10Max50(BigDecimal.valueOf(15));
        resource.setUnique("unique");
        resource.setTextArea("textarea");
        resource.setTextField("textfield");
        resource.setSearchableField("searchable");
        resource.setRequired("required");

        resource.setNonEditable("nonEditable");

        return resource;
    }
}
