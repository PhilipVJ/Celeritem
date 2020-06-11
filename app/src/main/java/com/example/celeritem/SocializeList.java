package com.example.celeritem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Interfaces.IChangeListener;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Interfaces.ISocializeApplicationService;
import com.example.celeritem.Managers.SoundManager;
import com.example.celeritem.Misc.ApplicationServiceFactory;
import com.example.celeritem.Misc.SocializeAdapter;
import com.example.celeritem.Model.SocializeRequest;

import java.util.ArrayList;

public class SocializeList extends AppCompatActivity {
    private ArrayList<SocializeRequest> requests;
    private ListAdapter adapter;
    private ListView listView;
    // Application services
    private ISocializeApplicationService service;
    private IOptionsApplicationService optionsService;
    private String id;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialize_list);
        id = (String) getIntent().getExtras().get("id");
        city = (String) getIntent().getExtras().get("city");
        Log.d(MainActivity.TAG, "Opened activity with ID: " + id);
        listView = findViewById(R.id.socializeList);
        try {
            service = ApplicationServiceFactory.getSocializeApplicationService();
            optionsService = ApplicationServiceFactory.getOptionsApplicationService();
        } catch (InvalidServiceUsageException e) {
            Log.e(MainActivity.TAG, "An error occurred: " + e.getMessage());
        }

        service.addListener(id, city, new IChangeListener() {
            @Override
            public void onChange(ArrayList<SocializeRequest> allRequests) {
                Log.d(MainActivity.TAG, "New updates");
                requests = allRequests;
                setupAdapter();
            }
        });


    }

    private void setupAdapter() {
        adapter = new SocializeAdapter(this, R.layout.socializecell, requests);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onRequestClick(position);
            }
        });
    }


    private void onRequestClick(int position) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + requests.get(position).getPhoneNumber()));
        startActivity(intent);
    }

    /**
     * Removes the firebase listener and removes the request from both SQLite and Firebase
     */
    private void stopSocializing() {
        service.removeListener();
        service.removeRequest(id);
    }


    public void delete(View v) {
        if (optionsService.getOptions().hasSound())
            SoundManager.playClickSound(this);
        stopSocializing();
        finish();
    }


}
