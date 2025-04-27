package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
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
    IntentionExecutor intentionExecutor;
    BeliefBase beliefBase;
    double positionOnRoad = 0;
    int timeOnRoad = 0;
    int movingTime = 0;
    int currentTick;

    public Vehicle(GraphMap graph, SpriteManager sman, String name, Node startNode, Node endNode, double speed, Timer timer) {
        this.graph = graph;
        this.sman = sman;
        this.vehicle = this.sman.addSprite(name);

//        this.vehicle.setAttribute("ui.label", name);
        this.vehicle.setAttribute("ui.style", "fill-color: rgb(0,0,255);");

        this.startNode = startNode;
        this.departureNode = startNode;
        this.endNode = endNode;
        this.vehicle.attachToNode(startNode.getId());

        this.givenSpeed = speed;
        this.vehicle.setAttribute("speed", speed);

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
        this.beliefBase.watchLight();
        this.beliefBase.finishVehicle();

        if (isAtEnd()) {
            timer.removeActionListener(this);
            ajustSpeed();
            return;
        }
        Element attachment = vehicle.getAttachment();
        if (attachment instanceof Node) {
            setNextNode();
            this.timeOnRoad = 0;
            if (!isAtEnd()) {
                Edge route = getCurrentRoute();
                vehicle.attachToEdge(route.getId());
            }
        }

        if (attachment instanceof Edge) {
            move();
        }
    }

    public void setNextNode() {
        Queue<Node> intentions = this.intentionExecutor.getIntentions();
        if (!intentions.isEmpty()) {
            this.nextNode = intentions.poll();
        }
    }

    public Edge getCurrentRoute() {
        return this.departureNode.getEdgeToward(this.nextNode.getId());
    }

    public void move() {
        Edge route = getCurrentRoute();
        double distance = route.getAttribute("distance");
        double speed = vehicle.getAttribute("speed");
        if (speed == 0) {
            vehicle.setPosition(positionOnRoad);
        } else {
            this.positionOnRoad = (((double) this.vehicle.getAttribute("speed")) * movingTime) / distance;
            vehicle.setPosition(positionOnRoad);
            this.movingTime ++;
        }

        if (positionOnRoad >= 1.0) {
            vehicle.attachToNode(route.getTargetNode().getId());
            departureNode = nextNode;
            timeOnRoad = 0;
            movingTime = 0;
            positionOnRoad = 0;
            vehicle.setPosition(positionOnRoad);
        }
    }

    public void ajustSpeed() {
        switch (beliefBase.getCurrentState()) {
            case "DRIVING":
                this.vehicle.setAttribute("speed", this.givenSpeed);
                break;
            case "WAITING":
                this.vehicle.setAttribute("speed", 0.0);
                break;
            case "PARKED":
                this.vehicle.detach();
                this.sman.removeSprite(this.vehicle.getId());
                break;
        }
    }

    public boolean isAtEnd() {
        return this.beliefBase.isArrived();
    }

    public double getGivenSpeed() {
        return this.givenSpeed;
    }

    public Node getDepartureNode() {
        return this.departureNode;
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public Node getEndNode() {
        return this.endNode;
    }

    public double getPositionOnRoad() {
        return this.positionOnRoad;
    }

    public int getTimeOnRoad() {
        return this.timeOnRoad;
    }

}