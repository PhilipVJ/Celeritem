package com.example.celeritem.Model;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;

public class ExerciseLandmark implements Serializable {

    private double latitude;
    private double longitude;
    private Date timeStamp;

    public ExerciseLandmark(double latitude, double longitude, Date timeStamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Location getAsLocation(){
        Location location = new Location("");//provider name is unnecessary
        location.setLatitude(latitude);//your coords of course
        location.setLongitude(longitude);
        return location;
    }


}
