package org.ravioles.utils.interpretors;

import org.ravioles.agents.beliefs.BeliefBase;

public interface LogicalExpression {
    boolean evaluate(BeliefBase beliefs);
}
