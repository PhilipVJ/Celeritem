package com.example.celeritem.Misc;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.example.celeritem.Interfaces.IViewCallBack;

/**
 * The apps LocationListener implementation. Is used for getting GPS coordinates.
 */
public class GPSListener implements LocationListener{

    private final IViewCallBack _vcb; // Used for callbacks upon location changes

    public GPSListener(IViewCallBack view) {
        _vcb = view;
    }

    /**
     * When the location has changed the IViewCallBack objects setCurrentLocation will be called
     * with the new location as an argument
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        _vcb.setCurrentLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
