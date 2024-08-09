package com.elepy.auth.users;

import com.elepy.auth.roles.RolesService;
import com.elepy.crud.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.DefaultObjectUpdateEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.DefaultUpdate;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpContext;
import com.elepy.igniters.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;

public class UserUpdate extends DefaultUpdate<User> {

    @Inject
    private RolesService policy;

    @Override
    public User handleUpdate(HandlerContext<User> ctx, ObjectMapper objectMapper) throws Exception {
        final var context = ctx.http();
        Crud<User> crud = ctx.crud();
        User loggedInUser = context.loggedInUserOrThrow();

        User userToUpdateBefore = crud.getById(context.recordId()).orElseThrow(() -> ElepyException.notFound("User"));

        User userToUpdateAfter = updatedObjectFromRequest(userToUpdateBefore, context.request(), objectMapper, ctx.model().getSchema());

        // You can only execute this if the updating user is yourself, or you can administrate users
        if (!userToUpdateAfter.equals(loggedInUser)) {
            context.requirePermissions("users.update");
        }
        checkPermissionIntegrity(loggedInUser, userToUpdateAfter, userToUpdateBefore);

        validateUpdate(context, ctx.model(), userToUpdateBefore, userToUpdateAfter);

        //If password is empty, use the old password
        if (userToUpdateAfter.getPassword().isEmpty()) {
            userToUpdateAfter.setPassword(userToUpdateBefore.getPassword());
        }

        //Encrypt password if changed
        if (!userToUpdateAfter.getPassword().equals(userToUpdateBefore.getPassword())) {
            context.validate(userToUpdateAfter, PasswordCheck.class, jakarta.validation.groups.Default.class);
            userToUpdateAfter.setPassword(BCrypt.hashpw(userToUpdateAfter.getPassword(), BCrypt.gensalt()));
        } else {
            context.validate(userToUpdateAfter);
        }

        userToUpdateAfter.cleanUsername();
        // Finalize update and respond
        crud.update(userToUpdateAfter);

        context.status(200);
        context.result(Message.of("The user has been updated", 200));
        return userToUpdateAfter;
    }

    protected void validateUpdate(HttpContext context, ModelContext<User> modelContext, User userToUpdateBefore, User userToUpdateAfter) throws Exception {
        context.validate(userToUpdateAfter);
        //Elepy evaluation
        new DefaultObjectUpdateEvaluator<>().evaluate(userToUpdateBefore, userToUpdateAfter);

        new DefaultIntegrityEvaluator<>(modelContext).evaluate(userToUpdateAfter, EvaluationType.UPDATE);
    }

    private void checkPermissionIntegrity(User loggedInUser, User userToUpdate, User userBeforeUpdate) {
        if (loggedInUser.equals(userToUpdate) &&
            !rolesAreTheSame(loggedInUser, userToUpdate)) {
            throw ElepyException.translated(403, "{elepy.models.users.exceptions.cantUpdateSelf}");
        }

        boolean updatedPermissionsContainsSuperUser = (policy.userIsOwner(userBeforeUpdate) ||
                                                       policy.userIsOwner(userToUpdate));

        if (updatedPermissionsContainsSuperUser &&
            !rolesAreTheSame(userToUpdate, userBeforeUpdate)) {
            throw ElepyException.translated(403, "{elepy.models.users.exceptions.owner}");
        }
    }

    private boolean rolesAreTheSame(User user1, User user2) {
        return new HashSet<>(user1.getRoles()).equals(new HashSet<>(user2.getRoles()));
    }
}
