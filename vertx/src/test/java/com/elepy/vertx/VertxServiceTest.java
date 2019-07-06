package com.elepy.vertx;

import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.exceptions.ElepyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VertxServiceTest {


    private VertxService service;
    private HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() {
        service = new VertxService();
        service.port(3030);


    }

    @AfterEach
    void tearDown() {
        service.stop();
    }

    @Test
    void can_StartService_without_Exception() {
        assertDoesNotThrow(service::ignite);
    }

    @Test
    void can_handleGET() {
        service.get("/test", (request, response) -> response.result("hi"));


        service.ignite();
        assertResponseReturns("get", "/test", "hi");
    }

    @Test
    void can_handlePOST() {
        service.post("/test", (request, response) -> response.result("hi"));

        service.ignite();
        assertResponseReturns("post", "/test", "hi");
    }

    @Test
    void can_handlePUT() {
        service.put("/test", ctx -> ctx.result("hi"));

        service.ignite();
        assertResponseReturns("put", "/test", "hi");
    }

    @Test
    void can_handlePATCH() {
        service.patch("/test", ctx -> ctx.result("hi"));

        service.ignite();
        assertResponseReturns("patch", "/test", "hi");
    }


    @Test
    void can_handleDELETE() {
        service.delete("/test", ctx -> ctx.result("hi"));

        service.ignite();
        assertResponseReturns("delete", "/test", "hi");
    }

    @Test
    void can_handleMultipleRoutes() {
        service.get("/testGET", (request, response) -> response.result("hiGET"));
        service.post("/testPOST", (request, response) -> response.result("hiPOST"));
        service.put("/testPUT", ctx -> ctx.result("hiPUT"));
        service.patch("/testPATCH", ctx -> ctx.result("hiPATCH"));
        service.delete("/testDELETE", ctx -> ctx.result("hiDELETE"));

        service.ignite();

        assertResponseReturns("delete", "/testDELETE", "hiDELETE");
        assertResponseReturns("patch", "/testPATCH", "hiPATCH");
        assertResponseReturns("put", "/testPUT", "hiPUT");
        assertResponseReturns("post", "/testPOST", "hiPOST");
        assertResponseReturns("get", "/testGET", "hiGET");

    }

    @Test
    void can_handleException_inRoute() throws IOException, InterruptedException {
        service.get("/testException", (request, response) -> {
            response.result("Exception not handled");
            throw new ElepyException("Exception handled", 400);
        });

        service.exception(ElepyException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(e.getStatus());
        });
        service.ignite();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3030/testException")).GET().build();

        final HttpResponse<String> send = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Exception handled", send.body());
        assertEquals(400, send.statusCode());
    }

    @Test
    void can_handleException_inBefore() throws IOException, InterruptedException {

        service.before(context -> {
            throw new ElepyException("Exception handled", 400);
        });

        service.get("/testException2", (request, response) -> {
            response.result("Exception not handled");
        });

        service.exception(ElepyErrorMessage.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(e.getStatus());
        });
        service.ignite();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3030/testException2")).GET().build();

        final HttpResponse<String> send = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Exception handled", send.body());
        assertEquals(400, send.statusCode());
    }

    @Test
    void can_handleException_inAfter() throws IOException, InterruptedException {

        service.after(context -> {
            throw new ElepyException("Exception handled", 400);
        });

        service.get("/testException2", (request, response) -> {
            response.result("Exception not handled");
        });

        service.exception(ElepyErrorMessage.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(e.getStatus());
        });
        service.ignite();

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3030/testException2")).GET().build();

        final HttpResponse<String> send = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Exception handled", send.body());
        assertEquals(400, send.statusCode());
    }


    private void assertResponseReturns(String method, String path, String expectedResult) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:3030%s", path)))
                .method(method.toUpperCase(), HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            final HttpResponse<String> send = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(expectedResult, send.body());
            assertEquals(200, send.statusCode());
        } catch (IOException e) {
            throw new AssertionError("network error", e);
        } catch (InterruptedException e) {
            throw new AssertionError("thread error", e);
        }
    }
}
