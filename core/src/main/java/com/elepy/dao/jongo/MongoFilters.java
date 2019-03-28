package com.elepy.dao.jongo;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MongoFilters {
    private final List<MongoFilterTemplate> mongoFilterTemplates;


    public MongoFilters(List<MongoFilterTemplate> mongoFilterTemplates) {
        this.mongoFilterTemplates = mongoFilterTemplates;
    }

    public String compile() {
        return "{$and:[" +
                mongoFilterTemplates
                        .stream()
                        .map(MongoFilterTemplate::compile)
                        .collect(Collectors.joining(",")
                        ) +
                "]}";
    }

    public Serializable[] getHashtagsForJongo() {
        Serializable[] toReturn = new Serializable[mongoFilterTemplates.size()];

        for (int i = 0; i < mongoFilterTemplates.size(); i++) {
            toReturn[i] = mongoFilterTemplates.get(i).getValue();
        }
        return toReturn;
    }
}
