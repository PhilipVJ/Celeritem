package com.example.celeritem.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ExerciseResult implements Serializable {
    private ArrayList<ExerciseLandmark> landmarks;
    private Exercise exercise;
    private double avgSpeed;
    private int totalNumberOfBreaks;
    private int breakInSeconds;
    private int runInSeconds;
    private Date date;
    private double distance;
    private int id;

    public ExerciseResult(ArrayList<ExerciseLandmark> landmarks, Exercise exercise, double avgSpeed, int totalNumberOfBreaks, int breakInSeconds, int runInSeconds, Date date, double distance) {
        this.landmarks = landmarks;
        this.exercise = exercise;
        this.avgSpeed = avgSpeed;
        this.totalNumberOfBreaks = totalNumberOfBreaks;
        this.breakInSeconds = breakInSeconds;
        this.runInSeconds = runInSeconds;
        this.date = date;
        this.distance = distance;
    }

    public ArrayList<ExerciseLandmark> getLandmarks() {
        return landmarks;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public int getTotalNumberOfBreaks() {
        return totalNumberOfBreaks;
    }

    public int getBreakInSeconds() {
        return breakInSeconds;
    }

    public int getRunInSeconds() {
        return runInSeconds;
    }

    public Date getDate() {
        return date;
    }

    public double getDistance() {
        return distance;
    }
}
