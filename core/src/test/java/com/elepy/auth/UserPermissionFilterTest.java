package com.elepy.auth;

import com.elepy.Base;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.models.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserPermissionFilterTest extends Base {
    @Test
    void testSuccessfullyPermittedWithRequiredPermission() throws Exception {
        final HttpContext mockedContext = mockedContext();
        when(mockedContext.attribute("user")).thenReturn(new User("", "", "", Arrays.asList("admin", "protected")));
        final UserPermissionFilter filter = new UserPermissionFilter(Arrays.asList("admin"));

        assertDoesNotThrow(() -> filter.authenticate(mockedContext));
    }

    @Test
    void testSuccessfullyPermittedWithRequiredPermissions() throws Exception {
        final HttpContext mockedContext = mockedContext();
        when(mockedContext.attribute("user")).thenReturn(new User("", "", "", Arrays.asList("admin", "protected")));
        final UserPermissionFilter filter = new UserPermissionFilter(Arrays.asList("admin", "protected"));

        assertDoesNotThrow(() -> filter.authenticate(mockedContext));
    }

    @Test
    void testSuccessfullyPermittedWithoutRequiredPermission() throws Exception {
        final HttpContext mockedContext = mockedContext();
        when(mockedContext.attribute("user")).thenReturn(new User("", "", "", Collections.singletonList("admin")));
        final UserPermissionFilter filter = new UserPermissionFilter(Collections.emptyList());

        assertDoesNotThrow(() -> filter.authenticate(mockedContext));
    }

    @Test
    void testSuccessfullyPermittedWithoutRequiredPermissionWithoutUser() throws Exception {
        final HttpContext mockedContext = mockedContext();
        final UserPermissionFilter filter = new UserPermissionFilter(Collections.emptyList());

        assertDoesNotThrow(() -> filter.authenticate(mockedContext));
    }

    @Test
    void testUnsuccessfullyPermittedWithoutUser() throws Exception {
        final HttpContext mockedContext = mockedContext();
        final UserPermissionFilter filter = new UserPermissionFilter(Arrays.asList("admin"));

        final ElepyException elepyException = assertThrows(ElepyException.class, () -> filter.authenticate(mockedContext));

        assertEquals(401, elepyException.getStatus());
    }

    @Test
    void testUnsuccessfullyPermittedWithUserWithWrongPermission() throws Exception {
        final HttpContext mockedContext = mockedContext();
        when(mockedContext.attribute("user")).thenReturn(new User("", "", "", Collections.singletonList("adminOther")));


        final UserPermissionFilter filter = new UserPermissionFilter(Arrays.asList("admin"));
        final ElepyException elepyException = assertThrows(ElepyException.class, () -> filter.authenticate(mockedContext));

        assertEquals(401, elepyException.getStatus());
    }

    @Test
    void testUnsuccessfullyPermittedWithUserWithoutPermission() throws Exception {
        final HttpContext mockedContext = mockedContext();
        when(mockedContext.attribute("user")).thenReturn(new User("", "", "", Collections.emptyList()));


        final UserPermissionFilter filter = new UserPermissionFilter(Arrays.asList("admin"));
        final ElepyException elepyException = assertThrows(ElepyException.class, () -> filter.authenticate(mockedContext));

        assertEquals(401, elepyException.getStatus());
    }


}
