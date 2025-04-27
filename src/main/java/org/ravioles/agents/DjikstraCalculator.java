package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.ravioles.GraphMap;

import java.util.*;

public class DjikstraCalculator {

    public GraphMap graph;
    Map<Node, Double> distances = new HashMap<>();
    Map<Node, Node> predecessors = new HashMap<>();
    PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(
                    n -> distances.getOrDefault(n, Double.MAX_VALUE
                    )
            )); // compare by distance from start node

    public DjikstraCalculator(GraphMap graph) {
        this.graph = graph;
    }

    public List<Node> calculateShortestPath(Node start, Node end){
        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            if (currentNode.getId().equals(end.getId())) break;
            for (Edge e : currentNode.getEachLeavingEdge()){
                Node neighbor = e.getTargetNode();
                double newDistance = distances.get(currentNode) + this.graph.calculatePassibility(e);

                if ( newDistance < distances.getOrDefault(neighbor, Double.MAX_VALUE)){
                    distances.put(neighbor,newDistance);
                    predecessors.put(neighbor, currentNode);
                    if (!queue.contains(neighbor)) queue.add(neighbor);
                }
            }
        }
        return reconstructPath(predecessors, start, end);
    }


    public List<Node> reconstructPath(Map<Node, Node> predecessors, Node start, Node end) {
        List<Node> path = new ArrayList<>();
        for (Node node = end; node != start; node = predecessors.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }


}
