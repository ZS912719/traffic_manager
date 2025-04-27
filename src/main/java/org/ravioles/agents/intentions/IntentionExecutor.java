package org.ravioles.agents.intentions;

import org.graphstream.graph.Node;
import org.ravioles.agents.DjikstraCalculator;
import org.ravioles.agents.beliefs.BeliefBase;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class IntentionExecutor {
    DjikstraCalculator planner;
    BeliefBase beliefs;
    private Queue<Node> intentions = new LinkedList<>();
    Node position;
    Node desire;

    public IntentionExecutor(BeliefBase beliefs,Node position, Node desire) {
        this.beliefs = beliefs;
        this.planner = new DjikstraCalculator(beliefs.getGraph());
        this.position = position;
        this.desire = desire;

        generateIntention();
    }

    public void generateIntention() {
        List<Node> path = this.planner.calculateShortestPath(position, desire);
        if (!path.isEmpty()) {
            intentions = new LinkedList<>(path);
        }
    }

    public Node getPosition() {
        return this.position;
    }

    public Node getDesire() {
        return this.desire;
    }

    public Queue<Node> getIntentions() {
        return this.intentions;
    }

}