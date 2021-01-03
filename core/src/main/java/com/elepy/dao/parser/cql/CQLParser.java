package com.elepy.dao.parser.cql;

import com.elepy.dao.*;
import com.elepy.dao.BooleanGroup;
import com.elepy.dao.Query;
import com.elepy.exceptions.ElepyException;
import org.xbib.cql.*;

import java.util.stream.Collectors;

public class CQLParser {

    public static Query parse(String input) {
        org.xbib.cql.CQLParser parser = new org.xbib.cql.CQLParser(input);
        parser.parse();

        final var query = new Query();
        query.setExpression(getExpression(parser.getCQLQuery().getQuery().getScopedClause()));
        return query;
    }

    private static Expression getExpression(ScopedClause scopedClause) {
        if (scopedClause.getSearchClause() != null) {
            return getSearchPredicate(scopedClause.getSearchClause());
        } else if (scopedClause.getBooleanGroup() != null) {
            return getBooleanGroup(scopedClause.getBooleanGroup());
        }
        return getExpression(scopedClause.getScopedClause());
    }

    private static BooleanGroup getBooleanGroup(org.xbib.cql.BooleanGroup booleanGroup) {
        final var predicateGroup = new BooleanGroup();

        predicateGroup.setOperator(booleanGroup.getOperator().getToken()
                .equalsIgnoreCase("and") ?
                BooleanGroup.BooleanOperator.AND
                : BooleanGroup.BooleanOperator.OR);

        predicateGroup.setExpressions(booleanGroup.getModifierList().getModifierList().stream()
                .map(CQLParser::getModifier)
                .collect(Collectors.toList()));

        return predicateGroup;
    }

    private static Filter getModifier(Modifier modifier) {
        return new Filter(
                modifier.getName().getName(),
                getFilterType(modifier.getOperator()),
                modifier.getTerm().getValue());

    }

    private static FilterType getFilterType(Comparitor operator) {
        switch (operator) {
            case EQUALS:
                return FilterType.EQUALS;
            case GREATER:
                return FilterType.GREATER_THAN;
            case GREATER_EQUALS:
                return FilterType.GREATER_THAN_OR_EQUALS;
            case LESS:
                return FilterType.LESSER_THAN;
            case LESS_EQUALS:
                return FilterType.LESSER_THAN_OR_EQUALS;
            case NOT_EQUALS:
                break;
            default:
                throw ElepyException.translated("{elepy.messages.exceptions.notSupported}", operator.getToken());
        }

        return null;
    }

    private static SearchQuery getSearchPredicate(SearchClause searchClause) {
        return new SearchQuery(searchClause.getTerm().toString());
    }
} 
