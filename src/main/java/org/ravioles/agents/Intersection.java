package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.ravioles.GraphMap;
import org.ravioles.Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection implements ActionListener {
    private final Map<String, Map<String, List<Transition>>> transitions;
    Node tl1; // traffic light
    Node tl2;
    Node tl3;
    Node tl4;
    GraphMap graph;
    List<String> actions = List.of("a_10", "a_10?", "a_01", "a_01?");
    // Explanation by example: a_01 means road 1,3 are passable while road 2,4 are not.
    // Action "a_01?" means road 1,3 are passable while road 2,4 are not but they are switching (orange light).
    String action;
    List<Edge> intersections;
    List<String> states = List.of("s_10","s_10?","s_01","s_01?");
    String state;
    int timer = 0; // timer for switching light
    int currentTick;

    public Intersection(GraphMap graph, Node tl1, Node tl2, Node tl3, Node tl4) {
        this.transitions = new HashMap<>();

        this.graph = graph;

        this.tl1 = tl1;
        this.tl2 = tl2;
        this.tl3 = tl3;
        this.tl4 = tl4;

        this.intersections = getIntersections();

    }

    public Node getTL1() {
        return this.tl1;
    }

    public Node getTL2() {
        return this.tl2;
    }

    public Node getTL3() {
        return this.tl3;
    }

    public Node getTL4() {
        return this.tl4;
    }

    public List<Edge> getIntersections() {
        return List.of(
                tl1.getEdgeBetween(tl2.getId()),
                tl2.getEdgeBetween(tl3.getId()),
                tl3.getEdgeBetween(tl4.getId()),
                tl4.getEdgeBetween(tl1.getId())
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int oldTick = this.currentTick;
        this.currentTick = Main.tic;
        int deltaTicks = this.currentTick - oldTick;

        act();
    }

    public void act() {

    }

    public void toggleLight(String newState){
        this.state = newState;
        switch (this.state) {
            case "s_10":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(0,255,0);"); //green
                this.tl1.setAttribute("color", "green");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                this.tl3.setAttribute("color", "green");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(255,0,0);"); // red
                this.tl2.setAttribute("color", "red");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl4.setAttribute("color", "red");
                break;
            case "s_10?":
                this.tl1.setAttribute("ui.style","fill-color: rgb(255,128,0);"); // orange
                this.tl1.setAttribute("color", "orange");
                this.tl3.setAttribute("ui.style","fill-color: rgb(255,128,0);");
                this.tl3.setAttribute("color", "orange");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl2.setAttribute("color", "red");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl4.setAttribute("color", "red");
                break;
            case "s_01":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl1.setAttribute("color", "red");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl3.setAttribute("color", "red");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                this.tl2.setAttribute("color", "green");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                this.tl4.setAttribute("color", "green");
                break;
            case "s_01?":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl1.setAttribute("color", "red");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl3.setAttribute("color", "red");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(255,128,0);");
                this.tl2.setAttribute("color", "orange");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(255,128,0);");
                this.tl4.setAttribute("color", "orange");
                break;
        }
        this.timer = 0;
    }

    public double calculateReward() {
        // let a vehicle pass is 10pts
        // keep a vehicle waiting is -1pts/s then exponential decay

        return 0;
    }

    public void updateTimer(int tic) {
        this.timer += tic;
    }

    public double checkTraffic() {
        return 0;
    }

    public List<Node> getTrafficLights() {
        return List.of(tl1, tl2, tl3, tl4);
    }


}