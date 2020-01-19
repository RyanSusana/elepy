package com.elepy.admin;

import com.elepy.Configuration;
import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.admin.views.CdnResourceLocation;
import com.elepy.admin.views.LocalResourceLocation;
import com.elepy.admin.views.ResourceLocation;
import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Property;
import com.elepy.exceptions.ElepyConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class AdminPanel implements Configuration {

    private final ElepyAdminPanel elepyAdminPanel;
    private static final Logger logger = LoggerFactory.getLogger(AdminPanel.class);

    private final ResourceLocation resourceLocation;

    @ElepyConstructor
    public AdminPanel(
            @Property(key = "${cms.version}", defaultValue = "latest") String version,
            @Property(key = "${cms.requiredPermissions}") String requiredPermissions
    ) {

        if ("local" .equals(version)) {
            this.resourceLocation = new LocalResourceLocation();
        } else if ("latest" .equals(version)) {
            final var resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("admin.properties");
            final var properties = new Properties();
            try {
                properties.load(Objects.requireNonNull(resourceAsStream));
            } catch (IOException | NullPointerException e) {
                throw new ElepyConfigException("Error reading Elepy version", e);
            }

            logger.info(String.format("Using ElepyVue version '%s'", properties.getProperty("elepyVersion")));
            this.resourceLocation = CdnResourceLocation.version(properties.getProperty("elepyVersion"));
        } else {
            this.resourceLocation = CdnResourceLocation.version(version);
        }

        this.elepyAdminPanel = new ElepyAdminPanel(List.of(requiredPermissions.split(",")));

    }

    AdminPanel(ElepyAdminPanel elepyAdminPanel, ResourceLocation location) {
        this.elepyAdminPanel = elepyAdminPanel;
        this.resourceLocation = location;
    }

    @Deprecated(forRemoval = true)
    public static AdminPanel newAdminPanel() {
        return newBuilder().build();
    }

    public static AdminPanel cdn() {
        return newBuilder().build();
    }

    public static AdminPanel cdn(String version) {
        return newBuilder().withCDNVersion(version).build();
    }

    public static AdminPanel local() {
        logger.warn("Using the local version of ElepyVue. This might cause unnecessary egress traffic. ");
        return newBuilder().withLocal().build();
    }

    public static AdminPanelBuilder newBuilder() {
        return new AdminPanelBuilder();
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.registerDependency(ResourceLocation.class, resourceLocation);

        if (resourceLocation instanceof LocalResourceLocation) {
            elepy.addExtension((ElepyExtension) resourceLocation);
        }

        elepy.addExtension(elepyAdminPanel);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
