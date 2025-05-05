package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.GraphMap;
import org.ravioles.Intersection;
import org.ravioles.Main;
import org.ravioles.agents.beliefs.BeliefBase;
import org.ravioles.agents.desires.*;
import org.ravioles.agents.intentions.IntentionExecutor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Vehicle implements ActionListener {
    private final Timer timer;
    GraphMap graph;
    Sprite vehicle;
    double givenSpeed;
    SpriteManager sman;

    Node startNode;
    Node departureNode;
    Node nextNode;
    Node endNode;

    List<String> vehicleStates = List.of("DRIVING", "WAITING", "PARKED");
    String currentState;

    BeliefBase beliefs;
    IntentionExecutor intentionExecutor;
    List<Desire> desires = new ArrayList<>();

    double positionOnRoad = 0;
    int timeOnRoad = 0;
    int movingTime = 0;
    int currentTick;
    int waitingTime = 0;

    DijkstraCalculator planner;
    private Queue<Node> dijkstraPath = new LinkedList<>();

    public Vehicle(GraphMap graph, SpriteManager sman, String name, Node startNode, Node endNode, double speed, Timer timer) {
        this.graph = graph;
        this.sman = sman;
        this.vehicle = this.sman.addSprite(name);

        this.vehicle.setAttribute("ui.style", "fill-color: rgb(0,0,255);");

        this.startNode = startNode;
        this.departureNode = startNode;
        this.endNode = endNode;
        this.vehicle.attachToNode(startNode.getId());

        this.givenSpeed = speed / 100;
        this.vehicle.setAttribute("speed", speed / 100);

        this.beliefs = new BeliefBase(graph);
        this.beliefs.addBelief("arrived", false);
        this.beliefs.addBelief("startNode", startNode.getId());
        this.beliefs.addBelief("endNode", endNode.getId());

        this.desires.add(new FinishTripDesire(this.beliefs));
        this.desires.add(new StopAtRedDesire(this.beliefs));
        this.desires.add(new GoAtGreenDesire(this.beliefs));

        this.intentionExecutor = new IntentionExecutor(this);
        this.currentState = this.vehicleStates.get(0);

        generatePath();

        this.timer = timer;
        timer.addActionListener(this);
    }

    public Sprite getVehicle() {
        return this.vehicle;
    }

    public GraphMap getGraph() {
        return this.graph;
    }

    public void generatePath() {
        this.planner = new DijkstraCalculator(graph);
        List<Node> path = this.planner.calculateShortestPath(this.startNode, this.endNode);
        if (!path.isEmpty()) {
            this.dijkstraPath = new LinkedList<>(path);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int oldTick = this.currentTick;
        this.currentTick = Main.tic;
        int deltaTicks = this.currentTick - oldTick;

        this.timeOnRoad += deltaTicks;

        this.waitingTime = this.timeOnRoad > this.movingTime ? this.timeOnRoad - this.movingTime : 0;

        act();
    }

    /**
     * Performs the main action sequence for the vehicle, which involves managing its state,
     * updating its position, and transitioning between nodes and edges in the road network.
     * <p>
     * This method performs the following operations:
     * <p>
     * - Observes the state of traffic lights and updates the vehicle's behavior accordingly
     * by invoking the `watchLight` method from the belief base.
     * - Checks if the vehicle has reached its final destination by calling the `finishVehicle` method.
     * - If the vehicle is at its end destination (`isAtEnd` method returns true), it stops the timer
     * listener and adjusts its speed via the `ajustSpeed` method.
     * - Determines whether the vehicle is currently attached to a node or an edge.
     * - If attached to a node:
     * - Moves to the next node by invoking `setNextNode`.
     * - Resets road-related state variables, such as `timeOnRoad`.
     * - If the journey is not yet complete (`isAtEnd` returns false), computes the current route
     * using the `getCurrentRoute` method and attaches the vehicle to the corresponding edge.
     * - If attached to an edge:
     * - Moves the vehicle along the road segment by invoking the `move` method.
     */
    public void act() {
        updateBeliefs();
        processDesires();

        if (this.currentState.equals("PARKED")) {
            timer.removeActionListener(this);
            adjustSpeed();
            return;
        }
        Element attachment = vehicle.getAttachment();
        if (attachment instanceof Node) {
            setNextNode();
            this.timeOnRoad = 0;
            if (!this.currentState.equals("PARKED")) {
                Edge route = getCurrentRoute();
                vehicle.attachToEdge(route.getId());
            }
        }

        if (attachment instanceof Edge) {
            move();
        }
    }

    public void updateBeliefs() {
        this.beliefs.addBelief("state", this.currentState);
        this.beliefs.addBelief("position", getPositionOnRoad());
        this.beliefs.addBelief("departureNode", this.departureNode.getId());
        updateLightBeliefs();
    }

    private void processDesires() {
        for (Desire d : desires) {
            if (!d.isSatisfied() && d.shouldActivate()) {
                List<String> plan = d.generatePlan();
                intentionExecutor.enqueue(plan);
                break;
            }
        }
    }

    public void setNextNode() {
        timeOnRoad = 0;
        movingTime = 0;
        if (!dijkstraPath.isEmpty()) {
            this.nextNode = dijkstraPath.poll();
        }
    }

    public Edge getCurrentRoute() {
        return this.departureNode.getEdgeToward(this.nextNode.getId());
    }

    /**
     * Moves the vehicle along its current route by updating its position on the road
     * segment and handling transitions between nodes and edges in the road network.
     * <p>
     * This method performs the following steps:
     * - Retrieves the current route (edge) that the vehicle is traveling on.
     * - Determines the distance of the edge and the current speed of the vehicle.
     * - Calculates the vehicle's current position on the road segment based on its speed,
     * elapsed moving time, and the length of the edge.
     * - Updates the vehicle's position on the road.
     * - If the vehicle reaches the end of the edge (position ≥ 1.0), the vehicle transitions
     * to the target node of the edge:
     * - Attaches the vehicle to the target node.
     * - Updates the departure node to the current target node.
     * - Resets road-related variables such as `timeOnRoad`, `movingTime`, and `positionOnRoad`.
     */
    public void move() {
        Edge route = getCurrentRoute();
        double distance = route.getAttribute("distance");
        double speed = vehicle.getAttribute("speed");
        if (speed == 0) {
            vehicle.setPosition(positionOnRoad);
        } else {
            this.positionOnRoad = (((double) this.vehicle.getAttribute("speed")) * movingTime) / distance;
            vehicle.setPosition(positionOnRoad);
            this.movingTime++;
        }

        if (positionOnRoad >= 1.0) {
            vehicle.attachToNode(route.getTargetNode().getId());
            this.departureNode = this.nextNode;
            positionOnRoad = 0;
            vehicle.setPosition(positionOnRoad);
        }
    }

    /**
     * Adjusts the speed of the vehicle based on its current state as determined
     * by the belief base.
     * <p>
     * The method evaluates the vehicle's current state and performs the following actions:
     * - If the state is "DRIVING", the vehicle's speed is set to its predefined given speed.
     * - If the state is "WAITING", the vehicle's speed is set to 0.0.
     * - If the state is "PARKED", the vehicle is detached from the simulation, and its
     * associated sprite is removed from the sprite manager.
     */
    public void adjustSpeed() {
        switch (this.currentState) {
            case "DRIVING":
                this.vehicle.setAttribute("speed", this.givenSpeed);
                break;
            case "WAITING":
                this.vehicle.setAttribute("speed", 0.0);
                break;
            case "PARKED":
                this.vehicle.detach();
                this.sman.removeSprite(this.vehicle.getId());
                finishTrip();
                break;
        }
    }

    public double getPositionOnRoad() {
        return this.positionOnRoad;
    }

    public int getWaitingTime() {
        return this.waitingTime;
    }

    /**
     * determine if the vehicle arrives at the end node
     */
    public void finishTrip() {
        this.beliefs.addBelief("arrived", true);
    }

    public void updateLightBeliefs() {
        Element e = this.vehicle.getAttachment();
        List<Edge> exceptions = new ArrayList<>();
        for (Intersection intersection : this.graph.getIntersections()) {
            exceptions.addAll(intersection.getIntersections());
        }

        if (e instanceof Edge edge && !exceptions.contains(edge)) {
            Node tl = edge.getTargetNode();
            this.beliefs.addBelief("color", tl.getAttribute("color") == null? "black": tl.getAttribute("color") );
        }
    }

    public void setCurrentState(String state) {
        this.currentState = state;
    }

    public List<String> getVehicleStates() {
        return this.vehicleStates;
    }

}