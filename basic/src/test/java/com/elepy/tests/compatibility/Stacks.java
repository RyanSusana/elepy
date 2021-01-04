package com.elepy.tests.compatibility;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.hibernate.HibernateConfiguration;
import com.elepy.javalin.JavalinService;
import com.elepy.sparkjava.SparkService;
import com.elepy.tests.ElepyConfigHelper;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Stacks {
    public static final ElepyConfigHelper SPARKJAVA = elepy -> elepy.withHttpService(new SparkService());
    public static final ElepyConfigHelper JAVALIN = elepy -> elepy.withHttpService(new JavalinService());

    public static final ElepyConfigHelper MONGO = new MongoStack();
    public static final ElepyConfigHelper HIBERNATE = elepy -> elepy.addConfiguration(HibernateConfiguration.inMemory());

    private static Map<String, ElepyConfigHelper> stackMap;
    public static Map<String, ElepyConfigHelper> dataAccessMap;
    public static Map<String, ElepyConfigHelper> routingMap;

    static {
        stackMap = new HashMap<>();
        dataAccessMap = new HashMap<>();
        routingMap = new HashMap<>();

        dataAccessMap.put("mongo", MONGO);
        dataAccessMap.put("hibernate", HIBERNATE);
//TODO fix  javalin attributes bug
//        routingMap.put("javalin", JAVALIN);
        routingMap.put("sparkjava", SPARKJAVA);

        stackMap.putAll(routingMap);
        stackMap.putAll(dataAccessMap);
    }

    public static Set<List<String>> importantStacks() {
        return Sets.cartesianProduct(dataAccessMap.keySet(), routingMap.keySet());
    }

    public static List<ElepyConfigHelper> getStack(List<String> stack) {
        return stack.stream().map(stackMap::get)
                .peek(item -> {
                    if (item == null) {
                        throw new ElepyConfigException(item + " doesn't exist");
                    }
                }).collect(Collectors.toList());
    }


} 
