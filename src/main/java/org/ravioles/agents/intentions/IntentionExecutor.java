package org.ravioles.agents.intentions;

import org.ravioles.agents.Vehicle;
import java.util.LinkedList;
import java.util.List;

public class IntentionExecutor {
    Vehicle vehicle;
    private List<String> intentions = new LinkedList<>();

    public IntentionExecutor(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void enqueue(List<String> plan) {
        intentions.addAll(plan);
        if (intentions.isEmpty()) {
            return;
        }
        for (String intention : intentions) {
            if (intention.equals("go")) {
                this.vehicle.setCurrentState(this.vehicle.getVehicleStates().get(0)); // DRIVING
                this.vehicle.adjustSpeed();
            } else if (intention.equals("stop")) {
                this.vehicle.setCurrentState(this.vehicle.getVehicleStates().get(1));// WAITING
                this.vehicle.adjustSpeed();
            } else if (intention.equals("fin")){
                this.vehicle.setCurrentState(this.vehicle.getVehicleStates().get(2));// PARKED
                this.vehicle.adjustSpeed();
            }
        }

    }
}