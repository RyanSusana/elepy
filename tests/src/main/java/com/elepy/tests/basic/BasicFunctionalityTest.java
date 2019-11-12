package com.elepy.tests.basic;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.*;
import com.elepy.exceptions.Message;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BasicFunctionalityTest implements ElepyConfigHelper {


    private Crud<User> userCrud;
    private Crud<Resource> resourceCrud;

    private static int resourceCounter = -100;
    protected ElepySystemUnderTest elepy;

    @BeforeAll
    protected void setUpAll() {
        elepy = ElepySystemUnderTest.create();

        elepy.addModel(Resource.class);
        this.configureElepy(elepy);

        elepy.start();

        resourceCrud = elepy.getCrudFor(Resource.class);
        userCrud = elepy.getCrudFor(User.class);
    }

    @BeforeEach
    protected void setUp() {
        resourceCrud.delete(resourceCrud.getAll().stream().map(Resource::getId).collect(Collectors.toList()));
        userCrud.delete(userCrud.getAll().stream().map(User::getId).collect(Collectors.toList()));
    }

    @AfterAll
    protected void tearDownAll() {
        elepy.stop();
    }

    @Test
    void can_CreateInitialUser_and_BlockExtraCreationsWithoutAuthentication() throws UnirestException, JsonProcessingException, InterruptedException {

        User user = new User("admin", "admin", "admin", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(elepy + "/users")
                .body(json(user))
                .asString();

        final HttpResponse<String> response2 = Unirest
                .post(elepy + "/users")
                .body(json(user))
                .asString();


        assertEquals(200, response.getStatus());
        assertEquals(401, response2.getStatus());
        assertEquals(1, userCrud.count());

    }

    @Test
    void can_Login_and_FindOtherUsers() throws UnirestException, JsonProcessingException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedFind = Unirest
                .get(elepy + "/users").asString();
        final HttpResponse<String> authorizedFind = Unirest
                .get(elepy + "/users")
                .basicAuth("admin", "admin")
                .asString();

        assertEquals(200, authorizedFind.getStatus());
        assertEquals(401, unauthorizedFind.getStatus());
    }

    @Test
    void can_Login_and_DeleteUsers() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedDelete = Unirest
                .delete(elepy + "/users")
                .asString();
        final HttpResponse<String> authorizedDelete = Unirest
                .delete(elepy + "/users" + "/user")
                .basicAuth("admin", "admin")
                .asString();

        assertEquals(200, authorizedDelete.getStatus());

        assertEquals(401, unauthorizedDelete.getStatus());
        assertEquals(1, userCrud.count());

    }

    @Test
    void can_GetToken_and_Login() throws UnirestException {
        elepy.createInitialUserViaHttp("ryan", "susana");


        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(Permissions.AUTHENTICATED);
            context.result(Message.of("Perfect!", 200));
        });
        final var getTokenResponse = Unirest.post(elepy + "/elepy-token-login")
                .basicAuth("ryan", "susana")
                .asString();


        final var token = getTokenResponse.getBody().replaceAll("\"","");

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route").header("ELEPY_TOKEN", token).asString();

        assertEquals(200, authenticationResponse.getStatus());
    }

    @Test
    void can_Login_and_UpdateOtherUserPermissions() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.AUTHENTICATED));

        final HttpResponse<String> unauthorizedFind = Unirest
                .put(elepy + "/users" + "/user")
                .body(json(userToUpdate))
                .asString();

        final HttpResponse<String> authorizedFind = Unirest
                .put(elepy + "/users" + "/user")
                .basicAuth("admin", "admin")
                .body(json(userToUpdate))
                .asString();

        final User user = userCrud.getById("user").orElseThrow();

        assertEquals(200, authorizedFind.getStatus());
        assertEquals(401, unauthorizedFind.getStatus());
        assertEquals(2, userCrud.count());
        assertEquals(1, user.getPermissions().size());
        assertEquals(Permissions.AUTHENTICATED, user.getPermissions().get(0));

    }

    @Test
    void cant_Login_and_UpdateSuperUsersPermission() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.SUPER_USER));


        final HttpResponse<String> authorizedFind = Unirest
                .put(elepy + "/users" + "/user")
                .basicAuth("admin", "admin")
                .body(json(userToUpdate))
                .asString();

        final User user = userCrud.getById("user").orElseThrow();


        assertEquals(403, authorizedFind.getStatus());
        assertEquals(0, user.getPermissions().size());
    }

    @Test
    void cant_Login_and_CreateSuperUser_afterOneHasBeenCreated() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.SUPER_USER));

        final HttpResponse<String> authorizedFind = Unirest
                .post(elepy + "/users")
                .basicAuth("admin", "admin")
                .body(json(userToUpdate))
                .asString();

        final User user = userCrud.getById("user").orElseThrow();


        assertEquals(403, authorizedFind.getStatus());
        assertEquals(0, user.getPermissions().size());
    }

    @Test
    void cant_Login_and_DeleteYourself() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        final HttpResponse<String> authorizedFind = Unirest
                .delete(elepy + "/users" + "/admin")
                .basicAuth("admin", "admin")
                .asString();

        final User user = userCrud.getById("user").orElseThrow();


        assertEquals(403, authorizedFind.getStatus());
        assertEquals(0, user.getPermissions().size());
    }

    @Test
    void can_Login_and_UpdateOwnPassword_AsSuperUser() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        final HttpResponse<String> authorizedFind = Unirest
                .delete(elepy + "/users" + "/admin")
                .basicAuth("user", "user")
                .asString();

        assertEquals(403, authorizedFind.getStatus());
        assertTrue(userCrud.getById("admin").isPresent());
    }

    @Test
    void can_AccessExtraRoutes_when_RoutesAreDefinedInAService() throws UnirestException {

        final String shouldReturn = "I am here";
        Resource resource1 = validObject();
        resource1.setId(77);
        resource1.setTextField(shouldReturn);
        resourceCrud.create(resource1);
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources/" + resource1.getId() + "/extra").asString();

        assertEquals(200, getRequest.getStatus());
        assertEquals(shouldReturn, getRequest.getBody());
    }

    @Test
    void can_AccessExtraRoutes_as_Intended() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources-extra").asString();

        assertEquals(201, getRequest.getStatus());
        assertEquals("generated", getRequest.getBody());
    }

    @Test
    void can_AccessActions_as_Intended() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources/actions/extra-action?ids=999,777").asString();

        assertEquals(200, getRequest.getStatus());
        assertEquals("[999,777]", getRequest.getBody());
    }

    @Test
    public void can_FindItems_as_Intended() throws IOException, UnirestException {


        final long count = resourceCrud.count();
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources").asString();


        Page resourcePage = elepy.objectMapper().readValue(getRequest.getBody(), Page.class);


        Assertions.assertEquals(200, getRequest.getStatus(), getRequest.getBody());
        Assertions.assertEquals(count + 1, resourcePage.getValues().size());

    }

    @Test
    public void can_FilterAndSearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources?id_equals=4&uniqueField_contains=filter&numberMax40_equals=25&q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.objectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });


        Assertions.assertEquals(200, getRequest.getStatus(), getRequest.getBody());
        Assertions.assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals("filterUnique", resourcePage.getValues().get(0).getUniqueField());
    }

    @Test
    public void canNot_FindItems_when_QueryDoesntMatch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources?q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.objectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        Assertions.assertEquals(200, getRequest.getStatus(), getRequest.getBody());
        Assertions.assertEquals(0, resourcePage.getValues().size());
    }

    @Test
    public void can_SearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources?q=testsearchto").asString();


        Page<Resource> resourcePage = elepy.objectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        Assertions.assertEquals(200, getRequest.getStatus(), getRequest.getBody());
        Assertions.assertEquals(1, resourcePage.getValues().size());
    }

    @Test
    public void can_FindItem_byId() throws IOException, UnirestException {
        resourceCrud.create(validObject());

        final Resource resource = resourceCrud.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.objectMapper().readValue(getRequest.getBody(), Resource.class);

        Assertions.assertEquals(200, getRequest.getStatus(), getRequest.getBody());
        Assertions.assertEquals(foundResource.getId(), resource.getId());

    }

    @Test
    public void can_CreateItem() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueCreate");
        final String s = elepy.objectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post(elepy + "/resources").body(s).asString();

        Assertions.assertEquals(201, postRequest.getStatus(), postRequest.getBody());
        Assertions.assertEquals(count + 1, resourceCrud.count());
    }


    @Test
    public void doesNot_CreateMultipleItems_when_ThereAreIntegrityIssues() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();

        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate");


        final String s = elepy.objectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(elepy + "/resources").body(s).asString();

        Assertions.assertEquals(count, resourceCrud.count());
    }

    @Test
    public void can_CreateMultipleItems_inOneRequest() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate1");

        final String s = elepy.objectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(elepy + "/resources").body(s).asString();


        Assertions.assertEquals(201, postRequest.getStatus(), postRequest.getBody());
        Assertions.assertEquals(count + 2, resourceCrud.count());
    }

    @Test
    void can_DeleteItem() throws UnirestException {

        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(55);

        resourceCrud.create(resource);

        Assertions.assertEquals(beginningCount + 1, resourceCrud.count());
        final HttpResponse<String> delete = Unirest.delete(elepy + "/resources/55").asString();

        Assertions.assertEquals(200, delete.getStatus(), delete.getBody());
        Assertions.assertEquals(beginningCount, resourceCrud.count());

    }

    @Test
    void can_UpdateItemPartially_without_AffectingOtherFields() throws UnirestException {
        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(66);
        resource.setMARKDOWN("ryan");


        resourceCrud.create(resource);

        Assertions.assertEquals(beginningCount + 1, resourceCrud.count());
        final HttpResponse<String> patch = Unirest.patch(elepy + "/resources/66").body("{\"id\":" + resource.getId() + ",\"uniqueField\": \"uniqueUpdate\"}").asString();

        Assertions.assertEquals(beginningCount + 1, resourceCrud.count());


        Optional<Resource> updatePartialId = resourceCrud.getById(66);


        Assertions.assertEquals(200, patch.getStatus(), patch.getBody());
        Assertions.assertTrue(updatePartialId.isPresent());
        Assertions.assertEquals("uniqueUpdate", updatePartialId.get().getUniqueField());
        Assertions.assertEquals("ryan", updatePartialId.get().getMARKDOWN());
        Assertions.assertEquals(200, patch.getStatus(), patch.getBody());
    }

    @Test
    void can_SortDescending() {
        final Resource resource1 = validObject();
        final Resource resource2 = validObject();

        resource1.setTextField("resource1");
        resource2.setTextField("resource2");


        resourceCrud.create(resource1, resource2);

        final Page<Resource> search = resourceCrud.search(new Query("", List.of()),
                new PageSettings(1, Integer.MAX_VALUE,
                        List.of(new PropertySort("textField", SortOption.DESCENDING))
                )
        );


        assertThat(search.getValues().get(0).getTextField())
                .isEqualTo("resource2");

        assertThat(search.getValues().get(1).getTextField())
                .isEqualTo("resource1");

    }

    @Test
    void can_SortAscending() {
        final Resource resource1 = validObject();
        final Resource resource2 = validObject();


        resource2.setTextField("resource2");
        resource1.setTextField("resource1");


        resourceCrud.create(resource1, resource2);

        final Page<Resource> search = resourceCrud.search(new Query("", List.of()),
                new PageSettings(1, Integer.MAX_VALUE,
                        List.of(new PropertySort("textField", SortOption.ASCENDING))
                )
        );

        assertThat(search.getValues().get(0).getTextField())
                .isEqualTo("resource1");
        assertThat(search.getValues().get(1).getTextField())
                .isEqualTo("resource2");


    }

    private synchronized Resource validObject() {
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


    private String json(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    private void createInitialUsersViaHttp() throws JsonProcessingException, UnirestException {
        User user = new User("admin", "admin", "admin", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(elepy + "/users")
                .body(json(user))
                .asString();

        User user2 = new User("user", "user", "user", Collections.emptyList());

        final HttpResponse<String> response2 =
                Unirest.post(elepy + "/users")
                        .basicAuth("admin", "admin")
                        .body(json(user2))
                        .asString();

        assertEquals(200, response.getStatus());
        assertEquals(200, response2.getStatus());
    }


}
