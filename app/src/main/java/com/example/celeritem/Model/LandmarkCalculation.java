package com.example.celeritem.Model;

import java.util.Objects;

public class LandmarkCalculation {

     private double avgSpeed;
     private double distance;

    public LandmarkCalculation(double avgSpeed, double distance) {
        this.avgSpeed = avgSpeed;
        this.distance = distance;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandmarkCalculation that = (LandmarkCalculation) o;
        return Double.compare(that.avgSpeed, avgSpeed) == 0 &&
                Double.compare(that.distance, distance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(avgSpeed, distance);
    }
}
