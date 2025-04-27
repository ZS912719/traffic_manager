package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.ravioles.GraphMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection {
    private final Map<String, Map<String, List<Transition>>> transitions;
    Node tl1;
    Node tl2;
    Node tl3;
    Node tl4;
    GraphMap graph;
    List<String> actions = List.of("a_10", "a_10?", "a_01", "a_01?");
    // Explanation by example: a_01 means road 1,3 are passable while road 2,4 are not.
    // Action "a_01?" means road 1,3 are passable while road 2,4 are not but they are switching (orange light).
    String action;
    List<Edge> intersections;
    ArrayList<String> states = new ArrayList<String>();
    String state;
    int timer = 0; // universal ticking timer , updated by the class Timer of this project

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

    public void takeAction() {

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


}