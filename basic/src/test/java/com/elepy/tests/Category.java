package com.elepy.tests;

import com.elepy.annotations.Featured;
import com.elepy.annotations.Model;
import com.elepy.annotations.Searchable;

@Model(name = "Categories", path = "/categories")
public class Category {

    private String id;

    @Featured
    @Searchable
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
