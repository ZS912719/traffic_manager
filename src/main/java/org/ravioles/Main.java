package org.ravioles;

import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.utils.VehicleBatchCreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Thread.sleep;

public class Main{
    public static volatile int tic = 0;

    public static void main(String[] args) throws InterruptedException {
        int delay = 100;
        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tic++;
            }
        });
        timer.setInitialDelay(0);
        timer.start();

        System.setProperty("org.graphstream.ui", "swing");
        GraphMap graphMap = new GraphMap(timer);
        Graph graph = graphMap.getGraph();
        SpriteManager sman = graphMap.getSpriteManager();
        graph.display();

        String[][] intersections = {
                {"c2","b2","b3","c3"},
                {"c4","b4","b5","c5"},
                {"c6","b6","b7","c7"},
                {"e6","d6","d7","e7"},
                {"e4","d4","d5","e5"},
                {"e2","d2","d3","e3"},
                {"g2","f2","f3","g3"},
                {"g4","f4","f5","g5"},
                {"g6","f6","f7","g7"}
        };

        for (String[] ids : intersections) {
            Intersection inter = new Intersection(
                    graphMap,
                    graph.getNode(ids[0]),
                    graph.getNode(ids[1]),
                    graph.getNode(ids[2]),
                    graph.getNode(ids[3]),
                    timer
            );
            graphMap.addTrafficLight(inter);
        }

        sleep(2000);
//        Vehicle v = new Vehicle(graphMap, sman, "Demo1", graph.getNode("a2"), graph.getNode("h8"), 40.0, timer);
        VehicleBatchCreator.createVehicles(graphMap, sman, timer, 100);
    }
}