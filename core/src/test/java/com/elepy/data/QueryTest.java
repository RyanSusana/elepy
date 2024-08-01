package com.elepy.data;

import com.elepy.query.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xbib.cql.CQLParser;

import java.io.Serializable;

import static com.elepy.query.Queries.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

public class QueryTest {
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void simpleSearch() {
        //Should handle multiple spaces properly
        final var searchQuery = parse("hi my name          is ryan");

        assertThat(searchQuery.getExpression())
                .asInstanceOf(type(SearchQuery.class))
                .extracting(SearchQuery::getTerm)
                .isEqualTo("hi my name is ryan");

    }

    @Test
    void stringSearch() {
        var subject = "'ryan is my firstname  and is not equals my last name    or  my middle name!!! @™@€'";
        final var searchQuery = parse(subject);

        assertThat(searchQuery.getExpression())
                .asInstanceOf(type(SearchQuery.class))
                .extracting(SearchQuery::getTerm)
                .isEqualTo(subject.substring(1, subject.length() - 1));

    }

    @Test
    void orSearch() {
        //Should handle multiple spaces properly
        final var searchQuery = parse("ryan or ryan");

        assertThat(searchQuery.getExpression())
                .asInstanceOf(type(BooleanGroup.class))
                .satisfies(group -> {
                    assertThat(group.getOperator()).isEqualTo(BooleanGroup.BooleanOperator.OR);
                    assertThat(group.getExpressions())
                            .hasSize(2)
                            .allSatisfy(expression ->
                                    assertThat(expression)
                                            .asInstanceOf(type(SearchQuery.class))
                                            .extracting(SearchQuery::getTerm)
                                            .isEqualTo("ryan")
                            );

                });
    }

    @Test
    void andSearch() {
        //Should handle multiple spaces properly
        final var searchQuery = parse("ryan AND ryan");

        assertThat(searchQuery.getExpression())
                .asInstanceOf(type(BooleanGroup.class))
                .satisfies(group -> {
                    assertThat(group.getOperator()).isEqualTo(BooleanGroup.BooleanOperator.AND);
                    assertThat(group.getExpressions())
                            .hasSize(2)
                            .allSatisfy(expression ->
                                    assertThat(expression)
                                            .asInstanceOf(type(SearchQuery.class))
                                            .extracting(SearchQuery::getTerm)
                                            .isEqualTo("ryan")
                            );

                });
    }

    @Test
    void name() {

        CQLParser cqlParser = new CQLParser("\"hi my name is ryan\"");


        cqlParser.parse();
        System.out.println(cqlParser.getCQLQuery().getQuery().getScopedClause().getSearchClause()
                .getTerm());


    }




    @ParameterizedTest
    @ValueSource(strings = {
            "property=some !arbitrary text",
            "property = some !arbitrary text",
            "property =some !arbitrary text",
            "property= some !arbitrary text",
            "property eq some !arbitrary text",
            "property equal some !arbitrary text",
            "property equals some !arbitrary text"

    })
    void testEquals(String input) {
        final var parse = parse(input);

        assertFilterMatches(
                parse.getExpression(),
                "property",
                FilterType.EQUALS,
                "some arbitrary text"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "property!=some !arbitrary text",
            "property!= some !arbitrary text",
            "property !=some !arbitrary text",
            "property != some !arbitrary text",
            "property<>some !arbitrary text",
            "property<> some !arbitrary text",
            "property <>some !arbitrary text",
            "property <> some !arbitrary text",
            "property ne some !arbitrary text",
            "property neq some !arbitrary text",
            "property not equal some !arbitrary text",
            "property not equals some !arbitrary text"

    })
    void testNotEquals(String input) {
        final var parse = parse(input);

        assertFilterMatches(
                parse.getExpression(),
                "property",
                FilterType.NOT_EQUALS,
                "some arbitrary text"
        );
    }

    private ObjectAssert<Filter> assertFilterMatches(Expression expression, String propName, FilterType type, Serializable value) {
        return assertThat(expression)
                .asInstanceOf(type(Filter.class))
                .satisfies(filter -> {
                            assertThat(filter.getPropertyName()).isEqualTo(propName);
                            assertThat(filter.getFilterType()).isEqualTo(type);
                            assertThat(filter.getFilterValue()).isEqualTo(value.toString());
                        }
                );
    }
}
