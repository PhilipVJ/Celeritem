package com.example.celeritem.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.ExerciseActivity;
import com.example.celeritem.Interfaces.IViewCallBack;
import com.example.celeritem.MainActivity;
import com.example.celeritem.Managers.MovementManager;
import com.example.celeritem.Managers.NotifyManager;
import com.example.celeritem.Misc.GPSListener;
import com.example.celeritem.Model.AppOptions;
import com.example.celeritem.Model.BroadcastTempResult;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.ExerciseLandmark;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Model.LandmarkCalculation;
import com.example.celeritem.Model.State;
import com.example.celeritem.Utility.Utility;

import org.apache.commons.lang3.time.StopWatch;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This service class is used for real-time tracking
 */
public class ExerciseService extends Service implements IViewCallBack {
    private MovementManager movementManager;
    // State
    private ArrayList<ExerciseLandmark> allLandmarks = new ArrayList<>();
    private Location currentLocation;
    private double currentSpeed;
    private Date currentTime;
    private Exercise chosenExercise;
    private boolean stopped = false;
    private double totalDistance = 0;
    private int nextVibrationKm = 1;
    private State currentState;
    private int numberOfBreaks;
    private StopWatch breakTimer;
    private double totalBreaksInSeconds;
    private StopWatch runTimer = new StopWatch();
    // Managers and options
    private LocationManager locationManager;
    private LocationListener locationListener;
    private NotifyManager nManager;
    private AppOptions options;
    private final IBinder binder = new LocalBinder();
    // public final Strings used for broadcasting intents to make sure the inputs aren't misspelled
    public final static String GO = "Go";
    public final static String MESSAGE = "ServiceMessage";
    public final static String TEMP_RESULT = "TempResult";
    public final static String EXERCISE_DONE = "Done";

    /**
     * This method is called when starting the service. Here we setup the tracking.
     * @param intent - used for getting the chosen exercise
     * @param flags - used for internal use when calling the base class
     * @param startId - used for internal use when calling the base class
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get options
        try {
            options = ApplicationServiceFactory.getOptionsApplicationService().getOptions();
        } catch (InvalidServiceUsageException e) {
            Log.d(MainActivity.TAG, e.getMessage());
        }

        String exercise = intent.getExtras().getString(ExerciseActivity.CHOSEN_EXERCISE);
        chosenExercise = Utility.getExerciseEnumFromString(exercise);
        nManager = new NotifyManager(this);
        startForeground(1, nManager.createNotification("Waiting for GPS to start"));
        startGPSListening();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startTracking();
            }
        });
        thread.start();
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }


    private void startTracking() {
        // Wait for the GPS to start up
        while (currentLocation == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Send begin message to user
        Intent go = new Intent(GO);
        sendBroadcast(go);

        initializeExercise();
        // Begin tracking
        while (!stopped) {
            Date rightNow = new Date();
            // If it is more than 3 seconds ago since last high acceleration value (above 3)
            // it will be measured as a break
            long timeElapsed = rightNow.getTime() - movementManager.timeSinceLastValueAbove3.getTime();
            if (timeElapsed > 3 * 1000) {
                if (currentState == State.Moving) {
                    breakTimer = new StopWatch();
                    breakTimer.start();
                    numberOfBreaks++;
                }
                currentState = State.Pause;

            } else {
                if (currentState == State.Pause) {
                    breakTimer.stop();
                    totalBreaksInSeconds += breakTimer.getTime(TimeUnit.SECONDS);
                }
                currentState = State.Moving;
            }
            broadCastInfo();
            // Update notification
            nManager.updateNotification("Distance: " + new DecimalFormat("##.##").format(totalDistance) + " m"
                    + "  Time: " + Utility.formatSeconds((int) runTimer.getTime(TimeUnit.SECONDS)));
            // Add vibration
            if (options.hasKilometerNotification()) {
                if (totalDistance >= nextVibrationKm * 1000) {
                    addVibration(1000);
                    nextVibrationKm++;
                }
            }
            // Wait 1 second before broadcasting again
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // The exercise has been stopped
        runTimer.stop();
        int runTime = (int) runTimer.getTime(TimeUnit.SECONDS);
        if (breakTimer != null && breakTimer.isStarted()) { // In case the user stops the exercise during a break
            breakTimer.stop();
            totalBreaksInSeconds += breakTimer.getTime(TimeUnit.SECONDS);
        }
        // Calculate average speed
        Date firstTimeStamp = allLandmarks.get(0).getTimeStamp();
        Date lastTimeStamp = allLandmarks.get(allLandmarks.size() - 1).getTimeStamp();
        double avgSpeedOverAll = Utility.getAverageSpeed(totalDistance, firstTimeStamp, lastTimeStamp); // avg speed including breaks

        // Send the result back to the exercise activity
        ExerciseResult result = new ExerciseResult(allLandmarks, chosenExercise, avgSpeedOverAll, numberOfBreaks,
                (int) totalBreaksInSeconds, runTime, new Date(), totalDistance);
        Intent i = new Intent(EXERCISE_DONE);
        i.putExtra(ExerciseActivity.RESULT, result);
        sendBroadcast(i);

        // Stop service
        stopService();
    }

    /**
     * Used to setup the exercise about to happen. It is changing various variables values and starting the timer
     */
    private void initializeExercise() {
        runTimer.start();
        numberOfBreaks = 0;
        totalBreaksInSeconds = 0;
        movementManager.timeSinceLastValueAbove3 = new Date();
        currentState = State.Moving;
    }

    /**
     * Stops the service and all its listeners
     */
    private void stopService() {
        stopListening();
        stopForeground(true);
        stopSelf();
    }

    /**
     * Broadcast information to the ExerciseActivity
     */
    private void broadCastInfo() {
        Intent i = new Intent(MESSAGE);
        int breaksInSecondsTotal = (int) totalBreaksInSeconds;
        if (currentState == State.Pause) {
            breaksInSecondsTotal += (int) breakTimer.getTime(TimeUnit.SECONDS);
        }
        BroadcastTempResult tempResult = new BroadcastTempResult(totalDistance, currentSpeed, numberOfBreaks,
                breaksInSecondsTotal, (int) runTimer.getTime(TimeUnit.SECONDS), "" + currentState);
        i.putExtra(TEMP_RESULT, tempResult);
        sendBroadcast(i); // Only for UI
    }

    /**
     * Used for stopping the service from the ExerciseActivity
     */
    public void stopOperation() {
        stopped = true;
    }

    /**
     * Checks permissions for the GPS usage and creates a new GPSListener if granted
     */
    protected void startGPSListening() {
        boolean GPSPermissionGiven = true;
        // Make sure permission is granted
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            GPSPermissionGiven = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        locationListener = new GPSListener(this);
        if (GPSPermissionGiven)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    (11 - options.getAccuracy()) * 1000,
                    11 - options.getAccuracy(),
                    locationListener);
        else {
            Log.d("TAG", "Could not register location listener..");
            stopService();
        }
    }

    /**
     * This method is called from the GPSListener upon changes. The location object is the
     * updated location
     * @param location
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setCurrentLocation(Location location) {
        Date rightNow = new Date();
        if (currentLocation != null) {
            // check distance and speed between last and current location
            LandmarkCalculation calc = Utility.getSpeedAndDistance(currentLocation, location, currentTime, rightNow);
            totalDistance += calc.getDistance();
            currentSpeed = calc.getAvgSpeed();
        }
        currentLocation = location;
        currentTime = rightNow;
        ExerciseLandmark landmark = new ExerciseLandmark(location.getLatitude(), location.getLongitude(), currentTime);
        allLandmarks.add(landmark);
    }

    /**
     * Vibrates the phone in the x amount of milliseconds
     * @param time the amount of milliseconds
     */
    private void addVibration(int time) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
    }

    /**
     * Stops both the accelerometer listener and gps listener
     */
    private void stopListening() {
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (movementManager != null) {
            movementManager.unregisterListener();
        }
    }

    /**
     * Setting up various managers for internal use.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        movementManager = new MovementManager((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        movementManager.initSensor();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * This is used to access the service from within the ExerciseActivity
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder implements Serializable {
        public ExerciseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ExerciseService.this;
        }
    }
}
