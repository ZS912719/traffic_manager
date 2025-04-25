package org.ravioles;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        Timer timer = new Timer();
        GraphMap graphMap = new GraphMap(timer);
        Graph graph = graphMap.getGraph();
        graph.display();

        SpriteManager sman = new SpriteManager(graph);
    }
}