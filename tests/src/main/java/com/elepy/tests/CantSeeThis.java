package com.elepy.tests;

import com.elepy.annotations.Hidden;
import com.elepy.annotations.RestModel;

@RestModel(slug = "/no-way-josay", name = "Should not be able to see this message")
@Hidden
public class CantSeeThis {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
