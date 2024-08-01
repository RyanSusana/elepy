package com.elepy.query.parser;

import com.elepy.data.query.parser.EleQueryBaseListener;
import com.elepy.exceptions.ElepyException;
import com.elepy.query.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.stream.Collectors;

import static com.elepy.data.query.parser.EleQueryParser.*;

public class QueryListener extends EleQueryBaseListener {
    private final Query query = new Query();

    public Query getQuery() {
        return query;
    }

    @Override
    public void enterQuery(QueryContext ctx) {
        query.setExpression(getExpression(ctx.expression()));
    }

    private Expression getExpression(ExpressionContext expression) {
        if (expression == null) {
            return Filters.search("");
        } else if (expression.filter() != null) {
            return getFilter(expression.filter());
        } else if (expression.booleanOperator() != null) {
            return getPredicateGroup(expression.booleanOperator(), expression.expression());
        } else if (expression.searchQuery() != null) {
            return getSearchQuery(expression.searchQuery());
        } else {
            return getExpression(expression.expression(0));
        }
    }

    private Expression getSearchQuery(SearchQueryContext searchQuery) {
        return new SearchQuery(searchQuery.validSearchTerm().stream()
                .map(ValidSearchTermContext::getText)
                .collect(Collectors.joining(" ")));
    }

    private Expression getPredicateGroup(BooleanOperatorContext booleanOperator, List<ExpressionContext> expression) {

        final var predicateGroup = new BooleanGroup();

        predicateGroup.setOperator(booleanOperator.AND() != null ? BooleanGroup.BooleanOperator.AND : BooleanGroup.BooleanOperator.OR);

        predicateGroup.setExpressions(expression.stream().map(this::getExpression).collect(Collectors.toList()));
        return predicateGroup;
    }

    private Expression getFilter(FilterContext filter) {


        if (filter.textFilter() != null) {
            return getTextFilter(filter.textFilter());
        } else if (filter.numberFilter() != null) {
            return getNumberFilter(filter.numberFilter());
        } else {
            return getBaseFilter(filter.baseFilter());
        }
    }

    private Expression getNumberFilter(NumberFilterContext numberFilter) {
        final var filter = new Filter();

        filter.setPropertyName(numberFilter.propertyName().getText());
        filter.setFilterValue(numberFilter.numberValue().getText());
        filter.setFilterType(getFilterType(numberFilter.numberFilterType()));

        return filter;
    }

    private Expression getTextFilter(TextFilterContext textFilter) {
        final var filter = new Filter();

        filter.setPropertyName(textFilter.propertyName().getText());
        filter.setFilterValue(searchString(textFilter.textValue().validSearchTerm()));
        filter.setFilterType(getFilterType(textFilter.textFilterType()));

        return filter;
    }

    private String searchString(List<ValidSearchTermContext> validSearchTerm) {
        return validSearchTerm.stream()
                .map(ValidSearchTermContext::getText)
                .collect(Collectors.joining(" "));
    }

    private Expression getBaseFilter(BaseFilterContext baseFilter) {
        final var filter = new Filter();

        filter.setPropertyName(baseFilter.propertyName().getText());
        filter.setFilterValue(searchString(baseFilter.baseValue().validSearchTerm()));


        filter.setFilterType(getFilterType(baseFilter.baseFilterType()));

        return filter;
    }

    private TerminalNode getFilterTypeToken(ParserRuleContext parserRuleContext) {


        final int[] types = {EQUALS, NOT_EQUALS, CONTAINS, STARTS_WITH, GREATER_THAN, GREATER_THAN_OR_EQUALS, LESSER_THAN, LESSER_THAN_OR_EQUALS};

        for (int type : types) {

            final var token = parserRuleContext.getToken(type, 0);


            if (token != null) {
                return token;
            }
        }
        return null;

    }

    private FilterType getFilterType(ParserRuleContext filterType) {
        final var filterTypeToken = getFilterTypeToken(filterType);
        if (filterTypeToken == null) {
            throw ElepyException.translated("{elepy.messages.exceptions.notSupported}", filterType.getText());
        }
        switch (filterTypeToken.getSymbol().getType()) {
            case EQUALS:
                return FilterType.EQUALS;
            case NOT_EQUALS:
                return FilterType.NOT_EQUALS;
            case CONTAINS:
                return FilterType.CONTAINS;
            case STARTS_WITH:
                return FilterType.STARTS_WITH;
            case GREATER_THAN:
                return FilterType.GREATER_THAN;
            case GREATER_THAN_OR_EQUALS:
                return FilterType.GREATER_THAN_OR_EQUALS;
            case LESSER_THAN:
                return FilterType.LESSER_THAN;
            case LESSER_THAN_OR_EQUALS:
                return FilterType.LESSER_THAN_OR_EQUALS;
            default:
                throw ElepyException.translated("{elepy.messages.exceptions.notSupported}", filterTypeToken.getSymbol().getText());
        }


    }


}
