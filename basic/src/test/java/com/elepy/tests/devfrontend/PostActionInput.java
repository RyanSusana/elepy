package com.elepy.tests.devfrontend;

import com.elepy.annotations.Reference;

public class PostActionInput {


    @Reference(to = Post.class)
    private String postId;
    private Integer amount;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
