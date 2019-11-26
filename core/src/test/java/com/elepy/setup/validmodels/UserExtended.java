package com.elepy.setup.validmodels;

import com.elepy.annotations.Text;
import com.elepy.auth.User;
import com.elepy.models.TextType;

public class UserExtended extends User {
    @Text(TextType.TEXTAREA)
    private String extendedUserProperty;
} 
