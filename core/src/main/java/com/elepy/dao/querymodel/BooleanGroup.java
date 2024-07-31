package com.elepy.dao.querymodel;

import java.util.List;
import java.util.stream.Collectors;

public class BooleanGroup extends Expression {
    private List<Expression> expressions;
    private BooleanOperator operator;

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public BooleanOperator getOperator() {
        return operator;
    }

    public void setOperator(BooleanOperator operator) {
        this.operator = operator;
    }


    @Override
    public void purge() {

        expressions.forEach(Expression::purge);
        expressions = expressions.stream()
                .filter(expression -> !expression.canBeIgnored())
                .collect(Collectors.toList());
    }

    @Override
    public boolean canBeIgnored() {
        return this.getExpressions().isEmpty();
    }


    public enum BooleanOperator{
        AND, OR
    }
}
