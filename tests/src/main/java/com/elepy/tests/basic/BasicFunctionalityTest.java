package com.elepy.tests.basic;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.dao.SortOption;
import com.elepy.exceptions.Message;
import com.elepy.tests.CustomUser;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.elepy.dao.Filters.search;
import static com.elepy.dao.Queries.create;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
public abstract class BasicFunctionalityTest implements ElepyConfigHelper {


    private Crud<? extends User> userCrud;
    private Crud<Resource> resourceCrud;

    private static int resourceCounter = -100;
    protected ElepySystemUnderTest elepy;

    @BeforeAll
    protected void setUpAll() {
        HttpClient httpClient = HttpClients.custom()
                .disableCookieManagement()
                .build();
        Unirest.setHttpClient(httpClient);
        elepy = ElepySystemUnderTest.create();

        elepy.addModel(Resource.class);
        elepy.addModel(CustomUser.class);
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

        User user = new User("admin@admin.com", "admin@admin.com", "admin@admin.com", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(elepy + "/users")
                .body(json(user))
                .asString();

        final HttpResponse<String> response2 = Unirest
                .post(elepy + "/users")
                .body(json(user))
                .asString();


        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response2.getStatus()).isEqualTo(401);
        assertThat(userCrud.count()).isEqualTo(1);

    }

    @Test
    void can_Login_and_FindOtherUsers() throws UnirestException, JsonProcessingException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedFind = Unirest
                .get(elepy + "/users").asString();
        final HttpResponse<String> authorizedFind = Unirest
                .get(elepy + "/users")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .asString();

        assertThat(authorizedFind.getStatus()).isEqualTo(200);
        assertThat(unauthorizedFind.getStatus()).isEqualTo(401);
    }

    @Test
    void can_Login_and_DeleteUsers_AsModerator() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedDelete = Unirest
                .delete(elepy + "/users/user")
                .asString();

        assertThat(unauthorizedDelete.getStatus()).isEqualTo(401);


        final HttpResponse<String> authorizedDelete = Unirest
                .delete(elepy + "/users/user")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .asString();


        final List<? extends User> all1 = userCrud.getAll();
        assertThat(authorizedDelete.getStatus()).isEqualTo(200);

        assertThat(userCrud.count()).isEqualTo(1);

    }

    @Test
    void can_GetToken_and_Login() throws UnirestException {
        elepy.createInitialUserViaHttp("ryan", "susana");


        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(Permissions.AUTHENTICATED);
            context.result(Message.of("Perfect!", 200));
        });
        final var getTokenResponse = Unirest.post(elepy + "/elepy/token-login")
                .basicAuth("ryan", "susana")
                .asString();


        final var token = getTokenResponse.getBody().replaceAll("\"", "");

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route").header("Authorization", "Bearer " + token).asString();

        assertThat(authenticationResponse.getStatus()).isEqualTo(200);
    }

    @Test
    void can_Login_and_UpdateOtherUserRoles() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.AUTHENTICATED));

        final HttpResponse<String> unauthorizedFind = Unirest
                .put(elepy + "/users" + "/user")
                .body(json(userToUpdate))
                .asString();

        final HttpResponse<String> authorizedFind = Unirest
                .put(elepy + "/users" + "/user")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .body(json(userToUpdate))
                .asString();

        final User user = userCrud.getById("user").orElseThrow();

        assertThat(authorizedFind.getStatus()).isEqualTo(200);
        assertThat(unauthorizedFind.getStatus()).isEqualTo(401);
        assertThat(userCrud.count()).isEqualTo(2);
        assertThat(user.getRoles().size()).isEqualTo(1);
        assertThat(user.getRoles().get(0)).isEqualTo(Permissions.AUTHENTICATED);

    }


    @Test
    void cant_Login_and_UpdateSuperUsersPermission() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.SUPER_USER));


        final HttpResponse<String> authorizedFind = Unirest
                .put(elepy + "/users" + "/user")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .body(json(userToUpdate))
                .asString();

        final User user = userCrud.getById("user").orElseThrow();


        assertThat(authorizedFind.getStatus()).isEqualTo(403);
        assertThat(user.getRoles().size()).isEqualTo(0);
    }

    @Test
    void cant_Login_and_CreateSuperUser_afterOneHasBeenCreated() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.SUPER_USER));

        final HttpResponse<String> authorizedFind = Unirest
                .post(elepy + "/users")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .body(json(userToUpdate))
                .asString();

        final User user = userCrud.getById("user").orElseThrow();


        assertThat(authorizedFind.getStatus()).isEqualTo(403);
        assertThat(user.getRoles().size()).isEqualTo(0);
    }

    @Test
    void cant_Login_and_DeleteYourself() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        final HttpResponse<String> authorizedFind = Unirest
                .delete(elepy + "/users" + "/admin@admin.com")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .asString();

        final User user = userCrud.getById("user").orElseThrow();


        assertThat(authorizedFind.getStatus()).isEqualTo(403);
        assertThat(user.getRoles().size()).isEqualTo(0);
    }

    @Test
    void can_Login_and_UpdateYourself_withCustomField() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        final var user1 = new CustomUser();

        user1.setId("user");
        user1.setUsername("user");
        user1.setEmail("email");
        user1.setPassword("newPassword");


        final HttpResponse<String> update = Unirest
                .put(elepy + "/users/user")
                .basicAuth("user", "user")
                .body(json(user1))
                .asString();

        final CustomUser user = ((Crud<CustomUser>) userCrud).getById("user").orElseThrow();


        assertThat(update.getStatus()).isEqualTo(200);

        assertThat(user.getEmail())
                .isEqualTo("email");
        assertThat(BCrypt.checkpw("newPassword", user.getPassword()))
                .isTrue();
    }

    @Test
    void can_Login_and_UpdateOwnPassword_AsSuperUser() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        final HttpResponse<String> authorizedFind = Unirest
                .patch(elepy + "/users" + "/admin@admin.com")
                .queryString("password", "newPassword")
                .basicAuth("admin@admin.com", "admin@admin.com")
                .asString();

        final var admin = userCrud.getById("admin@admin.com").orElseThrow();
        assertThat(authorizedFind.getStatus()).isEqualTo(200);
        assertThat(BCrypt.checkpw("newPassword", admin.getPassword()))
                .isTrue();
    }

    @Test
    void can_AccessExtraRoutes_as_Intended() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources-extra").asString();

        assertThat(getRequest.getStatus()).isEqualTo(201);
        assertThat(getRequest.getBody()).isEqualTo("generated");
    }

    @Test
    void can_AccessActions_as_Intended() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources/actions/extra-action?ids=999,777").asString();

        assertThat(getRequest.getStatus()).isEqualTo(200);
        assertThat(getRequest.getBody()).isEqualTo("[999,777]");
    }

    @Test
    public void can_FindItems_as_Intended() throws IOException, UnirestException {


        final long count = resourceCrud.count();
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources").asString();


        List results = elepy.objectMapper().readValue(getRequest.getBody(), List.class);


        assertThat(getRequest.getStatus()).as(getRequest.getBody()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(count + 1);

    }

    @Test
    public void can_FilterAndSearchItems_AsIntended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources?id_equals=4&uniqueField_contains=filter&numberMax40_equals=25&q=ilterUni").asString();


        List<Resource> results = elepy.objectMapper().readValue(getRequest.getBody(), new TypeReference<List<Resource>>() {
        });


        assertThat(getRequest.getStatus()).as(getRequest.getBody()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(1);

        assertThat(results.get(0).getUniqueField()).isEqualTo("filterUnique");
    }

    @Test
    public void canNot_FindItems_when_QueryDoesntMatch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources?q=ilterUni").asString();


        List<Resource> results = elepy.objectMapper().readValue(getRequest.getBody(), new TypeReference<List<Resource>>() {
        });

        assertThat(getRequest.getStatus()).as(getRequest.getBody()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void can_SearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources?q=testsearchto").asString();


        List<Resource> results = elepy.objectMapper().readValue(getRequest.getBody(), new TypeReference<List<Resource>>() {
        });

        assertThat(getRequest.getStatus()).as(getRequest.getBody()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void can_FindItem_byId() throws IOException, UnirestException {
        resourceCrud.create(validObject());

        final Resource resource = resourceCrud.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get(elepy + "/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.objectMapper().readValue(getRequest.getBody(), Resource.class);

        assertThat(getRequest.getStatus()).as(getRequest.getBody()).isEqualTo(200);
        assertThat(resource.getId()).isEqualTo(foundResource.getId());

    }

    @Test
    public void can_CreateItem() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueCreate");
        final String s = elepy.objectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post(elepy + "/resources").body(s).asString();

        assertThat(postRequest.getStatus()).as(postRequest.getBody()).isEqualTo(201);
        assertThat(resourceCrud.count()).isEqualTo(count + 1);
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

        assertThat(resourceCrud.count()).isEqualTo(count);
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


        assertThat(postRequest.getStatus()).as(postRequest.getBody()).isEqualTo(201);
        assertThat(resourceCrud.count()).isEqualTo(count + 2);
    }

    @Test
    void can_DeleteItem() throws UnirestException {

        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(55);

        resourceCrud.create(resource);

        assertThat(resourceCrud.count()).isEqualTo(beginningCount + 1);
        final HttpResponse<String> delete = Unirest.delete(elepy + "/resources/55").asString();

        assertThat(delete.getStatus()).as(delete.getBody()).isEqualTo(200);
        assertThat(resourceCrud.count()).isEqualTo(beginningCount);

    }

    @Test
    void can_UpdateItemPartially_without_AffectingOtherFields() throws UnirestException {
        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(66);
        resource.setMARKDOWN("ryan");


        resourceCrud.create(resource);

        assertThat(resourceCrud.count()).isEqualTo(beginningCount + 1);
        final HttpResponse<String> patch = Unirest.patch(elepy + "/resources/66").body("{\"id\":" + resource.getId() + ",\"uniqueField\": \"uniqueUpdate\"}").asString();

        assertThat(resourceCrud.count()).isEqualTo(beginningCount + 1);


        Optional<Resource> updatePartialId = resourceCrud.getById(66);


        assertThat(patch.getStatus()).as(patch.getBody()).isEqualTo(200);
        assertThat(updatePartialId.isPresent()).isTrue();
        assertThat(updatePartialId.get().getUniqueField()).isEqualTo("uniqueUpdate");
        assertThat(updatePartialId.get().getMARKDOWN()).isEqualTo("ryan");
        assertThat(patch.getStatus()).as(patch.getBody()).isEqualTo(200);
    }

    @Test
    void can_SortDescending() {
        final Resource resource1 = validObject();
        final Resource resource2 = validObject();

        resource1.setTextField("resource1");
        resource2.setTextField("resource2");


        resourceCrud.create(resource1, resource2);

        final List<Resource> search = resourceCrud.find(create(search(""))
                .limit(2)
                .sort("textField", SortOption.DESCENDING));


        assertThat(search.get(0).getTextField())
                .isEqualTo("resource2");

        assertThat(search.get(1).getTextField())
                .isEqualTo("resource1");

    }

    @Test
    void can_SortAscending() {
        final Resource resource1 = validObject();
        final Resource resource2 = validObject();


        resource2.setTextField("resource2");
        resource1.setTextField("resource1");


        resourceCrud.create(resource1, resource2);

        final List<Resource> search = resourceCrud.find(create(search("")).limit(2).sort("textField", SortOption.ASCENDING));

        assertThat(search.get(0).getTextField())
                .isEqualTo("resource1");
        assertThat(search.get(1).getTextField())
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

        resource.setNonEditable("nonEditable");

        return resource;
    }


    private String json(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    private void createInitialUsersViaHttp() throws JsonProcessingException, UnirestException {
        User user = new User("admin@admin.com", "admin@admin.com", "admin@admin.com", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(elepy + "/users")
                .body(json(user))
                .asString();

        User user2 = new User("user", "user", "user", Collections.emptyList());

        final HttpResponse<String> response2 =
                Unirest.post(elepy + "/users")
                        .basicAuth("admin@admin.com", "admin@admin.com")
                        .body(json(user2))
                        .asString();

        assertThat(response.getStatus()).as(response.getBody()).isEqualTo(200);
        assertThat(response2.getStatus()).as(response2.getBody()).isEqualTo(200);
        assertThat(userCrud.count()).isEqualTo(2);
    }


}
