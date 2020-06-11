package com.example.celeritem.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.celeritem.MainActivity;

public class SQLiteDataAccess {

    private static final String DATABASE_NAME = "celeritem.database";
    private static final int DATABASE_VERSION = 11;
    public static final String TABLE_NAME_OPTIONS = "Options";
    public static final String TABLE_NAME_EXERCISE = "Exercise";
    public static final String TABLE_NAME_LANDMARKS = "Landmarks";
    public static final String TABLE_NAME_REQUESTS = "Requests";
    public static final String TABLE_NAME_SAVED_REQUEST_INPUT = "SavedRequestInput";

    private static SQLiteDatabase mDatabase;

    /**
     * Gets the SQLiteDatabase object singleton. If it isn't instantiated the context object
     * will be used to create it.
     * @param context
     * @return the SQLiteDatabase object singleton
     */
    public static SQLiteDatabase getDatabase(Context context) {
        if (mDatabase == null) {
            OpenHelper openHelper = new OpenHelper(context);
            mDatabase = openHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    /**
     * Class which both handles the creation and updating of the local SQLite database
     */
    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(MainActivity.TAG, "Creating new DB");
            // Create options table
            db.execSQL("CREATE TABLE " + TABLE_NAME_OPTIONS
                    + "(id INTEGER PRIMARY KEY, accuracy INTEGER, sound INTEGER, kilometerNotification INTEGER, quotes INTEGER)");
            // Create activity table
            db.execSQL("CREATE TABLE " + TABLE_NAME_EXERCISE
                    + "(exerciseId INTEGER PRIMARY KEY, exercise TEXT, avgSpeed DOUBLE, totalNumberOfBreaks INTEGER, breaksInSeconds INTEGER," +
                    "runInSeconds INTEGER, date DATE, distance DOUBLE)");
            // Create landmark table
            db.execSQL("CREATE TABLE " + TABLE_NAME_LANDMARKS
                    + "(exerciseId INTEGER, position INTEGER, latitude DOUBLE, longitude DOUBLE, timeStamp DATE, PRIMARY KEY (exerciseId, position))");

            // Create request table
            db.execSQL("CREATE TABLE " + TABLE_NAME_REQUESTS
                    + "(requestId TEXT, PRIMARY KEY (requestId))");

            // Create saved request input table
            db.execSQL("CREATE TABLE " + TABLE_NAME_SAVED_REQUEST_INPUT
                    + "(id INTEGER PRIMARY KEY, name TEXT, wantsTo TEXT, age INTEGER, gender TEXT, phone INTEGER," +
                    "city TEXT)");

            // Create default settings
            db.execSQL("insert into " + TABLE_NAME_OPTIONS
                    + "(accuracy, sound, kilometerNotification, quotes) values (5, 1, 1, 1)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(MainActivity.TAG, "Upgrading database");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OPTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EXERCISE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LANDMARKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REQUESTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SAVED_REQUEST_INPUT);
            onCreate(db);
        }
    }
}