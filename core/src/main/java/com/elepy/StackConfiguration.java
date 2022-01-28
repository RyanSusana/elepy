package com.elepy;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class StackConfiguration {
    private static final Map<String, String> shortcuts =
            Map.of(
                    "mongo", "com.elepy.mongo.MongoConfiguration",
                    "hibernate", "com.elepy.hibernate.HibernateConfiguration",
                    "firebase", "com.elepy.firebase.FirebaseConfiguration",
                    "jwt", "com.elepy.jwt.JWTConfiguration",
                    "cms", "com.elepy.admin.AdminPanel"
            );

    public static void configureStack(Elepy elepy) {
        final var stack = elepy.getPropertyConfig().getStringArray("stack");


        if (stack != null) {
            Stream.of(stack).map(s -> toConfiguration(elepy, s)).forEach(elepy::addConfiguration);
        }
    }

    private static Configuration toConfiguration(Elepy elepy, String s) {
        final var className = Optional.ofNullable(shortcuts.get(s.toLowerCase())).orElse(s);
        return elepy.initialize(className);
    }
} 
