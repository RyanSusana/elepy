package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.DefaultObjectUpdateEvaluator;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.UpdateHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;

public class UserUpdate implements UpdateHandler<User> {

    @Override
    public void handleUpdatePut(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {

        //Make sure that you can administrate users
        context.requirePermissions(Permissions.CAN_ADMINISTRATE_USERS);

        User loggedInUser = context.loggedInUserOrThrow();
        User userToUpdate = objectMapper.readValue(context.body(), modelContext.getModelType());
        User userToUpdateBefore = crud.getById(crud.getId(userToUpdate)).orElseThrow(() -> new ElepyException("No user found with this ID", 404));

        checkPermissionIntegrity(loggedInUser, userToUpdate, userToUpdateBefore);

        //Elepy evaluation
        new DefaultObjectUpdateEvaluator<>().evaluate(userToUpdateBefore, userToUpdate);

        for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(userToUpdate);
        }
        new DefaultIntegrityEvaluator<>(modelContext).evaluate(userToUpdate);

        //If password is empty, use the old password
        if (userToUpdate.getPassword().isEmpty()) {
            userToUpdate.setPassword(userToUpdateBefore.getPassword());
        }

        //Encrypt password if changed
        if (!userToUpdate.getPassword().equals(userToUpdateBefore.getPassword())) {
            userToUpdate.setPassword(BCrypt.hashpw(userToUpdate.getPassword(), BCrypt.gensalt()));
        }

        // Finalize update and respond
        crud.update(userToUpdate);

        context.status(200);
        context.result(Message.of("The user has been updated", 200));
    }

    private void checkPermissionIntegrity(User loggedInUser, User userToUpdate, User userBeforeUpdate) {
        if (loggedInUser.getId().equals(userToUpdate.getId()) &&
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
