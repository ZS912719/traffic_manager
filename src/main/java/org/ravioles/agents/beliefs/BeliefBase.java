package org.ravioles.agents.beliefs;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.ravioles.GraphMap;
import org.ravioles.agents.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeliefBase {
    GraphMap graph;
    Vehicle vehicle;
    Map<String, Boolean> beliefs = new HashMap<>();
    List<String> vehicleStates = List.of("DRIVING", "WAITING", "PARKED");
    String currentState;
    Boolean arrived = false;

    int deltaTicks;


    public BeliefBase(GraphMap graph, Vehicle vehicle) {
        this.graph = graph;
        this.vehicle = vehicle;

        this.currentState = vehicleStates.get(0);
    }

    public GraphMap getGraph() {
        return this.graph;
    }

    public void updateVehicleState(String newState) {
        this.currentState = newState;
    }


    public void updateBelief(String proposition, boolean truthValue) {
        this.beliefs.put(proposition, truthValue);
    }

    public boolean getTruthValue(String proposition) {
        return this.beliefs.getOrDefault(proposition, false);
    }

    public void setDeltaTicks(int deltaTicks) {
        this.deltaTicks = deltaTicks;
    }

    public void update() {

    }

    public void watchLight() {
        Element e = this.vehicle.getVehicle().getAttachment();
        if (e instanceof Edge) {
            Node tl = ((Edge) e).getTargetNode();
            if ((tl.getAttribute("color") == "red" ||
                    (tl.getAttribute("color") == "orange") &&
                            this.currentState.equals("DRIVING")) && this.vehicle.getPositionOnRoad() > 0.95) {
                this.currentState = vehicleStates.get(1); // WAITING
                vehicle.ajustSpeed();
            } else if (tl.getAttribute("color") == "green" && this.currentState.equals("WAITING")) {
                this.currentState = vehicleStates.get(0); // DRIVING
                vehicle.ajustSpeed();
            }
        }
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public void finishVehicle() {
        if (this.vehicle.getDepartureNode().equals(this.vehicle.getEndNode())) {
             this.arrived = true;
            this.currentState = vehicleStates.get(2);
        }
    }

    public boolean isArrived() {
        return this.arrived;
    }
}
