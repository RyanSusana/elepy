package com.elepy.te;

import com.elepy.annotations.Model;

@Model(name = "Posts", path = "/posts")
public class Post {
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
