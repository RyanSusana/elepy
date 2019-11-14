package com.elepy.mongo.fast;

import java.math.BigDecimal;

public class Base {

    private static int counter = -100;

    public synchronized Resource validObject() {
        Resource resource = new Resource();

        resource.setId(counter++);
        resource.setMaxLen40("230428");
        resource.setMinLen20("My name is ryan and this is a string  with more than 20 chars");
        resource.setMinLen10MaxLen50("12345678910111213");
        resource.setNumberMax40(BigDecimal.valueOf(40));
        resource.setNumberMin20(BigDecimal.valueOf(20));
        resource.setNumberMin10Max50(BigDecimal.valueOf(15));
        resource.setUnique("unique");
        resource.setMARKDOWN("MARKDOWN");
        resource.setTextArea("textarea");
        resource.setTextField("textfield");
        resource.setSearchableField("searchable");
        resource.setRequired("required");

        resource.setNonEditable("nonEditable");

        return resource;
    }

}
