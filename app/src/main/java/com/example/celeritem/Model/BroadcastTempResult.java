package com.example.celeritem.Model;

import java.io.Serializable;

public class BroadcastTempResult implements Serializable {
    private double distance;
    private double speed;
    private int breaks;
    private int breakTimeSeconds;
    private int runTimeSeconds;
    private String state;

    public BroadcastTempResult(double distance, double speed, int breaks, int breakTimeSeconds, int runTimeSeconds, String state) {
        this.distance = distance;
        this.speed = speed;
        this.breaks = breaks;
        this.breakTimeSeconds = breakTimeSeconds;
        this.runTimeSeconds = runTimeSeconds;
        this.state = state;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public int getBreaks() {
        return breaks;
    }

    public int getBreakTimeSeconds() {
        return breakTimeSeconds;
    }

    public int getRunTimeSeconds() {
        return runTimeSeconds;
    }

    public String getState() {
        return state;
    }
}
