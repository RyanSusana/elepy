package com.elepy.mongo;

import com.elepy.Elepy;
import com.elepy.auth.authorization.PolicyBinding;

public class Ok {

    public static void main(String[] args) {
        var elepy = new Elepy();

        elepy.addConfiguration(MongoConfiguration.inMemory());

        elepy.start();

        var policies = elepy.getCrudFor(PolicyBinding.class);

    }

    void name() {
    }
}
