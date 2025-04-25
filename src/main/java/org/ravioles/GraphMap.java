package org.ravioles;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphMap {
    Graph graph;
    Timer timer;

    public GraphMap(Timer timer) {
        this.graph = new MultiGraph("Traffic Management System");
        this.timer = timer;

//        List<String> cols = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        List<String> cols = List.of("a","b","c","d");
        int rowCount = 4;

        // the pointes are noted like a_1, b_2
        for (int ci = 0; ci < cols.size(); ci++) {
            String col = cols.get(ci);

            for (int r = 1; r <= rowCount; r++) {
                String id = col + r;
                Node n = graph.addNode(id);
                n.setAttribute("ui.label", id);
            }

            for (int r = 1; r < rowCount; r++) {
                String src, dst;
                if (ci % 2 == 0) {

                    src = col + r;
                    dst = col + (r + 1);
                } else {

                    src = col + (r + 1);
                    dst = col + r;
                }
                graph.addEdge(src + dst, src, dst, true);
            }
        }

        // see output for edges
        for (int r = 1; r <= rowCount; r++) {

            List<String> order = new ArrayList<>(cols);
            if (r % 2 == 1) Collections.reverse(order);

            for (int j = 0; j < order.size() - 1; j++) {
                String src = order.get(j) + r;
                String dst = order.get(j + 1) + r;
                graph.addEdge(src + dst, src, dst, true);
            }
        }

        graph.getNode("c2").setAttribute("ui.style", "fill-color: rgb(255,0,0);");
        List<String> edges = new ArrayList<>();
        for (Edge e : graph.getNode("c2").getEachLeavingEdge()) {
            edges.add(e.getId());
        }
        System.out.println(edges);
//        System.out.println((graph.getEdge("c2c3").getSourceNode().getId()));

        //        for (Node n : graph.getEachNode()) {
//            System.out.println(n.getId());
//        }
//
//        for (Edge e : graph.getEachEdge()) {
//            System.out.println(e.getId());
//        }

    }

    public Graph getGraph() {
        return this.graph;
    }
}
