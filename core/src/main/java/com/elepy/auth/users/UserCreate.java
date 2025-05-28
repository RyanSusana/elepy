package com.elepy.auth.users;

import com.elepy.auth.authentication.AuthenticationService;
import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authorization.AuthorizationResult;
import com.elepy.auth.authorization.AuthorizationService;
import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.crud.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpContext;
import com.elepy.id.HexIdentityProvider;
import com.elepy.igniters.ModelDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@ApplicationScoped
public class UserCreate implements ActionHandler<User> {

    @Inject
    private UserService userService;

    @Override
    public void handle(HandlerContext<User> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        String body = context.request().body();

        final var crud = ctx.crud();
        User user = context.elepy().objectMapper().readValue(body, crud.getType());
        context.validate(user, PasswordCheck.class, jakarta.validation.groups.Default.class);
        var credentials = context.request().loggedInCredentials().orElse(null);

        userService.createUser(credentials, user);

        context.response().result(Message.of("Successfully created user", 200)
                .withProperty("createdRecords", List.of(user.withEmptyPassword())));

    }


}
