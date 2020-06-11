package com.example.celeritem.Managers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Objects;

public class MovementManager {

    private final SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    // The following three variables are used to calculate if the user are moving or not
    private float accel = 10f;
    private float accelCurrent = SensorManager.GRAVITY_EARTH;
    private float accelLast = SensorManager.GRAVITY_EARTH;
    // The last time the acceleration was having a value above 3
    public Date timeSinceLastValueAbove3;

    public MovementManager(SensorManager sensor) {
        this.mSensorManager = sensor;
    }

    /**
     * Initializes the MovementManager
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initSensor() {
        makeListener();
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregisters the accelerometer listener
     */
    public void unregisterListener() {
        if (mSensorListener != null)
            mSensorManager.unregisterListener(mSensorListener);
    }

    /**
     * Make the the SensorEventListener and sets up the onSensorChanged method
     */
    private void makeListener() {
        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                accelLast = accelCurrent;
                accelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
                float delta = accelCurrent - accelLast;
                accel = accel * 0.9f + delta;
                if (accel > 3) { // If acceleration is greater than 3 it suggests the user is moving
                    timeSinceLastValueAbove3 = new Date();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }
}
