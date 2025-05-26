package com.elepy.auth.users;

import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.crud.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpContext;
import com.elepy.id.HexIdentityProvider;
import com.elepy.igniters.ModelDetails;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;


@ApplicationScoped
public class UserCreate implements ActionHandler<User> {


    @Override
    public void handle(HandlerContext<User> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        String body = context.request().body();

        final var crud = ctx.crud();
        User user = context.elepy().objectMapper().readValue(body, crud.getType());

        context.validate(user, PasswordCheck.class, jakarta.validation.groups.Default.class);

        if (crud.count() > 0) {
            createAdditionalUser(modelContext, context, crud, user);
        } else {
            createInitialUser(context, crud, user, context.elepy().getCrudFor(PolicyBinding.class));
        }
    }

    protected void createInitialUser(HttpContext context, Crud<User> crud, User user, Crud<PolicyBinding> policies) {

        createUser(crud, user);
        var policyBinding = new PolicyBinding();
        policyBinding.setId(UUID.randomUUID().toString());
        policyBinding.setPrincipal(user.getId());
        policyBinding.setRole("roles/admin");
        policyBinding.setTarget("/");

        policies.create(policyBinding);
        context.response().result(Message.of("Successfully created the user", 200));
    }

    protected void createAdditionalUser(ModelDetails<User> modelDetails, HttpContext context, Crud<User> crud, User user) throws Exception {
        context.loggedInUserOrThrow();
        // TODO
//        context.requirePermissions("users.create");

        evaluateIntegrity(modelDetails, user);

        createUser(crud, user);
        context.response().result(Message.of("Successfully created user", 200).withProperty("createdRecords", List.of(user)));
    }

    private void evaluateIntegrity(ModelDetails<User> modelDetails, User user) {
        new DefaultIntegrityEvaluator<>(modelDetails).evaluate(user, EvaluationType.CREATE);
    }

    private void createUser(Crud<User> crud, User user) {
        user.cleanUsername();
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        //This line didn't exist before for some reason it got deleted
        new HexIdentityProvider().provideId(user, crud);

        crud.create(user);
    }
}
