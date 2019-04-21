package com.elepy.auth.methods;

import com.elepy.Elepy;
import com.elepy.auth.MockCrudProvider;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseMethodTest {
    protected Elepy elepy;

    @BeforeEach
    void setUp() {

        elepy = new Elepy();
        elepy.onPort(10293);

        elepy.withDefaultCrudProvider(MockCrudProvider.class);


    }

    @AfterEach
    void tearDown() {
        elepy.stop();
    }


    protected UserLoginService mockedLoginService(List<String> permissionsOnLogin) {

        final UserLoginService mock = mock(UserLoginService.class);

        when(mock.login(any(), any())).thenReturn(Optional.empty());
        when(mock.login("admin", "admin")).thenReturn(Optional.of(new User("admin", "admin", "admin", permissionsOnLogin)));

        return mock;
    }

} 
