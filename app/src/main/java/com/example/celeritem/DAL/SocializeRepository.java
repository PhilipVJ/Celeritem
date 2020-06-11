package com.example.celeritem.DAL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.celeritem.Interfaces.IChangeListener;
import com.example.celeritem.Interfaces.ISocializeDataAccess;
import com.example.celeritem.Interfaces.ISuccessListener;
import com.example.celeritem.MainActivity;
import com.example.celeritem.Model.AppOptions;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.Gender;
import com.example.celeritem.Model.SocializeRequest;
import com.example.celeritem.Utility.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SocializeRepository implements ISocializeDataAccess {
    private final FirebaseFirestore fireStoreDb;
    private final SQLiteDatabase sqLiteDatabase;
    private ListenerRegistration registration;
    private final String COLLECTION_NAME = "socialize";
    private SQLiteStatement requestInsert;

    public SocializeRepository(FirebaseFirestore db, SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.fireStoreDb = db;
        makeStatement();
    }

    /**
     * Makes an SQLite statement for internal use
     */
    public void makeStatement(){
        String INSERT_SAVED_REQUEST = "insert into " + SQLiteDataAccess.TABLE_NAME_SAVED_REQUEST_INPUT
                + "(name, wantsTo, age,gender,phone,city) values (?, ?, ?, ?, ?, ?)";
        requestInsert = sqLiteDatabase.compileStatement(INSERT_SAVED_REQUEST);
    }

    /**
     * Gets the ID from the old request and uses it to call removeRequest
     */
    @Override
    public void deleteOldRequest() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_REQUESTS, null);
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex("requestId"));
            cursor.close();
            removeRequest(id);
        }
    }

    /**
     * Deletes the request from SQLite
     */
    private void deleteFromSQLite() {
        sqLiteDatabase.execSQL("delete from " + SQLiteDataAccess.TABLE_NAME_REQUESTS); // From SQLite
    }

    /**
     * Checks if there are any undeleted requests in the SQLite database.
     * @return a boolean indicating if there is one or not
     */
    @Override
    public boolean hasUndeletedRequest() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_REQUESTS, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * Gets the last saved request from the SQLite database
     * @return a SocializeRequest object with the information. Returns null if there isn't an request
     */
    @Override
    public SocializeRequest getLastSavedRequest() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_SAVED_REQUEST_INPUT, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int age = cursor.getInt(cursor.getColumnIndex("age"));
            Exercise wantsTo = Utility.getExerciseEnumFromString(cursor.getString(cursor.getColumnIndex("wantsTo")));
            String gender = cursor.getString(cursor.getColumnIndex("gender"));
            Gender genderEnum = gender.equals("Male") ? Gender.Male : Gender.Female;
            int phone = cursor.getInt(cursor.getColumnIndex("phone"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            return new SocializeRequest(name, age, genderEnum, phone, wantsTo, city);
        } else {
            return null;
        }
    }

    /**
     * Adds an request to Firebase as well as the local SQLite database. If saveForNextTime is true
     * the basic user information will be stored locally for future use. The listener objects functions as a callback
     * @param request
     * @param listener
     * @param saveForNextTime
     */
    @Override
    public void addRequest(SocializeRequest request, final ISuccessListener listener, boolean saveForNextTime) {
        Map<String, Object> toAdd = new HashMap<>();
        toAdd.put("name", request.getName());
        toAdd.put("gender", request.getGender());
        toAdd.put("age", request.getAge());
        toAdd.put("phone", request.getPhoneNumber());
        toAdd.put("wantsTo", request.getWantsTo());
        toAdd.put("city", request.getCity());


        fireStoreDb.collection(COLLECTION_NAME)
                .add(toAdd)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addToLocalDb(documentReference.getId());
                        listener.onSuccess(documentReference.getId());
                        Log.d(MainActivity.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(MainActivity.TAG, "Error adding document", e);
                    }
                });
        if (saveForNextTime) {
            if (getLastSavedRequest() == null) { // Nothing has been added before
                requestInsert.bindString(1, request.getName());
                requestInsert.bindString(2, ""+request.getWantsTo());
                requestInsert.bindDouble(3, request.getAge());
                requestInsert.bindString(4, ""+request.getGender());
                requestInsert.bindDouble(5, request.getPhoneNumber());
                requestInsert.bindString(6, request.getCity());
                requestInsert.executeInsert();
            }
            else{ // Update the existing
                ContentValues cv = new ContentValues();
                cv.put("name", request.getName());
                cv.put("wantsTo", ""+request.getWantsTo());
                cv.put("age", request.getAge());
                cv.put("gender", ""+request.getGender());
                cv.put("phone", request.getPhoneNumber());
                cv.put("city", request.getCity());
                sqLiteDatabase.update(SQLiteDataAccess.TABLE_NAME_SAVED_REQUEST_INPUT, cv, "id=" + 1, null);
            }
        }
    }

    /**
     * Adds the Firebase ID to the local SQLite database
     * @param id
     */
    private void addToLocalDb(String id) {
        String INSERT_ID = "insert into " + SQLiteDataAccess.TABLE_NAME_REQUESTS
                + "(requestId) values (?)";
        SQLiteStatement insert = sqLiteDatabase.compileStatement(INSERT_ID);
        insert.bindString(1, id);
        insert.executeInsert();
    }

    /**
     * Removes the Firebase listener
     */
    @Override
    public void removeListener() {
        registration.remove();
    }

    /**
     * Adds a Firebase listener.
     * @param userId is the current users id - used to the users own request
     * @param userCity - used to find all requests in the given city
     * @param listener - functions as a callback
     */
    @Override
    public void addListener(final String userId, final String userCity, final IChangeListener listener) {
        registration = fireStoreDb.collection(COLLECTION_NAME).
                addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {
                                Log.d(MainActivity.TAG, "Item(s) changed");
                                ArrayList<SocializeRequest> allRequests = new ArrayList<SocializeRequest>();
                                for (DocumentSnapshot document : snapshot.getDocuments()) {
                                    // Skip all which isn't in the same city and if the document is the user
                                    String id = document.getId();
                                    String city = (String) document.get("city");
                                    if (id.equals(userId))
                                        continue;
                                    if (!city.toLowerCase().equals(userCity.toLowerCase()))
                                        continue;

                                    String name = (String) document.get("name");
                                    int age = Integer.parseInt("" + (document.get("age")));
                                    double phone = Integer.parseInt("" + (document.get("phone")));
                                    String gender = (String) document.get("gender");
                                    Gender genderEnum = gender.equals("Male") ? Gender.Male : Gender.Female;
                                    String wantsTo = (String) document.get("wantsTo");

                                    Exercise wantsToExercise = Utility.getExerciseEnumFromString(wantsTo);
                                    SocializeRequest request = new SocializeRequest(name, age, genderEnum, (int) phone, wantsToExercise, city);
                                    request.addId(id);
                                    allRequests.add(request);
                                }
                                listener.onChange(allRequests);
                            }
                        }
                );

    }

    /**
     * Wrapper method which calls two functions to delete an request by id both locally and on Firestore.
     * @param id
     */
    @Override
    public void removeRequest(String id) {
        deleteFromFirestore(id);
        deleteFromSQLite();
    }

    /**
     * Deletes request from Firestore based on the id argument
     * @param id
     */
    private void deleteFromFirestore(String id) {
        fireStoreDb.collection(COLLECTION_NAME).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(MainActivity.TAG, "Data has been deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(MainActivity.TAG, e.getMessage());
            }
        });
    }
}
