package com.elepy.di.props;

import com.elepy.annotations.Property;

public class Props {

    @Property(key = "${smtp.server}")
    private String smtpServer;

    @Property(key = "${test}")
    private boolean testBoolean;

    public String getSmtpServer() {
        return smtpServer;
    }

    public boolean isTestBoolean() {
        return testBoolean;
    }
}
