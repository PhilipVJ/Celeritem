package com.example.celeritem.Misc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.R;
import com.example.celeritem.Utility.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class is used for setting up a custom ArrayAdapter for the list in the HistoryActivity
 */
public class ExerciseAdapter extends ArrayAdapter<ExerciseResult> {

    private ArrayList<ExerciseResult> results;
    private final int[] colours = {
            Color.parseColor("#d0e1f9"),
            Color.parseColor("#E8FFF8")
    };

    public ExerciseAdapter(Context context, int textViewResourceId,
                           ArrayList<ExerciseResult> results) {
        super(context, textViewResourceId, results);
        this.results = results;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if (v == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.cell, null);
        }

        v.setBackgroundColor(colours[position % colours.length]);
        ExerciseResult f = results.get(position);
        ImageView type = v.findViewById(R.id.listImage);
        switch (f.getExercise()) {
            case Bike:
                type.setImageResource(R.drawable.bike);
                break;
            case Run:
                type.setImageResource(R.drawable.run);
                break;
            case Walk:
                type.setImageResource(R.drawable.walk);
                break;
        }
        TextView date = v.findViewById(R.id.dateSection);
        TextView distance = v.findViewById(R.id.distanceSection);
        TextView time = v.findViewById(R.id.timeSection);
        TextView avgSpeed = v.findViewById(R.id.avgSpeedSection);

        String dateText = "Date: " + Utility.formatDate(f.getDate());
        date.setText(dateText);
        String speedText = "Avg. speed: " + new DecimalFormat("##.##").format(f.getAvgSpeed()) + " km/t";
        avgSpeed.setText(speedText);
        String distanceText = new DecimalFormat("##.##").format(f.getDistance() / 1000) + " km";
        distance.setText(distanceText);
        String timeText = "Time: " + Utility.formatSeconds(f.getRunInSeconds());
        time.setText(timeText);
        return v;
    }
}
