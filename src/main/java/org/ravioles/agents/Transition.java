package org.ravioles.agents;

public class Transition {
    String nextState;
    double probability;
    int reward;

    public Transition(String nextState, double probability, int reward) {
        this.nextState = nextState;
        this.probability = probability;
        this.reward = reward;
    }

    public String getNextState() {
        return nextState;
    }
    public double getProbability() {
        return probability;
    }
    public int getReward() {
        return reward;
    }
}
