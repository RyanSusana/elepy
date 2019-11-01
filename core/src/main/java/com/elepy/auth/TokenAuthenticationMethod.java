package com.elepy.auth;

public interface TokenAuthenticationMethod extends AuthenticationMethod{

    String createToken(User user, int duration);

}
