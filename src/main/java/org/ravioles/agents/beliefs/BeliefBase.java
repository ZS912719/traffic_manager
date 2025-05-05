package org.ravioles.agents.beliefs;

import org.ravioles.GraphMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeliefBase {
    GraphMap graph;
    Map<String, Object> beliefs =new ConcurrentHashMap<>();

    public BeliefBase(GraphMap graph) {
        this.graph = graph;
    }

    public GraphMap getGraph() {
        return this.graph;
    }


    public void addBelief(String key, Object value) {
        beliefs.put(key, value);
    }

    public Object getBelief(String key) {
        return this.beliefs.get(key);
    }
}
