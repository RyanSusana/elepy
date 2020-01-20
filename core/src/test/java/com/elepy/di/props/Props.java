package com.elepy.di.props;

import com.elepy.annotations.Property;

public class Props {

    @Property(key = "smtp.server")
    private String smtpServer;

    @Property(key = "test")
    private boolean testBoolean;

    @Property(key = "notAvailable", defaultValue = "isAvailable")
    private String withDefault;

    public String getSmtpServer() {
        return smtpServer;
    }

    public boolean isTestBoolean() {
        return testBoolean;
    }

    public String getWithDefault() {
        return withDefault;
    }
}
