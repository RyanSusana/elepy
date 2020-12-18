package com.elepy.auth.users;

import com.elepy.annotations.Inject;
import com.elepy.auth.Permissions;
import com.elepy.auth.Policy;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.DefaultObjectUpdateEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.HandlerContext;
import com.elepy.handlers.DefaultUpdate;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;

public class UserUpdate extends DefaultUpdate<User> {

    @Inject
    private Policy policy;

    @Override
    public User handleUpdate(HandlerContext<User> ctx, ObjectMapper objectMapper) throws Exception {
        final var context = ctx.http();
        Crud<User> crud = ctx.crud();
        User loggedInUser = context.loggedInUserOrThrow();

        User userToUpdateBefore = crud.getById(context.recordId()).orElseThrow(() -> new ElepyException("No user found with this ID", 404));

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
            userToUpdateAfter.setPassword(BCrypt.hashpw(userToUpdateAfter.getPassword(), BCrypt.gensalt()));
        }

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

        for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(userToUpdateAfter);
        }
        new DefaultIntegrityEvaluator<>(modelContext).evaluate(userToUpdateAfter, EvaluationType.UPDATE);
    }

    private void checkPermissionIntegrity(User loggedInUser, User userToUpdate, User userBeforeUpdate) {
        if (loggedInUser.equals(userToUpdate) &&
                !rolesAreTheSame(loggedInUser, userToUpdate)) {
            throw new ElepyException("Can't update your own permissions", 403);
        }

        boolean updatedPermissionsContainsSuperUser = (policy.userHasRole(userBeforeUpdate, "owner") ||
                policy.userHasRole(userToUpdate, "owner"));

        if (updatedPermissionsContainsSuperUser &&
                !rolesAreTheSame(userToUpdate, userBeforeUpdate)) {
            throw new ElepyException(String.format("Can't edit the permissions of users that have the permission '%s'", Permissions.SUPER_USER), 403);
        }
    }

    private boolean rolesAreTheSame(User user1, User user2) {
        return new HashSet<>(user1.getRoles()).equals(new HashSet<>(user2.getRoles()));
    }
}
