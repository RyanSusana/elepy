package com.elepy.tests.devfrontend;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionPost implements ActionHandler<Post> {

    private static int counter = 1;

    @Override
    public void handle(HttpContext context, ModelContext<Post> modelContext) throws Exception {
        final var postActionInput = context.request().inputAs(PostActionInput.class);

        final var post = modelContext.getCrud().getById(postActionInput.getPostId()).orElseThrow();


        final var newPosts = IntStream.range(1, postActionInput.getAmount()).mapToObj(i -> {
            final var postCopy = new Post();
            postCopy.setId(post.getId() + counter);
            postCopy.setRating(new Random().nextInt(1000));
            postCopy.setContent(post.getContent() + " copy " + counter);
            counter++;

            return postCopy;
        }).collect(Collectors.toList());

        modelContext.getCrud().create(newPosts);
    }
}
