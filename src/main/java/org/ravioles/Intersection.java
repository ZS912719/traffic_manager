package org.ravioles;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.ravioles.agents.Vehicle;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


public class Intersection implements ActionListener {
    Node tl1; // traffic light
    Node tl2;
    Node tl3;
    Node tl4;
    GraphMap graph;
    List<String> actions = List.of("a1", "a2", "a3", "a4");
    List<Edge> intersections;
    List<String> states = List.of("s_10", "s_10?", "s_01", "s_01?");
    // Explanation by example: s_01 means road 1,3 are passable while road 2,4 are not.
    // State "s_01?" means road 1,3 are passable while road 2,4 are not but they are switching (yellow light).
    String state = "s_10";
    int currentTick;

    private static final double EPSILON = 0.1;
    private static final double ALPHA = 0.5;
    private static final double GAMMA = 0.9;
    private static final int DECISION_INTERVAL = 30;
    private Map<String, Map<String, Double>> qTable = new HashMap<>();
    private int countDown = 0;

    private static final Map<String, List<String>> ALLOWED_ACTIONS = Map.of(
            "s_10",  List.of("a1", "a2"),
            "s_10?", List.of("a3"),
            "s_01",  List.of("a3", "a4"),
            "s_01?", List.of("a1")
    );  // this defines the order of actions, at a certain state we can only take proper actions.


    public Intersection(GraphMap graph, Node tl1, Node tl2, Node tl3, Node tl4, Timer timer) {
        this.graph = graph;

        this.tl1 = tl1;
        this.tl2 = tl2;
        this.tl3 = tl3;
        this.tl4 = tl4;

        this.intersections = getIntersections();

        for (String state : states) {
            Map<String, Double> actionMap = new HashMap<>();
            for (String action : actions) {
                actionMap.put(action, 0.0);
            }
            qTable.put(state, actionMap);
        }

        timer.addActionListener(this);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        this.currentTick = Main.tic;
        act();
    }

    /**
     * Executes the primary logic for controlling traffic lights using a reinforcement learning approach.
     * This method adjusts the system's behavior based on the current state, rewards, and penalties.
     *
     * - If a countdown timer is active, it decrements the timer and exits the method.
     * - Retrieves the current traffic light state and determines the optimal action based on the policy.
     * - Toggles the traffic lights to match the determined action.
     * - Calculates the reward and punishment based on the system's new state and updates the Q-table.
     * - Resets the countdown timer based on the new state; certain states require increased decision intervals.
     */
    public void act() {
        if (countDown > 0) {
            countDown--;
            return;
        }

        String currentState = getState();

        String action = selectAction(currentState);
        toggleLight(action);
        String newState = getState();
        double reward = calculateReward() - calculatePunish();

        updateQValue(currentState, action, reward, newState);

        this.countDown = newState.equals("s_10?")||newState.equals("s_01?") ? 10: DECISION_INTERVAL;
    }

    /**
     * Selects an action based on the current state using an ε-greedy policy.
     * With a probability of ε, it selects a random action from the list of allowed actions.
     * Otherwise, it selects the action with the highest Q-value for the given state.
     *
     * @param state the current state for which an action needs to be selected
     * @return the selected action as a string
     */
    private String selectAction(String state) {
        List<String> allowed = ALLOWED_ACTIONS.get(state);
        if (Math.random() < EPSILON) {
            int index = (int) (Math.random() * allowed.size());
            return allowed.get(index);
        } else {
            return allowed.stream()
                    .max(Comparator.comparing(a -> qTable.get(state).get(a)))
                    .get();
        }
    }

    /**
     * Updates the Q-value in the Q-table for a specific state-action pair based on the received reward
     * and the maximum Q-value of the next state.
     *
     * @param state the current state for which the Q-value is being updated
     * @param action the action taken in the current state
     * @param reward the reward received after taking the action
     * @param nextState the resulting state after taking the action
     */
    private void updateQValue(String state, String action, double reward, String nextState) {
        double oldQ = qTable.get(state).get(action);
        double maxNextQ = qTable.get(nextState).values().stream().mapToDouble(v -> v).max().orElse(0.0);
        double newQ = (1 - ALPHA) * oldQ + ALPHA * (reward + GAMMA * maxNextQ);
        qTable.get(state).put(action, newQ);
    }

    public void toggleLight(String action){
        switch (action) {
            case "a1":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(0,255,0);"); //green
                this.tl1.setAttribute("color", "green");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                this.tl3.setAttribute("color", "green");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(255,0,0);"); // red
                this.tl2.setAttribute("color", "red");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl4.setAttribute("color", "red");
                this.state = "s_10";
                break;
            case "a2":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(255,128,0);"); // orange
                this.tl1.setAttribute("color", "orange");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(255,128,0);");
                this.tl3.setAttribute("color", "orange");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl2.setAttribute("color", "red");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl4.setAttribute("color", "red");
                this.state = "s_10?";
                break;
            case "a3":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl1.setAttribute("color", "red");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl3.setAttribute("color", "red");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                this.tl2.setAttribute("color", "green");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(0,255,0);");
                this.tl4.setAttribute("color", "green");
                this.state = "s_01";
                break;
            case "a4":
                this.tl1.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl1.setAttribute("color", "red");
                this.tl3.setAttribute("ui.style", "fill-color: rgb(255,0,0);");
                this.tl3.setAttribute("color", "red");
                this.tl2.setAttribute("ui.style", "fill-color: rgb(255,128,0);");
                this.tl2.setAttribute("color", "orange");
                this.tl4.setAttribute("ui.style", "fill-color: rgb(255,128,0);");
                this.tl4.setAttribute("color", "orange");
                this.state = "s_01?";
                break;
        }
    }

    /**
     * let a vehicle pass at green light is 10pts
     * let a vehicle pass at orange light is 1pts
     */
    public double calculateReward() {
        List<Node> trafficLights = getTrafficLights();
        for (Vehicle v : graph.getVehicles()) {
            Element e = v.getVehicle().getAttachment();
            if (e instanceof Node tl && trafficLights.contains(e)) {
                String color = tl.getAttribute("color");
                return switch (color) {
                    case "green" -> 10;
                    case "orange" -> 1;
                    default -> 0;
                };
            }
        }
        return 0;
    }

    /**
     * calculate punish points by accumulate the waiting time of each vehicle
     */
    public double calculatePunish() {
        List<Edge> enteringEdges = new ArrayList<>();
        double punish = 0;


        // we consider only the edges entering the intersections
        for (Node n : getTrafficLights()) {
            for (Edge e : n.getEnteringEdgeSet()) {
                if (!intersections.contains(e)) {
                    enteringEdges.add(e);
                }
            }
        }

        for (Vehicle v : graph.getVehicles()) {
            int waitingTime = v.getWaitingTime();
            if (waitingTime > 0) {
                Element e = v.getVehicle().getAttachment();
                if (e instanceof Edge && enteringEdges.contains(e)) {
                    punish = punish + waitingTime;
                }
            }
        }
        return punish;
    }

    public List<Node> getTrafficLights() {
        return List.of(tl1, tl2, tl3, tl4);
    }

    public String getState() {
        return this.state;
    }
}