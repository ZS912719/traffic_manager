package org.ravioles.utils.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public class Atom implements LogicalExpression {
    private final String proposition;

    public Atom(String proposition) {
        this.proposition = proposition;
    }

    @Override
    public boolean evaluate(BeliefBase beliefs) {
        return beliefs.getTruthValue(proposition);
    }

}