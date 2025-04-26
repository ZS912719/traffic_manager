package org.ravioles;

import static java.lang.Thread.sleep;

public class Timer {
    private int tic;

    public synchronized void inc() throws InterruptedException {
        tic++;
        sleep(1000);
    }

    public synchronized int get() {
        return tic;
    }
}
