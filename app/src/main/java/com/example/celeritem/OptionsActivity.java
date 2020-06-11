package com.example.celeritem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Exceptions.InvalidOptionsException;
import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Model.AppOptions;
import com.example.celeritem.Utility.Utility;

public class OptionsActivity extends AppCompatActivity {

    private CheckBox sound, kmNotifications, showQuotes;
    private TextView gpsWarning;
    private SeekBar accuracy;
    private Context context;
    public final static String INFORMATION = "Information"; // Used as a key in an intents extras
    private IOptionsApplicationService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        context = this;
        try {
            service = ApplicationServiceFactory.getOptionsApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "An error occurred: " + e.getMessage());
        }

        // Get UI components
        Button cancelBtn = findViewById(R.id.cancelBtn);
        Button okBtn = findViewById(R.id.okBtn);
        sound = findViewById(R.id.soundCheckBox);
        showQuotes = findViewById(R.id.showQuotesCheckBox);
        kmNotifications = findViewById(R.id.kmNotificationCheckBox);
        accuracy = findViewById(R.id.accuracySlider);
        accuracy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setAccuracyWarningText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        gpsWarning = findViewById(R.id.gpsWarning);

        // Set SeekBar values
        accuracy.setMax(10);
        accuracy.setMin(1);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelClick();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okClick();
            }
        });

        setInitialSettings();
    }

    private void okClick() {
        if (sound.isChecked())
            SoundManager.playClickSound(context);
        saveSettings();
    }

    private void cancelClick() {
        if (sound.isChecked())
            SoundManager.playClickSound(context);
        Intent data = new Intent();
        data.putExtra(INFORMATION, "No changes made");
        setResult(RESULT_CANCELED, data);
        finish();
    }

    /**
     * Getting and setting the initial settings saved in the SQLite database
     */
    private void setInitialSettings() {
        AppOptions options = service.getOptions();
        accuracy.setProgress(options.getAccuracy());
        setAccuracyWarningText(options.getAccuracy());

        kmNotifications.setChecked(options.hasKilometerNotification());
        sound.setChecked(options.hasSound());
        showQuotes.setChecked(options.showQuotes());

    }

    /**
     * If the accuracy is set to more than 6 a warning will be shown.
     * @param accuracy
     */
    private void setAccuracyWarningText(int accuracy) {
        if (accuracy > 6)
            gpsWarning.setVisibility(View.VISIBLE);
        else
            gpsWarning.setVisibility(View.INVISIBLE);
    }

    private void saveSettings() {
        try {
            // Fetch values from views
            int accuracySetting = accuracy.getProgress();
            boolean kmNotificationSetting = kmNotifications.isChecked();
            boolean soundSetting = sound.isChecked();
            boolean quotes = showQuotes.isChecked();

            AppOptions options = new AppOptions(accuracySetting, soundSetting, kmNotificationSetting, quotes);
            service.setOptions(options);
            // Go back
            Intent data = new Intent();
            data.putExtra(INFORMATION, "Settings saved");
            setResult(RESULT_OK, data);
            finish();
        } catch (InvalidOptionsException exception) {
            Log.e(MainActivity.TAG, "Error: " + exception.getMessage());
            Utility.makeToast(this, "An error occurred with the following message: " + exception.getMessage());
            finish();
        }
    }
}
