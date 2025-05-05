package org.ravioles.agents.desires;


import org.ravioles.agents.beliefs.BeliefBase;
import org.ravioles.interpretors.*;
import java.util.List;

public class StopAtRedDesire implements Desire {
    BeliefBase beliefs;

    public StopAtRedDesire(BeliefBase beliefs) {
        this.beliefs = beliefs;
    }

    @Override
    public boolean shouldActivate() {
        LogicalExpression e = new And(
                new And(
                        new EqualsExpression("color", "red"),
                        new EqualsExpression("state", "DRIVING")
                ),
                new GreaterThanExpression("position", 0.94)
        );
        return e.evaluate(beliefs);
    }

    @Override
    public boolean isSatisfied() {
        LogicalExpression e = new EqualsExpression("state", "WAITING");
        return e.evaluate(beliefs);
    }

    @Override
    public List<String> generatePlan() {
            return List.of("stop");
    }
}
