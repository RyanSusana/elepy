package com.elepy;

import com.elepy.concepts.Resource;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.DB;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ElepyEndToEndTest extends BaseFongoTest {

    Elepy elepy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        elepy = new Elepy();


        elepy.attachSingleton(DB.class, getDb());


        elepy.addModel(Resource.class);

        elepy.onPort(1337);

        elepy.start();
    }

    @Test
    public void testCreate() throws IOException, UnirestException {


    }
}
