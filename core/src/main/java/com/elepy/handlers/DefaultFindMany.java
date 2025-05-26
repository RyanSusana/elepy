package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.query.*;
import com.elepy.schemas.Schema;
import jakarta.enterprise.context.Dependent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.elepy.query.Filters.*;
import static com.elepy.query.Queries.create;

@Dependent
public class DefaultFindMany<T> implements ActionHandler<T> {
    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        context.status(200);
        if (context.queryParams("count") != null) {
            context.response().json(count(context, ctx));
        } else {
            context.response().json(find(context, ctx));
        }
    }

    public List<? extends T> find(HttpContext context, HandlerContext<T> handlerContext) {
        context.type("application/json");

        final var crud = handlerContext.crud();
        if (context.queryParams("ids") != null) {
            return crud.getByIds(context.request().recordIds());
        } else {
            var query = parseQuery(context.request(), handlerContext);
            return crud.find(query);
        }
    }

    public long count(HttpContext context, HandlerContext<T> handlerContext) {
        var query = parseQuery(context.request(), handlerContext);
        var crud = handlerContext.crud();
        return crud.count(query.getExpression());
    }

    private Query parseQuery(Request request, HandlerContext<T> handlerContext) {
        final var q = Optional.ofNullable(request.queryParams("q")).orElse("");
        var schema = handlerContext.model().getSchema();
        final var sortingSpec = sortingForModel(request, schema);
        String ps = request.queryParams("pageSize");
        String pn = request.queryParams("pageNumber");
        int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
        int pageNumber = pn == null ? 1 : Integer.parseInt(pn);

        final var or = or(filtersForModel(request, schema));
        return create(and(Queries.parse(q).getExpression(), or.getExpressions().isEmpty() ? search("") : or))
                .purge().sort(sortingSpec).page(pageNumber, pageSize);
    }

    private SortingSpecification sortingForModel(Request request, Schema<?> schema) {
        String[] sorts = request.queryParamValues("sort");
        SortingSpecification sortingSpecification = new SortingSpecification();
        if (sorts == null || sorts.length == 0) {
            sortingSpecification.add(schema.getDefaultSortField(), schema.getDefaultSortDirection());
        } else {
            for (String sort : sorts) {
                String[] split = sort.split(",");

                if (split.length == 1) {
                    sortingSpecification.add(split[0], SortOption.ASCENDING);
                } else {
                    sortingSpecification.add(split[0], SortOption.get(split[1]));
                }
            }
        }
        return sortingSpecification;
    }

    private List<Filter> filtersForModel(Request request, Schema<?> schema) {
        final List<Filter> filterQueries = new ArrayList<>();
        for (String queryParam : request.queryParams()) {
            if (queryParam.contains("_")) {
                String[] propertyNameFilter = queryParam.split("_");

                //Get the property name like this, incase you have a property called 'customer_type'
                List<String> propertyNameList = new ArrayList<>(Arrays.asList(propertyNameFilter));
                propertyNameList.remove(propertyNameFilter.length - 1);
                String propertyName = String.join("_", propertyNameList);

                FilterType.getByQueryString(propertyNameFilter[propertyNameFilter.length - 1]).ifPresent(filterType1 -> {

                    final var property = schema.getProperty(propertyName);
                    final var isCompatible = property.getAvailableFilters().stream().map(FilterTypeDescription::filterType)
                            .anyMatch(filterType1::equals);
                    if (!isCompatible) {
                        throw
                                ElepyException.translated("{elepy.messages.exceptions.badFilter}", filterType1.getPrettyName(), property.getLabel());
                    }

                    Filter filter = new Filter(propertyName, filterType1, request.queryParams(queryParam));
                    filterQueries.add(filter);
                });
            }
        }
        return filterQueries;
    }
}

