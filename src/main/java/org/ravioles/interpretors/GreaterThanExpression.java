package org.ravioles.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public class GreaterThanExpression implements LogicalExpression {
    private final String key;
    private final double threshold;

    public GreaterThanExpression(String key, double threshold) {
        this.key = key;
        this.threshold = threshold;
    }

    @Override
    public boolean evaluate(BeliefBase beliefs) {
        Object value = beliefs.getBelief(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue() > threshold;
        }
        return false;
    }
}