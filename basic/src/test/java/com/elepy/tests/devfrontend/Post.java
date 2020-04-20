package com.elepy.tests.devfrontend;

import com.elepy.annotations.Action;
import com.elepy.annotations.HTML;
import com.elepy.annotations.Model;
import com.elepy.annotations.Searchable;
import com.elepy.http.HttpMethod;

@Model(name = "Posts", path = "/posts")

@Action(name = "Action post", input = PostActionInput.class, handler = ActionPost.class, method = HttpMethod.POST)
public class Post {
    private String id;

    @HTML
    @Searchable
    private String content;

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
