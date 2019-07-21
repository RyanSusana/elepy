package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.CreateHandler;
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

            for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
                objectEvaluator.evaluate(user, User.class);
            }
            new DefaultIntegrityEvaluator<User>().evaluate(user, crud, true);

            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            crud.create(user);
            context.response().result(Message.of("Successfully created user", 200));
        } else {
            for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
                objectEvaluator.evaluate(user, User.class);
            }
            new DefaultIntegrityEvaluator<User>().evaluate(user, crud, true);


            user.getPermissions().add(Permissions.SUPER_USER);

            if (user.getPassword().length() < 5) {
                throw new ElepyException("Passwords must be more than 4 characters long!", 400);
            }

            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            crud.create(user);
            context.response().result();
            context.response().result(Message.of("Successfully created the user", 200));

        }
    }
}
