package com.elepy.admin;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;

public class AdminPanelConfiguration implements Configuration {

    @Deprecated(forRemoval = true)
    public static AdminPanelConfiguration newAdminPanel() {
        return newBuilder().build();
    }

    @Deprecated(forRemoval = true)
    public static AdminPanelConfiguration cdn() {
        return newBuilder().build();
    }

    @Deprecated(forRemoval = true)
    public static AdminPanelConfiguration cdn(String version) {
        return newBuilder().withCDNVersion(version).build();
    }

    public static AdminPanelConfiguration local() {
        return newBuilder().withLocal().build();
    }

    public static AdminPanelBuilder newBuilder() {
        return new AdminPanelBuilder();
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.addExtension(FrontendLoader.class);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
