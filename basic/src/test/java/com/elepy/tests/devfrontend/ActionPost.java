package com.elepy.tests.devfrontend;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionPost implements ActionHandler<Post> {

    private static int counter = 1;

    @Override
    public void handle(HandlerContext<Post> ctx) throws Exception {
        final var postActionInput = ctx.http().request().inputAs(PostActionInput.class);

        final var post = ctx.crud().getById(postActionInput.getPostId()).orElseThrow();


        final var newPosts = IntStream.range(1, postActionInput.getAmount()).mapToObj(i -> {
            final var postCopy = new Post();
            postCopy.setId(post.getId() + counter);
            postCopy.setRating(new Random().nextInt(1000));
            postCopy.setContent(post.getContent() + " copy " + counter);
            counter++;

            return postCopy;
        }).collect(Collectors.toList());

        ctx.crud().create(newPosts);
    }
}
