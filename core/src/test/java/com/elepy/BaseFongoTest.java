package com.elepy;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

import java.io.IOException;

public class BaseFongoTest extends BaseTest {
    /**
     * please store Starter or RuntimeConfig in a static final field
     * if you want to use artifact store caching (or else disable caching)
     */


    private MongoClient _mongo;
    private int port;
    private Fongo fongo ;

    public void setUp() throws Exception {


        fongo = new Fongo("test");




    }

    protected void tearDown() throws Exception {

    }


    public DB getDb() throws IOException {


        return fongo.getDB("test");
    }
}
