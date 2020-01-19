package com.elepy.di.props;

import com.elepy.annotations.Property;

public class Props {

    @Property("${smtp.server}")
    private String smtpServer;

    @Property("${test}")
    private boolean hi;

    public String getSmtpServer() {
        return smtpServer;
    }

    public boolean isHi() {
        return hi;
    }
}
