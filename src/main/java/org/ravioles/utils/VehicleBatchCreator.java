package org.ravioles.utils;

import org.graphstream.ui.spriteManager.SpriteManager;
import org.ravioles.GraphMap;
import org.ravioles.agents.Vehicle;

import javax.swing.Timer;
import java.util.Random;

import java.util.*;
import java.util.stream.IntStream;

public class VehicleBatchCreator {
    private static final char[] LETTERS = {'a','b','c','d','e','f','g','h'};
    private static final int MAX_DIGIT = 8;
    private static final double MIN_SPEED = 10.0;
    private static final double MAX_SPEED = 60.0;
    private static final Set<String> FORBIDDEN = Set.of(
            "c2","b2","b3","c3",
            "c4","b4","b5","c5",
            "c6","b6","b7","c7",
            "e6","d6","d7","e7",
            "e4","d4","d5","e5",
            "e2","d2","d3","e3",
            "g2","f2","f3","g3",
            "g4","f4","f5","g5",
            "g6","f6","f7","g7"
    );

    //the valid set of nodes' ids. start node or end node can be the node of traffic light.
    private static final List<String> VALID_NODES =
            IntStream.range(0, LETTERS.length)
                    .mapToObj(i -> LETTERS[i])
                    .flatMap(ch -> IntStream.rangeClosed(1, MAX_DIGIT)
                            .mapToObj(d -> "" + ch + d))
                    .filter(id -> !FORBIDDEN.contains(id))
                    .toList(); // this is chatGPT, ain't no idea what it's doing. but it works, change with cautions.

    /**
     * Creates a specified number of vehicle instances, initializing them with
     * random start and end nodes, as well as a random speed within predefined limits.
     *
     * @param graphMap the GraphMap instance representing the map and graph structure.
     * @param sman the SpriteManager responsible for managing graphical components.
     * @param timer the Timer used for synchronizing vehicle actions and behaviors.
     * @param count the number of vehicles to create.
     */
    public static void createVehicles(GraphMap graphMap,
                                      SpriteManager sman,
                                      Timer timer,
                                      int count) {
        Random rand = new Random();
        for (int i = 1; i <= count; i++) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted", e);
            }

            String name = "Demo" + i;
            String startId = VALID_NODES.get(rand.nextInt(VALID_NODES.size()));
            String endId;
            do {
                endId = VALID_NODES.get(rand.nextInt(VALID_NODES.size()));
            } while (endId.equals(startId));

            double speed = MIN_SPEED + rand.nextDouble() * (MAX_SPEED - MIN_SPEED);

            Vehicle vehicle = new Vehicle(
                    graphMap, sman, name,
                    graphMap.getGraph().getNode(startId),
                    graphMap.getGraph().getNode(endId),
                    speed, timer
            );
            graphMap.addToVehicles(vehicle);
        }
    }
}
