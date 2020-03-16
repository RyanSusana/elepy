package com.elepy.admin;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;

public class AdminPanel implements Configuration {

    @Deprecated(forRemoval = true)
    public static AdminPanel newAdminPanel() {
        return newBuilder().build();
    }

    @Deprecated(forRemoval = true)
    public static AdminPanel cdn() {
        return newBuilder().build();
    }

    @Deprecated(forRemoval = true)
    public static AdminPanel cdn(String version) {
        return newBuilder().withCDNVersion(version).build();
    }

    public static AdminPanel local() {
        return newBuilder().withLocal().build();
    }

    public static AdminPanelBuilder newBuilder() {
        return new AdminPanelBuilder();
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.addExtension(new FrontendLoader());
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
