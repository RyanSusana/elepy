package com.elepy.setup.validmodels;

import com.elepy.annotations.TextArea;
import com.elepy.auth.users.User;

public class UserExtended extends User {
    @TextArea
    private String extendedUserProperty;
} 
