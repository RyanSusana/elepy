package com.elepy;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

public class BaseFongoTest extends BaseTest {
    public DB getDb() {
        Fongo fongo = new Fongo("mongo server");

        return fongo.getDB("elepytestdb");
    }
}
