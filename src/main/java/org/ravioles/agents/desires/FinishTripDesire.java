package org.ravioles.agents.desires;

import org.ravioles.agents.beliefs.BeliefBase;
import org.ravioles.interpretors.EqualsExpression;
import org.ravioles.interpretors.LogicalExpression;

import java.util.List;

public class FinishTripDesire implements Desire {

    BeliefBase beliefs;

    public FinishTripDesire(BeliefBase beliefs) {
        this.beliefs = beliefs;
    }

    @Override
    public boolean shouldActivate() {
        LogicalExpression e = new EqualsExpression("departureNode", this.beliefs.getBelief("endNode"));
        return e.evaluate(beliefs);
    }

    @Override
    public boolean isSatisfied() {
        LogicalExpression e = new EqualsExpression("arrived", true);
        return e.evaluate(beliefs);
    }

    @Override
    public List<String> generatePlan() {
        return List.of("fin");
    }
}
