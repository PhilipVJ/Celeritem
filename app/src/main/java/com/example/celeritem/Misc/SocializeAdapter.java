package com.example.celeritem.Misc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.celeritem.Model.Gender;
import com.example.celeritem.Model.SocializeRequest;
import com.example.celeritem.R;

import java.util.ArrayList;

/**
 * This class is used for setting up a custom ArrayAdapter for the list in the SocializeList activity
 */
public class SocializeAdapter extends ArrayAdapter<SocializeRequest> {

    private ArrayList<SocializeRequest> requests;
    private final int[] colours = {
            Color.parseColor("#d0e1f9"),
            Color.parseColor("#E8FFF8")
    };

    public SocializeAdapter(Context context, int textViewResourceId,
                            ArrayList<SocializeRequest> requests) {
        super(context, textViewResourceId, requests);
        this.requests = requests;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.socializecell, null);
        }
        final SocializeRequest f = requests.get(position);
        v.setBackgroundColor(colours[position % colours.length]);
        TextView name = v.findViewById(R.id.name);
        TextView age = v.findViewById(R.id.age);
        TextView wantsTo = v.findViewById(R.id.wantsTo);
        String nameText = "Name: " + f.getName();
        String ageText = "Age: " + f.getAge();
        String wantsToText = "Wants to: " + f.getWantsTo();

        name.setText(nameText);
        age.setText(ageText);
        wantsTo.setText(wantsToText);
        ImageView user = v.findViewById(R.id.user);
        if (f.getGender() == Gender.Male) {
            user.setImageResource(R.drawable.male);
        } else {
            user.setImageResource(R.drawable.female);
        }
        return v;
    }
}
