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
    private boolean arrived = false;
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
        arrived = intentions.isEmpty();
    }

    public Node getPosition() {
        return this.position;
    }

    public Node getDesire() {
        return this.desire;
    }

    public boolean isArrived() {
        return this.arrived;
    }

    public Queue<Node> getIntentions() {
        return this.intentions;
    }

    public void setIsArrived(boolean isArrived) {
        this.arrived = isArrived;
    }
}