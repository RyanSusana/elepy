package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.CreateHandler;
import com.elepy.http.HttpContext;
import com.elepy.id.HexIdentityProvider;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;


public class UserCreate implements CreateHandler<User> {

    @Override
    public synchronized void handleCreate(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {

        String body = context.request().body();
        User user = objectMapper.readValue(body, crud.getType());
        if (user.getUsername().trim().isEmpty()) {
            throw new ElepyException("Usernames can't be empty!", 400);
        }
        if (crud.count() > 0) {
            context.loggedInUserOrThrow();
            context.requirePermissions(Permissions.CAN_ADMINISTRATE_USERS);

            if (user.getPermissions().contains(Permissions.SUPER_USER)) {
                throw new ElepyException(String.format("Can't create users with the permission '%s'", Permissions.SUPER_USER), 403);
            }
            context.validate(user);
            evaluateUser(modelContext, user);


            createUser(crud, user);
            context.response().result(Message.of("Successfully created user", 200));
        } else {
            evaluateUser(modelContext, user);

            user.getPermissions().add(Permissions.SUPER_USER);

            if (user.getPassword().length() < 5) {
                throw new ElepyException("Passwords must be more than 4 characters long!", 400);
            }

            createUser(crud, user);
            context.response().result();
            context.response().result(Message.of("Successfully created the user", 200));

        }
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
