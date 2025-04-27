package org.ravioles.agents.desires;

import org.graphstream.graph.Node;

public class Desire {
    private final Node destination;
    private boolean active;

    public Desire(Node destination) {
        this.destination = destination;
        this.active = true;
    }

    public void complete() {
        this.active = false;
    }

    // Getters
    public boolean isActive() {
        return active;
    }


    public Node getDescription() {
        return destination;
    }
}
