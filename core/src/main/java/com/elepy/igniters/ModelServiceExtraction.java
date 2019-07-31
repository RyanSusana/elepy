package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.*;
import com.elepy.models.Model;
import com.elepy.routes.*;
import com.elepy.utils.ReflectionUtils;

import java.util.Optional;

/**
 * This class is used to extractContext a service
 */
public class ModelServiceExtraction<T> {

    public static <T> ServiceHandler<T> extractService(Model<T> model, Elepy elepy) {

        var classType = model.getJavaClass();
        ServiceBuilder<T> serviceBuilder = extractInitialService(classType.getAnnotation(Service.class), elepy);

        Optional.ofNullable(classType.getAnnotation(Delete.class))
                .filter(annotation -> !annotation.handler().equals(DefaultDelete.class))
                .ifPresent(annotation -> serviceBuilder.delete(elepy.initializeElepyObject(annotation.handler())));

        Optional.ofNullable(classType.getAnnotation(Update.class))
                .filter(annotation -> !annotation.handler().equals(DefaultUpdate.class))
                .ifPresent(annotation -> serviceBuilder.update(elepy.initializeElepyObject(annotation.handler())));

        Optional.ofNullable(classType.getAnnotation(Create.class))
                .filter(annotation -> !annotation.handler().equals(DefaultCreate.class))
                .ifPresent(annotation -> serviceBuilder.create(elepy.initializeElepyObject(annotation.handler())));

        Optional.ofNullable(classType.getAnnotation(Find.class))
                .ifPresent(findAnnotation -> {
                    if (!findAnnotation.findManyHandler().equals(DefaultFindMany.class)) {
                        serviceBuilder.findMany(elepy.initializeElepyObject(findAnnotation.findManyHandler()));
                    }
                    if (!findAnnotation.findOneHandler().equals(DefaultFindOne.class)) {
                        serviceBuilder.findOne(elepy.initializeElepyObject(findAnnotation.findOneHandler()));
                    }
                });

        return serviceBuilder.build();

    }

    /**
     * Extracts the initial service and returns it
     */
    private static <T> ServiceBuilder<T> extractInitialService(Service serviceAnnotation, Elepy elepy) {

        ServiceBuilder<T> serviceBuilder = new ServiceBuilder<>();

        if (serviceAnnotation != null) {
            ServiceHandler<T> initialService = elepy.initializeElepyObject(serviceAnnotation.value());
            elepy.addRouting(ReflectionUtils.scanForRoutes(initialService));
            serviceBuilder.defaultFunctionality(initialService);
        }

        return serviceBuilder;
    }
} 
