package org.ravioles;

import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.agents.Vehicle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main{
    public static volatile int tic = 0;

    public static void main(String[] args) {
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

        Vehicle car1 = new Vehicle(graphMap, sman, "Demo", graph.getNode("b1"), graph.getNode("d4"), 0.2, timer);
        graph.display();

    }
}