package com.elepy.revisions;

import com.elepy.dao.*;
import com.elepy.exceptions.ElepyException;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import com.elepy.models.Schema;

import java.util.ArrayList;
import java.util.Optional;

import static com.elepy.dao.Filters.and;
import static com.elepy.dao.Filters.eq;

public class RevisionFind implements ActionHandler<Revision> {

    @Override
    public void handle(HandlerContext<Revision> ctx) throws Exception {

        final var context = ctx.http();

        final var revisions = ctx.crud();

        final Schema<?> schema = Optional.ofNullable(context.queryParams("schema"))
                .flatMap(schemaPath -> context.request().schemas().stream().filter(s -> s.getPath().equalsIgnoreCase(schemaPath)).findFirst())
                .orElseThrow(() -> new ElepyException(String.format("Schema not found"), 404));

        context.requirePermissions(schema.getDefaultActions().get("find").getRequiredPermissions());
        final var query = createQuery(schema,
                context.queryParams("user"),
                context.queryParams("record"),
                context.queryParams("skip"),
                context.queryParams("limit"));

        if ("true".equalsIgnoreCase(context.queryParams("count"))) {
            context.response().json(revisions.count(query));
        } else {
            context.response().json(revisions.find(query));
        }
    }

    private Query createQuery(Schema<?> schema, String userId, String recordId, String skip, String limit) {
        var filters = new ArrayList<Filter>();
        filters.add(eq("schemaPath", schema.getPath()));

        if (userId != null) {
            filters.add(eq("userId", userId));
        }

        if (recordId != null) {
            filters.add(eq("recordId", recordId));
        }

        return Queries.create(and(filters))
                .skip(toInt(skip, 0))
                .limit(toInt(limit, 100))
                .sort("timestamp", SortOption.DESCENDING);
    }

    private int toInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NullPointerException | NumberFormatException e) {
            return defaultValue;
        }
    }
}
