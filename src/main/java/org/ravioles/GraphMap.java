package org.ravioles;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.agents.Vehicle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphMap implements ActionListener {
    private final Timer timer;
    Graph graph;
    SpriteManager sman;
    List<Intersection> intersections = new ArrayList<>();
    List<Vehicle> vehicles = new ArrayList<>();

    int currentTick;

    public GraphMap(Timer timer) {
        this.timer = timer;
        timer.addActionListener(this);

        this.graph = new MultiGraph("Traffic Management System");
        this.sman = new SpriteManager(graph);

        List<String> cols = List.of("a", "b", "c", "d", "e", "f", "g", "h");
//        List<String> cols = List.of("a", "b", "c", "d");
        int rowCount = 8;

        // the pointes are noted like a1, b2
        for (int ci = 0; ci < cols.size(); ci++) {
            String col = cols.get(ci);

            for (int r = 1; r <= rowCount; r++) {
                String id = col + r;
                Node n = graph.addNode(id);
                n.setAttribute("ui.label", id);
            }

            for (int r = 1; r < rowCount; r++) {
                String src, dst;
                if (ci % 2 != 0) {

                    src = col + r;
                    dst = col + (r + 1);
                } else {

                    src = col + (r + 1);
                    dst = col + r;
                }
                Edge e = graph.addEdge(src + dst, src, dst, true);
                e.setAttribute("distance", 10.0); // each edge has a distance which by default is 10
                e.setAttribute("congestion", 0); // this shall be the index of certain number
            }
        }

        // see output for edges
        for (int r = 1; r <= rowCount; r++) {

            List<String> order = new ArrayList<>(cols);
            if (r % 2 == 0) Collections.reverse(order);

            for (int j = 0; j < order.size() - 1; j++) {
                String src = order.get(j) + r;
                String dst = order.get(j + 1) + r;
                Edge e = graph.addEdge(src + dst, src, dst, true);
                e.setAttribute("distance", 10.0);
                e.setAttribute("congestion", 0);
            }
        }
    }

    public void addTrafficLight(Intersection intersection) {
        this.intersections.add(intersection);
        this.initIntersection(intersection);
    }

    public void initIntersection(Intersection intersection) {

        Node tl1 = intersection.getTL1();
        Node tl2 = intersection.getTL2();
        Node tl3 = intersection.getTL3();
        Node tl4 = intersection.getTL4();

        List<Edge> intersections = new ArrayList<>(intersection.getIntersections());

        // Add left-turns
        Edge l13 = graph.addEdge("L" + tl1.getId() + tl3.getId(), tl1, tl3, true);
        Edge l24 = graph.addEdge("L" + tl2.getId() + tl4.getId(), tl2, tl4, true);
        Edge l31 = graph.addEdge("L" + tl3.getId() + tl1.getId(), tl3, tl1, true);
        Edge l42 = graph.addEdge("L" + tl4.getId() + tl2.getId(), tl4, tl2, true);

        intersections.addAll(List.of(l13, l24, l31, l42));

        for (Edge e : intersections) {
            e.setAttribute("distance", 1.0);
            e.setAttribute("congestion", 0);
        }
    }

    public SpriteManager getSpriteManager() {
        return this.sman;
    }

    public Graph getGraph() {
        return this.graph;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        this.currentTick = Main.tic;

        act();
    }

    public void act(){
        updateCongestion();
    }

    /**
     * the congestion level depends on the number of vehicles driving on the road.
     * the congestion level at an intersection shall always be 0 (not to be updated)
     */
    public void updateCongestion() {
        List<Edge> intersections = new ArrayList<>();

        for (Intersection intersection : this.intersections) {
            intersections.addAll(intersection.getIntersections());
        }

        for (Sprite s : sman.sprites()) {
            if (!s.attached()) continue;

            Element attach = s.getAttachment();

            if (attach instanceof Edge) {
                Edge e = (Edge) attach;
                if (!intersections.contains(e)) { // the congestion level of an intersection shall always be 0
                    Integer cnt = e.getAttribute("congestion");
                    e.setAttribute("congestion", (cnt == null ? 0 : cnt) + 1);
                }
            } else if (attach instanceof Node n) {
                for (Edge e : n.getEachLeavingEdge()) {
                    if (!intersections.contains(e)) {
                        Integer cnt = e.getAttribute("congestion");
                        e.setAttribute("congestion", (cnt == null ? 0 : cnt) + 1);
                    }
                }
            }
        }
    }

    /**
     * the passibility of a road is calculated by multiplying its distance by 1.5 raised to the power of the congestion value
     */
    public double calculatePassibility(Edge e) {
        return ((double) e.getAttribute("distance")) * Math.pow(1.5, (int)e.getAttribute("congestion"));
    }

    public List<Intersection> getIntersections() {
        return this.intersections;
    }

    public void addToVehicles(Vehicle v){
        this.vehicles.add(v);
    }

    public List<Vehicle> getVehicles(){
        return this.vehicles;
    }
}
