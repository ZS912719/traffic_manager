package org.ravioles.agents;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.ravioles.GraphMap;

import java.util.*;

public class DijkstraCalculator {

    public GraphMap graph;
    Map<Node, Double> distances = new HashMap<>();
    Map<Node, Node> predecessors = new HashMap<>();
    PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(
                    n -> distances.getOrDefault(n, Double.MAX_VALUE
                    )
            )); // compare by distance from start node

    public DijkstraCalculator(GraphMap graph) {
        this.graph = graph;
    }

    /**
     * Calculates the shortest path between a start node and an end node in a graph based on passibility
     * (a measure derived from edge distance and congestion level). Uses Dijkstra's algorithm to determine
     * the shortest path.
     *
     * @param start the starting node for the path calculation
     * @param end the destination node for the path calculation
     * @return a list of nodes representing the shortest path from the start node to the end node
     */
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


    /**
     * Reconstructs the path between a start node and an end node using a map of predecessor nodes.
     * The path is built by traversing the predecessors from the end node to the start node and
     * then reversing the resulting list to get the correct sequence from start to end.
     *
     * @param predecessors a map where the key is a node and the value is its predecessor in the shortest path
     * @param start the starting node of the path
     * @param end the ending node of the path
     * @return a list of nodes representing the path from the start node to the end node,
     *         excluding the start node but including the end node
     */
    public List<Node> reconstructPath(Map<Node, Node> predecessors, Node start, Node end) {
        List<Node> path = new ArrayList<>();
        for (Node node = end; node != start; node = predecessors.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }


}
