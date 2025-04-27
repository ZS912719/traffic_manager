package org.ravioles;

import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.agents.Intersection;
import org.ravioles.agents.Vehicle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
        GraphMap graphMap = new GraphMap();
        Graph graph = graphMap.getGraph();
        SpriteManager sman = graphMap.getSpriteManager();
        graph.display();

        Intersection i1 = new Intersection
                (
                        graphMap,
                        graph.getNode("c2"),
                        graph.getNode("b2"),
                        graph.getNode("b3"),
                        graph.getNode("c3")
                );

        graphMap.addTrafficLight(i1);
        i1.toggleLight("s_10?");

        List<Vehicle> vehicles = new ArrayList<>();

        sleep(3000);
        Vehicle car1 = new Vehicle(graphMap, sman, "Demo1", graph.getNode("b1"), graph.getNode("d4"), 0.2, timer);
        vehicles.add(car1);

        sleep(3000);
        Vehicle car2 = new Vehicle(graphMap, sman, "Demo2", graph.getNode("d4"), graph.getNode("b1"), 0.2, timer);
        vehicles.add(car2);

        sleep(3000);
        Vehicle car3 = new Vehicle(graphMap, sman, "Demo3", graph.getNode("c1"), graph.getNode("d4"), 0.2, timer);
        vehicles.add(car3);

        sleep(3000);
        i1.toggleLight("s_01");
    }
}