package com.elepy.jwt;

import com.elepy.Elepy;
import com.elepy.hibernate.HibernateConfiguration;
import com.elepy.tests.auth.CorrectPermissionsTest;

public class JWTCorrectPermissionsTest extends CorrectPermissionsTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(JWTConfiguration.HMAC256("ryan's little secret"));
        elepy.addConfiguration(HibernateConfiguration.inMemory());
    }
}
