package com.elepy.auth.users;

import com.elepy.crud.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.DefaultObjectUpdateEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.DefaultUpdate;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpContext;
import com.elepy.igniters.ModelDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserUpdate extends DefaultUpdate<User> {


    @Override
    public User handleUpdate(HandlerContext<User> ctx, ObjectMapper objectMapper) throws Exception {
        final var context = ctx.http();
        Crud<User> crud = ctx.crud();
        User loggedInUser = context.request().loggedInUserOrThrow();

        User userToUpdateBefore = crud.getById(context.recordId()).orElseThrow(() -> ElepyException.notFound("User"));

        User userToUpdateAfter = updatedObjectFromRequest(userToUpdateBefore, context.request(), objectMapper, ctx.model().getSchema());

// TODO
        // You can only execute this if the updating user is yourself, or you can administrate users
        if (!userToUpdateAfter.equals(loggedInUser)) {
        }

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

    protected void validateUpdate(HttpContext context, ModelDetails<User> modelDetails, User userToUpdateBefore, User userToUpdateAfter) throws Exception {
        context.validate(userToUpdateAfter);
        //Elepy evaluation
        new DefaultObjectUpdateEvaluator<>().evaluate(userToUpdateBefore, userToUpdateAfter);

        new DefaultIntegrityEvaluator<>(modelDetails.getCrud()).evaluate(userToUpdateAfter, EvaluationType.UPDATE);
    }


}
