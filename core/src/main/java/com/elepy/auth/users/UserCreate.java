package com.elepy.auth.users;

import com.elepy.annotations.Inject;
import com.elepy.auth.Policy;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.id.HexIdentityProvider;
import com.elepy.models.ModelContext;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;


public class UserCreate implements ActionHandler<User> {

    @Inject
    private Policy policy;

    @Override
    public synchronized void handle(HttpContext context, ModelContext<User> modelContext) throws Exception {

        String body = context.request().body();

        final var crud = modelContext.getCrud();
        User user = context.elepy().objectMapper().readValue(body, crud.getType());
        if (user.getUsername().trim().isEmpty()) {
            throw new ElepyException("Usernames can't be empty!", 400);
        }
        if (crud.count() > 0) {
            createAdditionalUser(context, crud, modelContext, user);
        } else {
            createInitialUser(context, crud, modelContext, user);
        }
    }

    protected void createInitialUser(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, User user) throws Exception {
        evaluateUser(modelContext, user);

        user.getRoles().add("owner");

        if (user.getPassword().length() < 5) {
            throw new ElepyException("Passwords must be more than 4 characters long!", 400);
        }

        createUser(crud, user);
        context.response().result(Message.of("Successfully created the user", 200).withProperty("createdRecords", List.of(user.getId())));
    }

    protected void createAdditionalUser(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, User user) throws Exception {
        context.loggedInUserOrThrow();
        context.requirePermissions("users.create");

        if (policy.userHasRole(user, "owner")) {
            throw new ElepyException("Can't create users with the owner role", 403);
        }
        context.validate(user);
        evaluateUser(modelContext, user);


        createUser(crud, user);
        context.response().result(Message.of("Successfully created user", 200).withProperty("createdRecords", List.of(user.getId())));
    }

    private void evaluateUser(ModelContext<User> modelContext, User user) throws Exception {
        for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(user);
        }
        new DefaultIntegrityEvaluator<>(modelContext).evaluate(user, EvaluationType.CREATE);
    }

    private void createUser(Crud<User> crud, User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        //This line didn't exist before for some reason it got deleted
        new HexIdentityProvider<User>().provideId(user, crud);


        crud.create(user);
    }
}
