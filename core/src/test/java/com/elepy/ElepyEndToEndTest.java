package com.elepy;

import com.elepy.concepts.Resource;
import com.elepy.dao.Page;
import com.elepy.dao.jongo.MongoDao;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.DB;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ElepyEndToEndTest extends BaseFongoTest {

    private Elepy elepy;
    private MongoDao<Resource> mongoDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        elepy = new Elepy();


        elepy.attachSingleton(DB.class, getDb());

        mongoDao = new MongoDao<>(getDb(), "resources", Resource.class);

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

        assertEquals(count+1, resourcePage.getValues().size());
        elepy.stop();
    }

    @Test
    public void testFindOne() throws IOException, UnirestException {


        mongoDao.create(validObject());

        final Resource resource = mongoDao.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get("http://localhost:7357/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        elepy.stop();

    }
}
