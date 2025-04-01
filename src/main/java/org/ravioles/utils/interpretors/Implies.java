package org.ravioles.utils.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public class Implies implements LogicalExpression {
    private final LogicalExpression antecedent;
    private final LogicalExpression consequent;

    public Implies(LogicalExpression antecedent, LogicalExpression consequent) {
        this.antecedent = antecedent;
        this.consequent = consequent;
    }

    @Override
    public boolean evaluate(BeliefBase beliefs) {
        return new Or(new Not(antecedent), consequent).evaluate(beliefs);
    }
}