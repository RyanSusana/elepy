package com.ryansusana.elepy.admin.services;

import com.ryansusana.elepy.admin.models.User;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.models.RestErrorMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserEvaluator implements ObjectEvaluator<User> {
    @Override
    public void evaluate(User user) {
        String username = user.getUsername();

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher("I am a string");

        if (username.contains(" ") || m.find()) {
            throw new RestErrorMessage("Usernames can not contain spaces or special characters!");
        }

    }
}
