package com.elepy.di;

import com.elepy.Elepy;
import com.elepy.ObjectMapperProducer;
import com.elepy.crud.Crud;
import com.elepy.crud.CrudRegistry;
import com.elepy.schemas.SchemaRegistry;
import jakarta.enterprise.inject.spi.CDI;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WeldContext implements ElepyContext {

    private final Weld weld;
    private WeldContainer container;
    private WeldExtension extension = new WeldExtension();

    public WeldContext() {
        this.weld = new Weld();
    }

    @Override
    public <T> T getDependency(Class<T> cls, String tag) {
        return container.select(cls).get();
    }


    public <T> T initialize(Class<? extends T> cls) {
        return container.select(cls).get();
    }

    @Override
    public <T> Crud<T> getCrudFor(Class<T> cls) {
        return container.select(CrudRegistry.class).get().getCrudFor(cls);

    }


    public void start() {
        weld.disableDiscovery();
        weld.addBeanClass(Elepy.class);

        var subs = findSubPackages("com.elepy");

        var clasLoader = WeldContext.class.getClassLoader();
        var pkgNameToPkg = subs.stream()
                .map(pkgName -> {
                    var definedPackage = clasLoader.getDefinedPackage(pkgName);
                    return definedPackage;
                }).filter(Objects::nonNull)
                .collect(Collectors.toSet());


        System.out.println(SchemaRegistry.class);
        weld.addPackages(pkgNameToPkg.toArray(Package[]::new));
        weld.addExtensions(extension);
        container = weld.initialize();

    }

    public <T> void registerDependency(Class<T> cls, T object) {
        extension.registerDependency(cls, object);
    }

    public <T> void registerDependency(T object) {
        extension.registerDependency(object);
    }

    public <T> void registerDependency(T object, String tag) {
        extension.registerDependency(object, tag);
    }

    public <T> void registerDependencySupplier(Class<T> clazz, String tag, Supplier<? extends T> supplier) {
        extension.registerDependencySupplier(clazz, tag, supplier);
    }

    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        extension.registerDependency(cls, tag, object);
    }

    public void registerDependency(Class<?> clazz) {
        extension.registerDependency(clazz);
    }

    public void registerDependency(Class<?> clazz, String tag) {
        extension.registerDependency(clazz, tag);
    }

    public static void main(String[] args) {
        String basePackage = "com.elepy"; // Your target base package

        Set<String> subPackages = findSubPackages(basePackage);

        System.out.println("Found subpackages of '" + basePackage + "':");
        subPackages.forEach(System.out::println);
    }

    public static Set<String> findSubPackages(String basePackage) {
        // Configure Reflections
        ConfigurationBuilder configBuilder = new ConfigurationBuilder()
                // Scan URLs relevant to the base package to potentially speed things up
                .setUrls(ClasspathHelper.forPackage(basePackage))
                // Filter inputs to only include resources within the base package
                .filterInputsBy(new FilterBuilder().includePackage(basePackage))
                // Use Scanners.SubTypes to find all class names
                // Using 'false' for SubTypesScanner constructor is deprecated/changed in newer versions.
                // Prefer using the Scanners enum/class constants.
                .setScanners(Scanners.SubTypes); // Scan for subtypes (effectively finding classes)

        Reflections reflections = new Reflections(configBuilder);


        // Get all class names found within the scanned packages
        // Using Scanners.SubTypes.index() accesses the specific index for subtype scanning results
        // The keys of this index are the fully qualified class names.
        Set<String> allClassNames = reflections.getStore().get(Scanners.SubTypes.index()).keySet();

        // Extract package names from class names, filter, and collect unique ones
        return allClassNames.stream()
                .map(className -> {
                    int lastDot = className.lastIndexOf('.');
                    // Ensure it's not a class in the default package (no dot)
                    return (lastDot > 0) ? className.substring(0, lastDot) : "";
                })
                // Ensure we only get packages under the basePackage
                // Also includes the basePackage itself if it contains classes
                .filter(pkgName -> !pkgName.isEmpty() && pkgName.startsWith(basePackage))
                .collect(Collectors.toSet()); // Use a Set to automatically handle duplicates
    }
}
