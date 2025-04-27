package org.ravioles.agents.beliefs;

import org.graphstream.graph.Node;
import org.ravioles.GraphMap;
import org.ravioles.agents.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class BeliefBase {
    GraphMap graph;
    Vehicle vehicle;
    Map<String, Boolean> beliefs = new HashMap<>();


    public BeliefBase(GraphMap graph, Vehicle vehicle) {
        this.graph = graph;
        this.vehicle = vehicle;
    }

    public GraphMap getGraph() {
        return this.graph;
    }

    public void watchLight(Node trafficLight) {
        // TODO: check the light color
    }

    public boolean getTruthValue(String proposition) {
        return this.beliefs.getOrDefault(proposition, false);
    }
}
