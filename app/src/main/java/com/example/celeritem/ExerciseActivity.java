package com.example.celeritem;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Model.BroadcastTempResult;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Services.ExerciseService;
import com.example.celeritem.Utility.Utility;

import java.util.ArrayList;

/**
 * This activity shows the live feed of the tracking. It changes the UI based on its internal state. As an example - if
 * the exercise hasn't begun yet - it will show a START button.
 */
public class ExerciseActivity extends AppCompatActivity {

    private static ExerciseService.LocalBinder myService;
    private TextView distance, breaks, breaksTimer, runTimer, speed, state;
    private TextView waitingInfoBox;
    private MessageHandler messageReceiver = new MessageHandler();
    private boolean serviceIsRunning = false;
    private Context context;
    private static ServiceConnection mServiceConnection;
    private Exercise chosenExercise;
    private Button button;
    private GridLayout infoBox;
    private boolean done = false;
    private boolean showStopButton = false;
    private boolean showingWaitInfo = false;
    // Public static strings used for intents and saving in the Bundle
    public static final String RESULT = "RESULT";
    public static final String CHOSEN_EXERCISE = "Exercise";
    public static final String RUNNING = "Running";
    public static final String SHOW_STOP_BUTTON = "ShowStopButton";
    public static final String WAIT_INFO = "WaitInfo";
    // ApplicationService
    private IOptionsApplicationService optionsService;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save UI relevant information
        outState.putBoolean(RUNNING, serviceIsRunning);
        outState.putBoolean(SHOW_STOP_BUTTON, showStopButton);
        outState.putBoolean(WAIT_INFO, showingWaitInfo);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        context = this;
        checkPermissions();
        if (savedInstanceState != null) { // If a Bundle has been saved we extract the UI relevant information from it
            serviceIsRunning = savedInstanceState.getBoolean(RUNNING);
            showStopButton = savedInstanceState.getBoolean(SHOW_STOP_BUTTON);
            showingWaitInfo = savedInstanceState.getBoolean(WAIT_INFO);
        }
        // Get services
        try {
            optionsService = ApplicationServiceFactory.getOptionsApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "Error occurred: " + e.getMessage());
        }
        // Get exercise from Intent
        String exercise = getIntent().getExtras().getString(CHOSEN_EXERCISE);
        chosenExercise = Utility.getExerciseEnumFromString(exercise);

        setupBroadcastReceiver();

        // Setup GUI
        getAllViews();
        setGUI();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });
    }

    /**
     * Setting up an broadcast receiver used for getting messages from the service.
     */
    private void setupBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ExerciseService.MESSAGE);
        filter.addAction(ExerciseService.EXERCISE_DONE);
        filter.addAction(ExerciseService.GO);
        registerReceiver(messageReceiver, filter);
    }

    /**
     * Getting all the views the activity depends upon
     */
    private void getAllViews() {
        distance = findViewById(R.id.distanceText);
        runTimer = findViewById(R.id.totalTimeText);
        breaks = findViewById(R.id.breaksText);
        breaksTimer = findViewById(R.id.breaksTimeText);
        speed = findViewById(R.id.speedText);
        state = findViewById(R.id.stateText);
        button = findViewById(R.id.button);
        infoBox = findViewById(R.id.infoBox);
        waitingInfoBox = findViewById(R.id.waitingBox);
    }


    /**
     *  The button changes functionality when pressed. First - it's a Start button - and then it becomes a Stop button.
     *     The serviceIsRunning instance variable declares the button-state.
     */
    private void buttonClick() {
        if (serviceIsRunning) {
            stopService();
        } else {
            if (optionsService.getOptions().hasSound()) {
                SoundManager.playClickSound(context);
            }
            startService();
            // Showing wait information while the GPS is starting up
            showWaitInformation();
            button.setVisibility(View.GONE); // Gone until the GPS has started
        }
    }

    /**
     * Making sure that the user will be notified to wait for the GPS to start up
     */
    private void showWaitInformation() {
        showingWaitInfo = true;
        waitingInfoBox.setVisibility(View.VISIBLE);
    }

    private void setGUI() {
        if (serviceIsRunning) {
            button.setText(R.string.stop);
            // It shouldn't be possible to stop the exercise until the traveled distance is greater than 0
            if (showStopButton)
                button.setVisibility(View.VISIBLE);
            else
                button.setVisibility(View.INVISIBLE); // Invisible until distance > 0
            if (showingWaitInfo)
                waitingInfoBox.setVisibility(View.VISIBLE);
            else
                infoBox.setVisibility(View.VISIBLE);

        } else {
            button.setText(R.string.start);
        }
        // Set image - only in portrait mode
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setImage();
        }
    }

    /**
     * Setting up the chosen exercises image
     */
    private void setImage() {
        ImageView image = findViewById(R.id.exerciseSymbol);
        switch (chosenExercise) {
            case Run:
                image.setImageResource(R.drawable.run);
                break;
            case Bike:
                image.setImageResource(R.drawable.bike);
                break;

            case Walk:
                image.setImageResource(R.drawable.walk);
                break;
        }
    }

    /**
     * Show the tracking information screen which means that the exercise has begun
     */
    public void showInfoScreen() {
        waitingInfoBox.setVisibility(View.GONE);
        button.setVisibility(View.INVISIBLE);
        button.setText(R.string.stop);
        infoBox.setVisibility(View.VISIBLE);
        showingWaitInfo = false;
    }

    public void stopService() {
        myService.getService().stopOperation();
    }


    public void startService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                myService = (ExerciseService.LocalBinder) binder;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                myService = null;
            }
        };
        serviceIsRunning = true;
        Intent i = new Intent(this, ExerciseService.class);
        i.putExtra(CHOSEN_EXERCISE, "" + chosenExercise);
        getApplicationContext().bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        this.startForegroundService(i);
    }

    static int PERMISSION_REQUEST_CODE = 1;

    /**
     * This method checks the permissions needed to do the exercise, which is the usage of the GPS
     */
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) return;

        ArrayList<String> permissions = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissions.size() > 0)
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
    }

    /**
     * If the user denies permission a Toast will be shown and the Activity will be closed
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (grantResults[0] == -1) {
            Utility.makeToast(this, "You need to accept GPS usage for this application to work");
            finish();
        }
    }

    /**
     * This class is the broadcast receiver.
     */
    public class MessageHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equalsIgnoreCase(ExerciseService.GO))
                    showInfoScreen();
                if (action.equalsIgnoreCase(ExerciseService.MESSAGE))
                    handleReceivedMessage(intent);
                if (action.equalsIgnoreCase(ExerciseService.EXERCISE_DONE))
                    openResultActivity(intent);
            }
        }
    }

    /**
     * This message handles the message from the service, which is an BroadcastTempResult object.
     * @param intent
     */
    private void handleReceivedMessage(Intent intent) {
        Bundle extra = intent.getExtras();
        BroadcastTempResult tempResult = (BroadcastTempResult) extra.get(ExerciseService.TEMP_RESULT);
        if (tempResult.getDistance() > 0) { // The stop button shouldn't be visible until at least two coordinates have been registered
            showStopButton = true;
            button.setVisibility(View.VISIBLE);
        }
        // Update GUI
        updateGUI(tempResult);
    }

    private void updateGUI(BroadcastTempResult tempResult) {
        distance.setText(Utility.getFormattedDistance(tempResult.getDistance()));
        speed.setText(Utility.getFormattedSpeed(tempResult.getSpeed()));
        String totalBreaks = "" + tempResult.getBreaks();
        runTimer.setText(Utility.formatSeconds(tempResult.getRunTimeSeconds()));
        breaksTimer.setText(Utility.formatSeconds(tempResult.getBreakTimeSeconds()));
        state.setText(tempResult.getState());
        breaks.setText(totalBreaks);
    }

    /**
     * Opens the ResultActivity when the exercise is done. The intent object contains the ExerciseResult object in its Extras.
     * @param intent
     */
    private void openResultActivity(Intent intent) {
        ExerciseResult result = (ExerciseResult) intent.getSerializableExtra(RESULT);
        Intent x = new Intent(ExerciseActivity.this.context, ResultActivity.class);
        getApplicationContext().unbindService(mServiceConnection); // Important to unbind the connection to the service
        x.putExtra(RESULT, result);
        startActivity(x);
        done = true;
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
        // App is getting closed during activity
        if (isFinishing() && !done) {
            clearService();
        }
    }

    /**
     * Cutting the connection to the service and makes sure it stops
     */
    private void clearService() {
        if (mServiceConnection != null && serviceIsRunning) {
            myService.getService().stopOperation();
            if (mServiceConnection != null) {
                getApplicationContext().unbindService(mServiceConnection);
            }
            serviceIsRunning = false;
        }
    }
}


