package com.elepy.jwt;

import com.elepy.Elepy;
import com.elepy.hibernate.HibernateConfiguration;
import com.elepy.tests.auth.SecurityTest;

public class JWTSecurityTest extends SecurityTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(JWTConfiguration.HMAC256("ryan's little secret"));
        elepy.addConfiguration(HibernateConfiguration.inMemory());
    }
}
