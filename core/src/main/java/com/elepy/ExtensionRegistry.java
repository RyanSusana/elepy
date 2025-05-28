package com.elepy;

import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.di.ElepyContext;
import com.elepy.http.HttpServiceInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class ExtensionRegistry {
    @Inject
    private HttpServiceInterceptor http;

    @Inject
    private Elepy elepyContext;

    private Set<Class<? extends ElepyExtension>> extensions = new HashSet<>();

    public void addExtension(Class<? extends ElepyExtension> extension) {
        if (extension == null) {
            throw new IllegalArgumentException("Extension cannot be null");
        }
        if (extensions.contains(extension)) {
            throw new IllegalArgumentException("Extension " + extension.getName() + " is already registered.");
        }
        extensions.add(extension);
    }

    public void initiateExtensions() {
        var elepyPostConfiguration = new ElepyPostConfiguration(elepyContext);

        for (Class<? extends ElepyExtension> extension : extensions) {
            Instance<? extends ElepyExtension> instance = CDI.current().select(extension);

            if (instance.isUnsatisfied()) {
                throw new IllegalStateException("Extension " + extension.getName() + " is not available in the CDI context.");
            }
            if (instance.isAmbiguous()) {
                throw new IllegalStateException("Multiple instances of extension " + extension.getName() + " found in the CDI context.");
            }
            ElepyExtension extensionInstance = instance.get();
            extensionInstance.setup(http, elepyPostConfiguration);
        }
    }
}
