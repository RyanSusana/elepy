package com.elepy.auth.methods;

import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.http.Request;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class BasicAuthenticationMethod implements AuthenticationMethod {


    @Override
    public Optional<User> authenticateUser(Request request) {

        final Optional<String[]> authorizationOpt = this.basicCredentials(request);
        if (authorizationOpt.isEmpty()) {
            return Optional.empty();
        }

        final String[] authorization = authorizationOpt.get();
        return login(request.elepy().getCrudFor(User.class), authorization[0], authorization[1]);
    }


    private Optional<User> login(Crud<User> userCrud, String usernameOrEmail, String password) {
        Optional<User> user = getUser(userCrud, usernameOrEmail);

        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    private Optional<User> getUser(Crud<User> userCrud, String usernameOrEmail) {
        if (!usernameOrEmail.contains("@")) {
            final List<? extends User> users = userCrud.searchInField("username", usernameOrEmail);
            if (users.size() > 0) {
                return Optional.of(users.get(0));
            }
        }

        return Optional.empty();
    }

    private Optional<String[]> basicCredentials(Request request) {
        String header = request.headers("Authorization");

        if (header == null || !header.startsWith("Basic")) {
            return Optional.empty();
        }

        var strippedDownHeader = header.replaceAll("Basic", "").trim();
        final String[] strings = readBasicUsernamePassword(strippedDownHeader);

        if (strings.length != 2) {
            return Optional.empty();
        }
        return Optional.of(strings);

    }

    private String[] readBasicUsernamePassword(String base64) {
        String credentials = new String(Base64.getDecoder().decode(base64),
                Charset.forName("UTF-8"));
        return credentials.split(":", 2);
    }


}
