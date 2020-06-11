package com.example.celeritem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Interfaces.IExerciseApplicationService;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Model.ExerciseLandmark;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Utility.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ExerciseResult result;
    private Spinner spinner;
    private LatLng location;
    private int chosenZoomLevel = -1; // temp value
    private boolean reWatch;
    private boolean hasPlayedSound = false;
    // Used for hash map keys in the saved bundle
    private final String ZOOM_LEVEL = "ZoomLevel";
    private final String HAS_PLAYED_SOUND = "HasPlayedSound";
    private TextView avgSpeed, distance, totalBreaks, timeBreaks, totalTime;
    private IOptionsApplicationService optionsService;
    private IExerciseApplicationService exerciseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (savedInstanceState != null) {
            chosenZoomLevel = savedInstanceState.getInt(ZOOM_LEVEL);
            hasPlayedSound = savedInstanceState.getBoolean(HAS_PLAYED_SOUND);
        }
        try {
            optionsService = ApplicationServiceFactory.getOptionsApplicationService();
            exerciseService = ApplicationServiceFactory.getExerciseApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "Error: " + e.getMessage());
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getViews();
        showResult();
        // Check if it's a re-watch
        reWatch = getIntent().getExtras().getBoolean(HistoryActivity.RE_WATCH);
        if (reWatch) { // if this is a re-watch of an old exercise - make save button disappear
            findViewById(R.id.saveButton).setVisibility(View.GONE);
        } else { // if this isn't a re-watch a victory sound will play
            playVictorySound();
        }
        mapFragment.getMapAsync(this);
    }

    /**
     * Gets the ExerciseResult from the intents extras and shows its information in the GUI
     */
    private void showResult() {
        ExerciseResult result = (ExerciseResult) getIntent().getExtras().getSerializable(ExerciseActivity.RESULT);
        Log.e(MainActivity.TAG, "RESULT: " + result.getExercise());
        String numberOfBreaks = "" + result.getTotalNumberOfBreaks();
        avgSpeed.setText(Utility.getFormattedSpeed(result.getAvgSpeed()));
        distance.setText(Utility.getFormattedDistance(result.getDistance()));
        totalBreaks.setText(numberOfBreaks);
        timeBreaks.setText(Utility.formatSeconds(result.getBreakInSeconds()));
        totalTime.setText(Utility.formatSeconds(result.getRunInSeconds()));
    }

    /**
     * Plays the victory sound - only once though in case of turning the phone
     */
    private void playVictorySound() {
        if (optionsService.getOptions().hasSound() && !hasPlayedSound) {
            SoundManager.playVictorySound(this);
            hasPlayedSound = true;
        }
    }

    private void getViews() {
        avgSpeed = findViewById(R.id.avgSpeedText);
        distance = findViewById(R.id.distanceText);
        totalBreaks = findViewById(R.id.breaksText);
        timeBreaks = findViewById(R.id.breaksTimeText);
        totalTime = findViewById(R.id.totalTimeText);
    }

    /**
     * Setting up the Google maps object by adding lines which represent the route as well as start and end point
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(MainActivity.TAG, "Map is ready");
        mMap = googleMap;
        // Fetch data from intent
        result = (ExerciseResult) getIntent().getSerializableExtra(ExerciseActivity.RESULT);
        ArrayList<ExerciseLandmark> allLandmarks = result.getLandmarks();

        PolylineOptions options = new PolylineOptions();
        options.clickable(true);
        // Draw on map
        for (int i = 0; i < allLandmarks.size(); i++) {
            location = new LatLng(allLandmarks.get(i).getLatitude(), allLandmarks.get(i).getLongitude());
            options.add(location);
            if (i == 0) {
                MarkerOptions start = new MarkerOptions().position(location).title("Start");
                googleMap.addMarker(start);
            }
            if (i == allLandmarks.size() - 1) {
                MarkerOptions end = new MarkerOptions().position(location).title("End");
                googleMap.addMarker(end);
            }
        }
        googleMap.addPolyline(options);
        CameraUpdate viewPoint = CameraUpdateFactory.newLatLngZoom(location, 17);
        googleMap.animateCamera(viewPoint);
        setupZoomLevel();
    }


    public void saveExercise(View v) {
        try {
            exerciseService.addResult(result);
            Utility.makeToast(this, "Result saved");
        } catch (Exception ex) {
            Utility.makeToast(this, "Error occurred: " + ex.getMessage());
        }
        finish();
    }

    public void deleteExercise(View v) {
        if (reWatch) { // If it isn't a re-watch the result simply wont be saved
            exerciseService.deleteResult(result);
            setResult(HistoryActivity.DELETED);
        }
        Utility.makeToast(this, "Result deleted");
        finish();
    }

    private void setupZoomLevel() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.zoom,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.zoomLevelsSpinner);
        spinner.setAdapter(adapter);
        if (chosenZoomLevel != -1) { // User has made a custom choice
            int count = spinner.getCount();
            for (int i = 0; i < count; i++) {
                if (Integer.parseInt(spinner.getItemAtPosition(i).toString()) == chosenZoomLevel) {
                    spinner.setSelection(i);
                }
            }
            initZoom();
        } else {
            spinner.setSelection(2);
            chosenZoomLevel = Integer.parseInt(spinner.getSelectedItem().toString());
        }
        initZoom();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                chosenZoomLevel = Integer.parseInt(spinner.getSelectedItem().toString());
                initZoom();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    /**
     * Changing the zoom level on Google Maps
     */
    private void initZoom() {
        CameraUpdate viewPoint = CameraUpdateFactory.newLatLngZoom(location, chosenZoomLevel * 3);
        mMap.animateCamera(viewPoint);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ZOOM_LEVEL, chosenZoomLevel);
        outState.putBoolean(HAS_PLAYED_SOUND, hasPlayedSound);
    }
}
