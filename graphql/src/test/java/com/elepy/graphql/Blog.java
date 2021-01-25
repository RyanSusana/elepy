package com.elepy.graphql;

import com.elepy.annotations.Model;

@Model(name = "Blogs", path = "/blogs")
public class Blog {
    private String id;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
