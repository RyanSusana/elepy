package com.elepy.http;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.Annotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class RouteScanner {
    public  Route routeFromMethod(Object obj, Method method) {
        com.elepy.annotations.Route annotation = Annotations.get(method, com.elepy.annotations.Route.class);
        HttpContextHandler route;
        if (method.getParameterCount() == 0) {
            route = ctx -> {
                Object invoke = method.invoke(obj);
                if (invoke instanceof String) {
                    ctx.response().result((String) invoke);
                }
            };
        } else if (method.getParameterCount() == 2
                && method.getParameterTypes()[0].equals(Request.class)
                && method.getParameterTypes()[1].equals(Response.class)) {

            route = ctx -> {
                Object invoke = method.invoke(obj, ctx.request(), ctx.response());
                if (invoke instanceof String) {
                    ctx.response().result((String) invoke);
                }
            };

        } else if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(HttpContext.class)) {
            route = ctx -> {
                Object invoke = method.invoke(obj, ctx);
                if (invoke instanceof String) {
                    ctx.response().result((String) invoke);
                }
            };
        } else {
            throw new ElepyConfigException("@HttpContextHandler annotated method must have no parameters or (Request, Response)");
        }
        return anElepyRoute()
                .addPermissions(annotation.requiredPermissions())
                .path(annotation.path())
                .method(annotation.method())
                .route(route)
                .build();
    }

    public  List<Route> scanForRoutes(Object obj) {
        List<Route> toReturn = new ArrayList<>();
        for (Method method : obj.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(com.elepy.annotations.Route.class)) {
                toReturn.add(routeFromMethod(obj, method));
            }
        }
        return toReturn;
    }

}
