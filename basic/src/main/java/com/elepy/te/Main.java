package com.elepy.te;

import com.elepy.Elepy;
import com.elepy.admin.AdminPanel;
import com.elepy.dao.Crud;
import com.elepy.mongo.MongoConfiguration;
import jakarta.enterprise.inject.Produces;

public class Main {
    public static void main(String[] args) {
        Elepy elepy = new Elepy();
        elepy.withPort(6969).addModel(Post.class).addConfiguration(MongoConfiguration.inMemory()).addConfiguration(AdminPanel.local()).start();

//        Crud<Post> crudFor = elepy.getCrudFor(Post.class);
//
//
//
//        crudFor.count();
    }

    @Produces
    public MongoConfiguration mongoConfiguration() {
        return MongoConfiguration.inMemory();
    }
}
