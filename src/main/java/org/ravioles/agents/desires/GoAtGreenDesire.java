package org.ravioles.agents.desires;

import org.ravioles.agents.beliefs.BeliefBase;
import org.ravioles.interpretors.*;

import java.util.List;

public class GoAtGreenDesire implements Desire {

    BeliefBase beliefs;

    public GoAtGreenDesire(BeliefBase beliefs) {
        this.beliefs = beliefs;
    }

    @Override
    public boolean shouldActivate() {
        LogicalExpression e = new And(
                new Or(
                        new EqualsExpression("color", "green"),
                        new EqualsExpression("color", "orange")
                ),
                new EqualsExpression("state", "WAITING")
        );
        return e.evaluate(beliefs);
    }

    @Override
    public boolean isSatisfied() {
        LogicalExpression e = new EqualsExpression("state", "DRIVING");
        return e.evaluate(beliefs);
    }

    @Override
    public List<String> generatePlan() {
        return List.of("go");
    }
}
