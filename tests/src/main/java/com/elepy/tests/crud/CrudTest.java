package com.elepy.tests.crud;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.tests.ElepyTest;
import com.elepy.tests.basic.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CrudTest implements ElepyTest {


    protected static int portCounter = 7300;

    protected static int resourceCounter = -100;
    protected final String userUrl;
    protected final String url;
    protected final Configuration[] configurations;
    protected final int port;
    protected Elepy elepy;
    protected Crud<Resource> resourceCrud;

    public CrudTest(Configuration... configurations) {
        this.configurations = configurations;
        port = portCounter++;

        url = String.format("http://localhost:%d", port);
        userUrl = url + "/users";


    }

    public Configuration configuration() {
        return null;
    }


    @BeforeEach
    protected void setUp() {
        resourceCrud.delete(resourceCrud.getAll().stream().map(Resource::getId).collect(Collectors.toList()));
    }

    @BeforeAll
    protected void setUpAll() {
        final Configuration configuration = configuration();


        elepy = new Elepy()
                .addModel(Resource.class)
                .onPort(port);

        List.of(configurations).forEach(elepy::addConfiguration);

        if (configuration != null) elepy.addConfiguration(configuration);

        elepy.start();

        resourceCrud = elepy.getCrudFor(Resource.class);
    }

    @AfterAll
    protected void tearDown() {
        elepy.stop();
    }


    @Test
    public void can_FindItems_as_Intended() throws IOException, UnirestException {


        final long count = resourceCrud.count();
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources").asString();


        Page resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), Page.class);

        Assertions.assertEquals(count + 1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_FilterAndSearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?id_equals=4&uniqueField_contains=filter&numberMax40_equals=25&q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        Assertions.assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
        Assertions.assertEquals("filterUnique", resourcePage.getValues().get(0).getUniqueField());
    }

    @Test
    public void canNot_FindItems_when_QueryDoesntMatch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        System.out.println(getRequest.getBody());
        Assertions.assertEquals(0, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_SearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?q=testsearchto").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        Assertions.assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_FindItem_byId() throws IOException, UnirestException {
        resourceCrud.create(validObject());

        final Resource resource = resourceCrud.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        Assertions.assertEquals(foundResource.getId(), resource.getId());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_CreateItem() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueCreate");
        final String s = elepy.getObjectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        Assertions.assertEquals(count + 1, resourceCrud.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }


    @Test
    public void doesNot_CreateMultipleItems_when_ThereAreIntegrityIssues() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();

        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate");


        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        Assertions.assertEquals(count, resourceCrud.count());
    }

    @Test
    public void can_CreateMultipleItems_inOneRequest() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate1");

        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        System.out.println(postRequest.getBody());
        Assertions.assertEquals(count + 2, resourceCrud.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }

    @Test
    void can_DeleteItem() throws UnirestException {

        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(55);

        resourceCrud.create(resource);

        Assertions.assertEquals(beginningCount + 1, resourceCrud.count());
        final HttpResponse<String> delete = Unirest.delete(url + "/resources/55").asString();

        Assertions.assertEquals(beginningCount, resourceCrud.count());
        Assertions.assertEquals(200, delete.getStatus());

    }

    @Test
    void can_UpdateItemPartially_without_AffectingOtherFields() throws UnirestException {
        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(66);
        resource.setMARKDOWN("ryan");


        resourceCrud.create(resource);

        Assertions.assertEquals(beginningCount + 1, resourceCrud.count());
        final HttpResponse<String> patch = Unirest.patch(url + "/resources/66").body("{\"id\":" + resource.getId() + ",\"uniqueField\": \"uniqueUpdate\"}").asString();

        Assertions.assertEquals(beginningCount + 1, resourceCrud.count());


        Optional<Resource> updatePartialId = resourceCrud.getById(66);

        Assertions.assertTrue(updatePartialId.isPresent());
        Assertions.assertEquals("uniqueUpdate", updatePartialId.get().getUniqueField());
        Assertions.assertEquals("ryan", updatePartialId.get().getMARKDOWN());
        Assertions.assertEquals(200, patch.getStatus());
    }

    protected synchronized Resource validObject() {
        Resource resource = new Resource();

        resource.setId(resourceCounter++);
        resource.setMaxLen40("230428");
        resource.setMinLen20("My name is ryan and this is a string  with more than 20 chars");
        resource.setMinLen10MaxLen50("12345678910111213");
        resource.setNumberMax40(BigDecimal.valueOf(40));
        resource.setNumberMin20(BigDecimal.valueOf(20));
        resource.setNumberMin10Max50(BigDecimal.valueOf(15));
        resource.setUniqueField("unique");
        resource.setMARKDOWN("MARKDOWN");
        resource.setTextArea("textarea");
        resource.setTextField("textfield");
        resource.setSearchableField("searchable");
        resource.setRequiredField("required");

        resource.setNonEditable("nonEditable");

        return resource;
    }
} 
