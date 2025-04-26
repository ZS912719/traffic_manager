package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.*;


public class Vehicle {
    //    private final BeliefBase beliefs = new BeliefBase();
//    private final GoalManager goals = new GoalManager();
//    private final IntentionExecutor intentions = new IntentionExecutor();
    Graph graph;
    Sprite vehicle;
    List<String> vehicleStates = List.of("DRIVING", "WAITING", "PARKED");
    String currentState;
    Node departureNode;
    Node nextNode;
    Node endNode;
    double speed;
    int timer = 0;
    Map<Node, Double> distances = new HashMap<>();
    Map<Node, Double> predecessors = new HashMap<>();
    PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(
                    n -> distances.getOrDefault(n, Double.MAX_VALUE
                    )
            )); // compare by distance from start node


    public Vehicle(Graph graph, SpriteManager sman, String name, Node start, Node endNode, double speed) {
        this.graph = graph;
        this.vehicle = sman.addSprite(name);
        this.vehicle.setAttribute("ui.label", name);
        this.vehicle.setAttribute("ui.style", "fill-color: rgb(0,0,255);");
        this.departureNode = start;
        this.endNode = endNode;
        this.vehicle.attachToNode(start.getId());
        this.speed = speed;
        this.currentState = vehicleStates.get(0);

        distances.put(start, 0.0);
        queue.add(start);
    }

    public Sprite getVehicle() {
        return this.vehicle;
    }

    public List<Edge> getPossibleRoutes() {
        List<Edge> possibleRoutes = new ArrayList<>();
        for (Edge e : this.departureNode.getEachLeavingEdge()) {
            possibleRoutes.add(e);
        }
        return possibleRoutes;
    }

    public Edge getCurrentRoute() {
        return this.departureNode.getEdgeBetween(this.nextNode.getId());
    }

    public void move() {
        int distance = this.getCurrentRoute().getAttribute("distance");
        this.vehicle.setPosition((this.speed * this.timer) / distance);
    }

    public boolean isAtEnd() {
        return this.departureNode.getId().equals(this.endNode.getId());
    }

    public void updateTimer() {

    }

    public void enterNextRoad(Edge next) {

    }

    public void planRoute() {

    }

    public int getPassedVehicleNumber(){
        return 0;
    }

    private void printVehicleStatus() {

    }

    private String getNextTrafficLightState() {
        return null;
    }

    public void updateVehicleState(String newState) {
        this.currentState = newState;
    }

    public List<Node> calculateShortestPath(){
        return new ArrayList<>();
    }

}