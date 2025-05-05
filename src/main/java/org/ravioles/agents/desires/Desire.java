package org.ravioles.agents.desires;

import org.ravioles.agents.beliefs.BeliefBase;

import java.util.List;

public interface Desire {
    boolean shouldActivate();
    boolean isSatisfied();
    List<String> generatePlan();
}
