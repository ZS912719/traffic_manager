package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.GraphMap;
import org.ravioles.Main;
import org.ravioles.agents.beliefs.BeliefBase;
import org.ravioles.agents.intentions.IntentionExecutor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.sleep;


public class Vehicle implements ActionListener {
    private final Timer timer;
    GraphMap graph;
    Sprite vehicle;
    List<String> vehicleStates = List.of("DRIVING", "WAITING", "PARKED");
    String currentState;
    Node startNode;
    Node departureNode;
    Node nextNode;
    Node endNode;
    double speed;
    IntentionExecutor intentionExecutor;
    BeliefBase beliefBase;
    double positionOnRoad = 0;
    int timeOnRoad = 0;
    int currentTick;

    public Vehicle(GraphMap graph, SpriteManager sman, String name, Node startNode, Node endNode, double speed, Timer timer) {
        this.graph = graph;
        this.vehicle = sman.addSprite(name);

//        this.vehicle.setAttribute("ui.label", name);
        this.vehicle.setAttribute("ui.style", "fill-color: rgb(0,0,255);");

        this.startNode = startNode;
        this.departureNode = startNode;
        this.endNode = endNode;
        this.vehicle.attachToNode(startNode.getId());

        this.speed = speed;

        this.currentState = vehicleStates.get(0);

        this.beliefBase = new BeliefBase(this.graph, this);
        this.intentionExecutor = new IntentionExecutor(this.beliefBase, startNode, endNode);

        this.timer = timer;
        timer.addActionListener(this);
    }

    public Sprite getVehicle() {
        return this.vehicle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int oldTick = this.currentTick;
        this.currentTick = Main.tic;
        int deltaTicks = this.currentTick - oldTick;

        this.timeOnRoad += deltaTicks;

        act();
    }

    public void act() {
        if (isAtEnd()) {
            timer.removeActionListener(this);
            return;
        }
        if (vehicle.getAttachment() instanceof Node) {
            setNextNode();
            this.timeOnRoad = 0;
            if (!isAtEnd()) {
                Edge route = getCurrentRoute();
//                vehicle.detach();
                vehicle.attachToEdge(route.getId());
                move();
            }
        }

        if (vehicle.getAttachment() instanceof Edge) {
            move();
        }
    }

    public void setNextNode() {
        Queue<Node> intentions = this.intentionExecutor.getIntentions();
        if (!intentionExecutor.isArrived() && !intentions.isEmpty()) {
            this.nextNode = intentions.poll();
            intentionExecutor.setIsArrived(intentions.isEmpty());
        }
    }

    public Edge getCurrentRoute() {
        return this.departureNode.getEdgeBetween(this.nextNode.getId());
    }

    public void move() {
        Edge route = getCurrentRoute();
        double distance = route.getAttribute("distance");
        this.positionOnRoad = (speed * timeOnRoad) / distance;
        vehicle.setPosition(positionOnRoad);

        if (positionOnRoad >= 1.0) {
            vehicle.detach();
            vehicle.attachToNode(route.getTargetNode().getId());
            departureNode = nextNode;
            timeOnRoad = 0;
            positionOnRoad = 0;
            vehicle.setPosition(positionOnRoad);

        }
    }

    public boolean isAtEnd() {
        return this.intentionExecutor.isArrived() && this.departureNode.equals(this.endNode);
    }


    public void updateVehicleState(String newState) {
        this.currentState = newState;
    }

}