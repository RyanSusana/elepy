package com.elepy.tests.devfrontend;

import com.elepy.annotations.*;
import com.elepy.http.HttpMethod;

@Model(name = "Posts", path = "/posts")

@Action(name = "Make copies with random rating", input = PostActionInput.class, handler = ActionPost.class, method = HttpMethod.POST, singleRecord = false, multipleRecords = false)
public class Post {
    private String id;

    @TextArea
    @Searchable
    @Featured
    private String content;


    private int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
