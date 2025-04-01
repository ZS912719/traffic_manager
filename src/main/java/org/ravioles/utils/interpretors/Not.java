package org.ravioles.utils.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public class Not implements LogicalExpression {
    private final LogicalExpression expr;

    public Not(LogicalExpression expr) {
        this.expr = expr;
    }

    @Override
    public boolean evaluate(BeliefBase beliefs) {
        return !expr.evaluate(beliefs);
    }
}
