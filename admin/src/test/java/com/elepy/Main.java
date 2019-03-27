package com.elepy;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.dto.Resource;
import com.elepy.admin.models.User;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;

public class Main {
    public static void main(String[] args) {
        Fongo fongo = new Fongo("examples");
        DB exampleDB = fongo.getDB("example1");

        Elepy elepy = new Elepy()
                //Attach a singleton to Elepy. To use Mongo with Elepy, you need to attach a DB
                .registerDependency(DB.class, exampleDB)

                //Run locally(this is also the default)
                .withIPAdress("localhost")

                //The port Elepy will run on.
                .onPort(7777)

                //Add your first Elepy Annotated Model. You can alternatively add an entire package of models.
                .addModel(Resource.class)

                //Add an Elepy extension. The AdminPanel/CMS is a great start :D
                .addExtension(new ElepyAdminPanel());

        User defaultUser = new User();

        defaultUser.setUsername("admin");
        defaultUser.setPassword("admin");


        elepy.start();

       // elepy.getCrudFor(User.class).create(defaultUser.hashWord());


    }
}
