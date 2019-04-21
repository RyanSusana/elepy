package com.elepy.auth.methods;

import com.elepy.Elepy;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseMethodTest {
    protected Elepy elepy;



    protected UserLoginService mockedLoginService(List<String> permissionsOnLogin) {

        final UserLoginService mock = mock(UserLoginService.class);

        when(mock.login(any(), any())).thenReturn(Optional.empty());
        when(mock.login("admin", "admin")).thenReturn(Optional.of(new User("admin", "admin", "admin", permissionsOnLogin)));

        return mock;
    }

} 
