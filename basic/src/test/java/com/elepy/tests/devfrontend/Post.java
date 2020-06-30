package com.elepy.tests.devfrontend;

import com.elepy.annotations.*;
import com.elepy.http.HttpMethod;


@PredefinedRole(id = "postmaster", name = "Postmaster", permissions = "post.*")
@Model(name = "Posts", path = "/posts")
@Delete(requiredPermissions = "post.delete")
@Create(requiredPermissions = "post.create")
@Update(requiredPermissions = "post.update")
@Action(name = "Make copies with random rating", input = PostActionInput.class, handler = ActionPost.class, method = HttpMethod.POST, singleRecord = false, multipleRecords = false)
@Action(name = "Only admins can see this action", input = PostActionInput.class, handler = ActionPost.class, requiredPermissions = "post.copy")
public class Post {
    private String id;

    @EditorJs
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
