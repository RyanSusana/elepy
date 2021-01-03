package com.elepy.auth.users;

import com.elepy.annotations.Inject;
import com.elepy.auth.Policy;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpContext;
import com.elepy.id.HexIdentityProvider;
import com.elepy.models.ModelContext;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;


public class UserCreate implements ActionHandler<User> {

    @Inject
    private Policy policy;

    @Override
    public void handle(HandlerContext<User> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        String body = context.request().body();

        final var crud = ctx.crud();
        User user = context.elepy().objectMapper().readValue(body, crud.getType());

        context.validate(user);

        if (crud.count() > 0) {
            createAdditionalUser(modelContext, context, crud, user);
        } else {
            createInitialUser(context, crud, user);
        }
    }

    protected void createInitialUser(HttpContext context, Crud<User> crud, User user) {
        user.getRoles().add("owner");

        createUser(crud, user);
        context.response().result(Message.of("Successfully created the user", 200).withProperty("createdRecords", List.of(user)));
    }

    protected void createAdditionalUser(ModelContext<User> modelContext, HttpContext context, Crud<User> crud, User user) throws Exception {
        context.loggedInUserOrThrow();
        context.requirePermissions("users.create");

        if (policy.userHasRole(user, "owner")) {
            throw ElepyException.translated(403, "{elepy.models.users.exceptions.owner}");
        }
        evaluateIntegrity(modelContext, user);

        createUser(crud, user);
        context.response().result(Message.of("Successfully created user", 200).withProperty("createdRecords", List.of(user)));
    }

    private void evaluateIntegrity(ModelContext<User> modelContext, User user) {
        new DefaultIntegrityEvaluator<>(modelContext).evaluate(user, EvaluationType.CREATE);
    }

    private void createUser(Crud<User> crud, User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        //This line didn't exist before for some reason it got deleted
        new HexIdentityProvider<User>().provideId(user, crud);

        crud.create(user);
    }
}
