package org.soulsoftware.spigot.core.Utils;

public class Benchmark {
    private Long startTimeMill;
    private Long endTimeMill;

    public void start() {
        startTimeMill = System.currentTimeMillis();
    }

    public void end() {
        endTimeMill = System.currentTimeMillis();
    }

    public Long result() {
        if (startTimeMill == null || endTimeMill == null)
            throw new SecurityException("Benchmark not started/completed" +
                    " properly!");
        return endTimeMill - startTimeMill;
    }
}
