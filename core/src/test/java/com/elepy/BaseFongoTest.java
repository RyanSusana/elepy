package com.elepy;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.io.IOException;

public class BaseFongoTest extends BaseTest {


    private MongoClient _mongo;
    private int port;
    private Fongo fongo;

    public void setUp() throws Exception {


        fongo = new Fongo("test");


    }

    protected void tearDown() throws Exception {

    }


    public DB getDb() throws IOException {


        return fongo.getDB("test");
    }
}
