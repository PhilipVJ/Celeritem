package com.example.celeritem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Interfaces.IExerciseApplicationService;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Misc.ExerciseAdapter;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Model.Order;
import com.example.celeritem.Model.OrderBy;
import com.example.celeritem.Model.OrderDirection;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<ExerciseResult> results;
    private ListView historyListView;
    private Order currentOrder;
    private Context context;
    private final int RESULT_REQUEST_CODE = 3;
    public static final int DELETED = 4;
    public static final String RE_WATCH = "Rewatch"; // Constant used as key in an intents extras
    private IOptionsApplicationService optionsService;
    private IExerciseApplicationService exerciseService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        context = this;
        historyListView = findViewById(R.id.historyListView);
        // Setup the default ordering
        currentOrder = new Order(OrderBy.DATE, OrderDirection.DESCENDING);
        // Get services
        try {
            optionsService = ApplicationServiceFactory.getOptionsApplicationService();
            exerciseService = ApplicationServiceFactory.getExerciseApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "Error occurred: " + e.getMessage());
        }
        results = exerciseService.getAllResults(currentOrder);
        // Setting the custom list adapter
        if (results != null) {
            ListAdapter adapter = new ExerciseAdapter(this, R.layout.cell, results);
            historyListView.setAdapter(adapter);
            historyListView.setDivider(null);
            historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onResultClick(position);
                }
            });
        }
    }

    private void onResultClick(int position) {
        if (optionsService.getOptions().hasSound()) {
            SoundManager.playClickSound(context);
        }
        ExerciseResult result = results.get(position);
        Intent x = new Intent(context, ResultActivity.class);
        x.putExtra(ExerciseActivity.RESULT, result);
        x.putExtra(RE_WATCH, true);
        startActivityForResult(x, RESULT_REQUEST_CODE);
    }

    /**
     *  If a result has been deleted after a re-watch the list will get updated
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_REQUEST_CODE)
            switch (resultCode) {
                case DELETED:
                    updateList();
                    break;
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.orderByDate) {
            Log.d(MainActivity.TAG, "Ordering by date");
            // If it is already ordered by date - the direction will change
            if (currentOrder.getOrderBy() == OrderBy.DATE) {
                currentOrder.switchOrderDirection();
            } else {
                currentOrder = new Order(OrderBy.DATE, OrderDirection.ASCENDING);
            }
        }
        if (id == R.id.orderByDistance) {
            Log.d(MainActivity.TAG, "Ordering by distance");
            // If it is already ordered by distance - the direction will change
            if (currentOrder.getOrderBy() == OrderBy.DISTANCE) {
                currentOrder.switchOrderDirection();
            } else {
                currentOrder = new Order(OrderBy.DISTANCE, OrderDirection.ASCENDING);
            }
        }
        updateList();
        return super.onOptionsItemSelected(item);
    }

    /**
     * This methods updates the list based on the order object
     */
    private void updateList() {
        results = exerciseService.getAllResults(currentOrder);
        ListAdapter adapter = new ExerciseAdapter(this, R.layout.cell, results);
        historyListView.setAdapter(adapter);
        historyListView.setDivider(null);
    }

}
