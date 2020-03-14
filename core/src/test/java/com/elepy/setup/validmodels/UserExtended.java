package com.elepy.setup.validmodels;

import com.elepy.annotations.Text;
import com.elepy.annotations.TextArea;
import com.elepy.auth.User;
import com.elepy.models.TextType;

public class UserExtended extends User {
    @TextArea
    private String extendedUserProperty;
} 
