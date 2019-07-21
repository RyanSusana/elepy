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

public class UserUpdate implements UpdateHandler<User> {

    @Override
    public void handleUpdatePut(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {

        context.requirePermissions(Permissions.CAN_ADMINISTRATE_USERS);
        User loggedInUser = context.loggedInUserOrThrow();
        User updated = objectMapper.readValue(context.body(), modelContext.getModelType());


        User before = crud.getById(crud.getId(updated)).orElseThrow(() -> new ElepyException("No object found with this ID", 404));


        DefaultObjectUpdateEvaluator<User> updateEvaluator = new DefaultObjectUpdateEvaluator<>();

        updateEvaluator.evaluate(before, updated);

        for (ObjectEvaluator<User> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(updated, User.class);
        }
        new DefaultIntegrityEvaluator<User>().evaluate(updated, crud);

        if (updated.getPassword().isEmpty()) {
            updated.setPassword(before.getPassword());
        }
        if (!updated.getPassword().equals(before.getPassword())) {
            updated.setPassword(BCrypt.hashpw(updated.getPassword(), BCrypt.gensalt()));
        }


        crud.update(updated);
        context.response().status(200);
        context.response().result(Message.of("The user has been updated", 200));
    }

    private void checkLogin(User loggedIn, User toUpdate) {

        //loggedIn.getPermissions().con
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {
        handleUpdatePut(context, crud, modelContext, objectMapper);
    }
}
