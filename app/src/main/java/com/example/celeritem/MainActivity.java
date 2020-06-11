package com.example.celeritem;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Interfaces.IQuoteApplicationService;
import com.example.celeritem.Interfaces.IQuoteListener;
import com.example.celeritem.Interfaces.ISocializeApplicationService;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.Quote;
import com.example.celeritem.Utility.Utility;

public class MainActivity extends AppCompatActivity {

    public static final int OPTIONS_REQUEST = 3;
    public static final String TAG = "DebugTAG"; // Tag used in the entire application for debugging.
    private static MenuItem deleteOld;
    private IOptionsApplicationService optionsService;
    private ISocializeApplicationService socializeService;
    private IQuoteApplicationService quoteService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) // Setup factory when opening app
        {
            ApplicationServiceFactory.setupFactory(this);
        }

        try {
            optionsService = ApplicationServiceFactory.getOptionsApplicationService();
            socializeService = ApplicationServiceFactory.getSocializeApplicationService();
            quoteService = ApplicationServiceFactory.getQuoteApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "Error occurred: " + e.getMessage());
        }
        setContentView(R.layout.activity_main);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && optionsService.getOptions().showQuotes()) {
            quoteService.getQuote(new IQuoteListener() {
                @Override
                public void onSuccess(Quote quote) {
                    setQuote(quote);
                }
            });
        }
    }

    private void setQuote(Quote quote) {
        String formattedAuthor = "- " + quote.getAuthor();
        TextView quoteView = findViewById(R.id.quote);
        TextView authorInput = findViewById(R.id.authorInput);
        quoteView.setText(quote.getQuote());
        authorInput.setText(formattedAuthor);
        findViewById(R.id.quoteArea).setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        deleteOld = menu.findItem(R.id.deleteOldRequest);
        setDeleteOldItem();
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * If there is an old request in storage we make sure there is an option in the menu to delete it
     */
    private void setDeleteOldItem() {
        boolean hasUnDeletedRequest = socializeService.hasUndeletedRequest();
        if (hasUnDeletedRequest)
            deleteOld.setVisible(true);
        else
            deleteOld.setVisible(false);
    }

    /**
     * This method handles item selections in the menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (optionsService.getOptions().hasSound()) {
            SoundManager.playClickSound(this);
        }

        if (id == R.id.options) {
            Intent x = new Intent(this, OptionsActivity.class);
            startActivityForResult(x, OPTIONS_REQUEST);
        }

        if (id == R.id.history) {
            Intent x = new Intent(this, HistoryActivity.class);
            startActivity(x);
        }
        if (id == R.id.socialize) {
            Intent x = new Intent(this, SocializeLogin.class);
            startActivity(x);
        }
        if (id == R.id.calculator) {
            Intent cal = new Intent(this, CalculatorActivity.class);
            startActivity(cal);
        }

        if (id == R.id.deleteOldRequest) {
            socializeService.deleteOldRequest();
            Utility.makeToast(this, "Deleted old request");
            deleteOld.setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deleteOld != null) {
            setDeleteOldItem();
        }
    }

    public void startRun(View v) {
        startActivity(Exercise.Run);
    }

    public void startBike(View v) {
        startActivity(Exercise.Bike);
    }

    public void startWalk(View v) {
        startActivity(Exercise.Walk);
    }

    private void startActivity(Exercise exercise) {
        if (optionsService.getOptions().hasSound()) {
            SoundManager.playClickSound(this);
        }
        Intent x = new Intent(this, ExerciseActivity.class);
        x.putExtra(ExerciseActivity.CHOSEN_EXERCISE, "" + exercise);
        startActivity(x);
    }

    /**
     * This method is called when an intent returning an result is closed
     * @param requestCode - the request code which the intent was opened with
     * @param resultCode - the result code returned
     * @param data - the intent containing data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPTIONS_REQUEST)
            switch (resultCode) {
                case RESULT_CANCELED:
                    Utility.makeToast(this, getString(R.string.noChanges));
                    break;
                case RESULT_OK:
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        showOrHideQuoteArea();
                    Utility.makeToast(this, getString(R.string.settingsSaved));

                    break;
            }
    }

    /**
     *  When settings has changed we need to make sure that the quote-area updates to the settings chosen
     */
    private void showOrHideQuoteArea() {
        LinearLayout quoteArea = findViewById(R.id.quoteArea);
        if (optionsService.getOptions().showQuotes()) {
            if (quoteArea.getVisibility() != View.VISIBLE) {
                quoteService.getQuote(new IQuoteListener() {
                    @Override
                    public void onSuccess(Quote quote) {
                        setQuote(quote);
                    }
                });
            }
        } else
            quoteArea.setVisibility(View.GONE);
    }
}
