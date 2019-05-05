package com.elepy.admin;

import com.elepy.Elepy;
import com.elepy.admin.dto.CollectedToken;
import com.elepy.admin.dto.Resource;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.mongo.MongoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fakemongo.Fongo;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenTest {
    private static final int PORT = 7371;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static Elepy elepy;
    private static Crud<Resource> resourceCrud;
    private static Crud<User> userCrud;
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeAll
    public static void beforeAll() throws Exception {
        elepy = new Elepy();
        Fongo fongo = new Fongo("test");

        elepy.addConfiguration(MongoConfiguration.of(fongo.getDB("test")));
        elepy.addModel(Resource.class);
        elepy.onPort(PORT);

        elepy.addExtension(new ElepyAdminPanel());
        elepy.start();
        resourceCrud = elepy.getCrudFor(Resource.class);
        userCrud = elepy.getCrudFor(User.class);
        User defaultUser = new User();

        defaultUser.setUsername("admin");
        defaultUser.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));


        userCrud.create(defaultUser);
    }

    @Test
    void testRetrieveToken() throws UnirestException, IOException {
        HttpResponse<String> response = Unirest.post(BASE_URL + "/elepy-token-login")
                .basicAuth("admin", "admin").asString();

        System.out.println(response.getBody());
        assertEquals(200, response.getStatus());
        CollectedToken token = objectMapper.readValue(response.getBody(), CollectedToken.class);

        HttpResponse<String> authResponse = Unirest.get(BASE_URL + "/elepy-login-check").header("ELEPY_TOKEN", token.getId()).asString();

        assertEquals(200, authResponse.getStatus());

    }


}
