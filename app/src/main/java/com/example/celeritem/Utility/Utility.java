package com.example.celeritem.Utility;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.example.celeritem.Exceptions.InvalidInputException;
import com.example.celeritem.Exceptions.InvalidOptionsException;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.LandmarkCalculation;
import com.example.celeritem.Model.MetActivity;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

    public static void makeToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static String formatSeconds(int seconds) {
        int numberOfMinutes = seconds / 60;
        if (numberOfMinutes == 0 && seconds >= 10)
            return "0:" + seconds;
        int numberOfSeconds = seconds - (numberOfMinutes * 60);
        if (numberOfSeconds < 10) {
            return numberOfMinutes + ":0" + numberOfSeconds;
        } else {
            return numberOfMinutes + ":" + numberOfSeconds;
        }
    }

    public static String formatDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        return dayOfMonth + "/" + month + "-" + year;
    }

    public static LandmarkCalculation getSpeedAndDistance(Location markOne, Location markTwo, Date timeOne, Date timeTwo) {
        double distance = markOne.distanceTo(markTwo);
        long startTime = timeOne.getTime();
        long endTime = timeTwo.getTime();
        float timeInSeconds = (endTime - startTime) / 1000.0f;
        double metersPerSecond = distance / timeInSeconds;
        return new LandmarkCalculation(metersPerSecond * 3.6, distance);
    }

    public static double getAverageSpeed(double distance, Date start, Date end) {
        long startTime = start.getTime();
        long endTime = end.getTime();
        float timeInSeconds = (endTime - startTime) / 1000.0f;
        double metersPerSecond = distance / timeInSeconds;
        return metersPerSecond * 3.6;
    }

    public static String getFormattedDistance(double distance) {
        return new DecimalFormat("##.##").format(distance) + " m";
    }

    public static String getFormattedSpeed(double speed) {
        return new DecimalFormat("##.##").format(speed) + " km/t";
    }

    public static Double getBmi(double weight, double height) throws InvalidInputException {
        if (weight <= 0 || height <= 0) {
            throw new InvalidInputException("Invalid input");
        }
        double bmiResult = weight / (height*height) * 10000; //To get the right "number" multiply by 10000 because of different measurement "tools"
        return bmiResult;
    }

    public static Double getKcalToExercise(double duration, MetActivity met, double weight) throws InvalidInputException {
        double metResult = 0;
        if (met == MetActivity.WalkingSlow) {
            metResult = 2.3; //walking with 2.7 km/h
        }
        if (met == MetActivity.Walking) {
            metResult = 2.9; //walking with 4 km/h
        }
        if (met == MetActivity.WalkingFast) {
            metResult = 3.6; //walking with 5.5 km/h
        }
        if (met == MetActivity.Jogging) {
            metResult = 6.0; // running with 6.5 km/h
        }
        if (met == MetActivity.Running) {
            metResult = 11.8; // running with 12.89 km/h
        }
        if (met == MetActivity.BicyclingSlow) {
            metResult = 3.5; //cycling with 8 km/h
        }
        if (met == MetActivity.Bicycling) {
            metResult = 5.8; // cycling with 15 km/h
        }
        if (met == MetActivity.BicyclingFast) {
            metResult = 12; // cycling with 26 -> 31 km/h
        }

        if (weight <= 0 || duration <= 0) {
            throw new InvalidInputException("Value");
        }

        double kcalBurned = duration * (metResult * 3.5 * weight)/200;

        return kcalBurned;
    }

    public static Exercise getExerciseEnumFromString(String input) {
        Exercise formatted = null;
        switch (input.toLowerCase()) {
            case "run":
                formatted = Exercise.Run;
                break;
            case "bike":
                formatted = Exercise.Bike;
                break;
            case "walk":
                formatted = Exercise.Walk;
                break;
        }
        return formatted;
    }

    public static MetActivity getMetScoreEnumFromString(String metReq) {
        MetActivity metFormat = null;
        switch (metReq) {
            case "Walking slow":
                metFormat = MetActivity.WalkingSlow;
                break;
            case "Walking":
                metFormat = MetActivity.Walking;
                break;
            case "Walking fast":
                metFormat = MetActivity.WalkingFast;
                break;
            case "Jogging":
                metFormat = MetActivity.Jogging;
                break;
            case "Running":
                metFormat = MetActivity.Running;
                break;
            case "Bicycling slow":
                metFormat = MetActivity.BicyclingSlow;
                break;
            case "Bicycling":
                metFormat = MetActivity.Bicycling;
                break;
            case "Bicycling fast":
                metFormat = MetActivity.BicyclingFast;
                break;
        }
        return metFormat;
    }
}
