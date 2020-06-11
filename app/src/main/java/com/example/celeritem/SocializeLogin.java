package com.example.celeritem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Interfaces.ISocializeApplicationService;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Exceptions.InvalidRequestException;
import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Interfaces.ISuccessListener;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.Gender;
import com.example.celeritem.Model.SocializeRequest;
import com.example.celeritem.Utility.Utility;

public class SocializeLogin extends AppCompatActivity {
    private Spinner genderInput, wantsToInput;
    private EditText age, name, phone, city;
    private CheckBox saveInput;
    private LinearLayout loadingSection;
    private ScrollView mainSection;
    private Gender gender;
    private Exercise exercise;
    private String id;
    private SocializeRequest savedInput;
    // Services
    private IOptionsApplicationService optionsService;
    private ISocializeApplicationService socializeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialize_login);
        try {
            optionsService = ApplicationServiceFactory.getOptionsApplicationService();
            socializeService = ApplicationServiceFactory.getSocializeApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "Error occurred: " + e.getMessage());
        }
        getViews();
        setupGenderSpinner();
        setupExerciseSpinner();
        savedInput = socializeService.getLastSavedRequest();
        if (savedInput != null) {
            setOldInput();
        }
    }

    /**
     * If there is previous saved request its input will be set in the GUI
     */
    private void setOldInput() {
        name.setText(savedInput.getName());
        String ageText = "" + savedInput.getAge();
        age.setText(ageText);
        city.setText(savedInput.getCity());
        String phoneText = "" + savedInput.getPhoneNumber();
        phone.setText(phoneText);

        switch (savedInput.getWantsTo()) {
            case Bike:
                wantsToInput.setSelection(2);
                break;
            case Walk:
                wantsToInput.setSelection(0);
                break;
            case Run:
                wantsToInput.setSelection(1);
                break;
        }

        if (savedInput.getGender() == Gender.Male)
            genderInput.setSelection(0);
        else
            genderInput.setSelection(1);
    }

    private void getViews() {
        genderInput = findViewById(R.id.genderInput);
        wantsToInput = findViewById(R.id.wantsToInput);
        age = findViewById(R.id.ageInput);
        name = findViewById(R.id.nameInput);
        phone = findViewById(R.id.phoneInput);
        city = findViewById(R.id.cityInput);
        saveInput = findViewById(R.id.saveInputCheckBox);
        loadingSection = findViewById(R.id.loadingSection);
        mainSection = findViewById(R.id.mainSection);
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.genders,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderInput.setAdapter(adapter);

        genderInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String textGender = genderInput.getSelectedItem().toString();
                gender = textGender.equals("Male") ? Gender.Male : Gender.Female;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void setupExerciseSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.exercise,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wantsToInput.setAdapter(adapter);

        wantsToInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String textExercise = wantsToInput.getSelectedItem().toString();
                exercise = Utility.getExerciseEnumFromString(textExercise);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void cancel(View v) {
        if (optionsService.getOptions().hasSound())
            SoundManager.playClickSound(this);
        finish();
    }

    /**
     * Logging the user in to Firestore. Various conditions must be met.
     * @param view
     */
    public void login(View view) {

        if (age.getText().toString().isEmpty()) {
            Utility.makeToast(this, "You must type in an age");
            return;
        }
        if (phone.getText().toString().isEmpty()) {
            Utility.makeToast(this, "You must type in your phone number");
            return;
        }
        if (gender == null) {
            Utility.makeToast(this, "You must pick your gender");
            return;
        }
        if (exercise == null) {
            Utility.makeToast(this, "You must choose an exercise");
            return;
        }
        if (city.getText().toString().isEmpty()) {
            Utility.makeToast(this, "You must type in your city");
            return;
        }
        String textName = name.getText().toString();
        String cityText = city.getText().toString();
        int inputAge = Integer.parseInt(age.getText().toString());
        int inputPhone = Integer.parseInt(phone.getText().toString());

        SocializeRequest request = new SocializeRequest(textName, inputAge, gender, inputPhone, exercise, cityText);
        try {
            socializeService.addRequest(request, new ISuccessListener() {
                @Override
                public void onSuccess(String id) {
                    saveNewId(id);
                    showList();
                }
            }, saveInput.isChecked());
        } catch (InvalidRequestException ex) {
            Utility.makeToast(this, ex.getMessage());
        }

        showLoadingSection(); // While we wait for the async call
    }

    private void showLoadingSection() {
        mainSection.setVisibility(View.GONE);
        loadingSection.setVisibility(View.VISIBLE);
    }

    /**
     * Opens the SocializeList activity and adds the id and city as extras
     */
    private void showList() {
        if (optionsService.getOptions().hasSound()) {
            SoundManager.playClickSound(this);
        }
        Intent x = new Intent(this, SocializeList.class);
        x.putExtra("id", id);
        x.putExtra("city", city.getText().toString());
        startActivity(x);
        finish();
    }


    public void saveNewId(String id) {
        this.id = id;
    }
}
