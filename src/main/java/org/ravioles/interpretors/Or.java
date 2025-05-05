package org.ravioles.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public class Or implements LogicalExpression {
    private final LogicalExpression left;
    private final LogicalExpression right;

    public Or(LogicalExpression left, LogicalExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(BeliefBase beliefs) {
        return left.evaluate(beliefs) || right.evaluate(beliefs);
    }
}