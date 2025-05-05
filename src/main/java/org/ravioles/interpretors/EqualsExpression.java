package org.ravioles.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public class EqualsExpression implements LogicalExpression {
    private final String key;
    private final Object expected;

    public EqualsExpression(String key, Object expected) {
        this.key = key;
        this.expected = expected;
    }

    @Override
    public boolean evaluate(BeliefBase beliefs) {
        return expected.equals(beliefs.getBelief(key));
    }
}