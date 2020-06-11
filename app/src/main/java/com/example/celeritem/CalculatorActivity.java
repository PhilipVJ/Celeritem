package com.example.celeritem;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Model.MetActivity;
import com.example.celeritem.Utility.Utility;

import java.text.DecimalFormat;

public class CalculatorActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText bmiWeight, bmiHeight, kcalWeight, kcalMinutsActive;
    private TextView bmiResult, kcalResult;
    private MetActivity chosenActivity;

    private double varKcalResult, varBmiResult;
    // The following two variables are used then storing data in the Bundle and functions as keys
    private final String SAVEDBMI = "SAVEDBMI";
    private final String SAVEDKCAL = "SAVEDKCAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        bmiHeight = findViewById(R.id.cm_Height);
        bmiHeight.addTextChangedListener(new BMIWatcher());
        bmiWeight = findViewById(R.id.kg_Weight);
        bmiWeight.addTextChangedListener(new BMIWatcher());
        bmiResult = findViewById(R.id.bmi_Result);


        kcalWeight = findViewById(R.id.kg_Weight_CalorieBurnt);
        kcalWeight.addTextChangedListener(new KcalWatcher());
        kcalMinutsActive = findViewById(R.id.activity_time);
        kcalMinutsActive.addTextChangedListener(new KcalWatcher());
        spinner = findViewById(R.id.dropdown_activity);
        kcalResult = findViewById(R.id.kcal_Result);
        setupMETSpinner();

        if (savedInstanceState != null) {
            varBmiResult = savedInstanceState.getDouble(SAVEDBMI);
            String resultBmi = new DecimalFormat("##.##").format(varBmiResult);
            bmiResult.setText(resultBmi);
            varKcalResult = savedInstanceState.getDouble(SAVEDKCAL);
            String resultKcal = new DecimalFormat("##.##").format(varKcalResult);
            kcalResult.setText(resultKcal);
        }

    }

    /**
     * Setting the kcal result. Is only called when there is correct input.
     */
    private void setKcalResult() {
        try {
            double weight = Double.parseDouble(kcalWeight.getText().toString());
            double activeMinuts = Double.parseDouble(kcalMinutsActive.getText().toString());
            varKcalResult = Utility.getKcalToExercise(activeMinuts, chosenActivity, weight);
            String result = new DecimalFormat("##.##").format(varKcalResult);
            kcalResult.setText(result);
        } catch (Exception ex) {
            Log.e(MainActivity.TAG, ex.getMessage());
        }
    }

    /**
     * Setting the BMI result. Is only called when there is correct input
     */
    private void setBMIResult() {
        try {
            double weight = Double.parseDouble(bmiWeight.getText().toString());
            double height = Double.parseDouble(bmiHeight.getText().toString());
            varBmiResult = Utility.getBmi(weight, height);
            String result = new DecimalFormat("##.##").format(varBmiResult);
            bmiResult.setText(result);
        } catch (Exception ex) {
            Log.e(MainActivity.TAG, ex.getMessage());
        }
    }

    /**
     * Setting up the dropdown box with the various exercise types
     */
    private void setupMETSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.metArray,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String metReq = spinner.getSelectedItem().toString();
                chosenActivity = Utility.getMetScoreEnumFromString(metReq);
                setKcalResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(SAVEDBMI, varBmiResult);
        outState.putDouble(SAVEDKCAL, varKcalResult);
    }

    // TextWatchers used for real time updates of results
    private class BMIWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setBMIResult();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class KcalWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setKcalResult();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
