package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.DefaultObjectUpdateEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.UpdateHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;

public class UserUpdate implements UpdateHandler<User> {

    @Override
    public void handleUpdatePut(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {


        User loggedInUser = context.loggedInUserOrThrow();
        User userToUpdateAfter = objectMapper.readValue(context.body(), modelContext.getModelType());


        // You can only execute this if the updating user is yourself, or you can administrate users
        if (!userToUpdateAfter.equals(loggedInUser)) {
            context.requirePermissions(Permissions.CAN_ADMINISTRATE_USERS);
        }
        User userToUpdateBefore = crud.getById(crud.getId(userToUpdateAfter)).orElseThrow(() -> new ElepyException("No user found with this ID", 404));

        checkPermissionIntegrity(loggedInUser, userToUpdateAfter, userToUpdateBefore);

        //Elepy evaluation
        new DefaultObjectUpdateEvaluator<>().evaluate(userToUpdateBefore, userToUpdateAfter);

        for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(userToUpdateAfter);
        }
        new DefaultIntegrityEvaluator<>(modelContext).evaluate(userToUpdateAfter, EvaluationType.UPDATE);

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
    }

    private void checkPermissionIntegrity(User loggedInUser, User userToUpdate, User userBeforeUpdate) {
        if (loggedInUser.equals(userToUpdate) &&
                !permissionsAreTheSame(loggedInUser, userToUpdate)) {
            throw new ElepyException("Can't update your own permissions", 403);
        }

        boolean updatedPermissionsContainsSuperUser = (userBeforeUpdate.getPermissions().contains(Permissions.SUPER_USER) ||
                userToUpdate.getPermissions().contains(Permissions.SUPER_USER));

        if (updatedPermissionsContainsSuperUser &&
                !permissionsAreTheSame(userToUpdate, userBeforeUpdate)) {
            throw new ElepyException(String.format("Can't add or remove '%s' permission to or from users", Permissions.SUPER_USER), 403);
        }
    }

    private boolean permissionsAreTheSame(User user1, User user2) {
        return new HashSet<>(user1.getPermissions()).equals(new HashSet<>(user2.getPermissions()));
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {
        handleUpdatePut(context, crud, modelContext, objectMapper);
    }
}
