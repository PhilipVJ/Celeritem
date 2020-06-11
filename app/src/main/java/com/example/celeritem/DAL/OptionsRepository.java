package com.example.celeritem.DAL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.celeritem.Interfaces.IOptionsDataAccess;
import com.example.celeritem.Model.AppOptions;

public class OptionsRepository implements IOptionsDataAccess {
    private final SQLiteDatabase mDatabase;

    public OptionsRepository(SQLiteDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets the AppOptions object from the SQLite database.
     * @return the AppOptions object with the current user options
     */
    @Override
    public AppOptions getOptions() {
        Cursor cursor = mDatabase.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_OPTIONS, null);
        cursor.moveToFirst();
        int accuracy = cursor.getInt(cursor.getColumnIndex("accuracy"));
        int sound = cursor.getInt(cursor.getColumnIndex("sound"));
        int kilometerNotification = cursor.getInt(cursor.getColumnIndex("kilometerNotification"));
        int quotes = cursor.getInt(cursor.getColumnIndex("quotes"));
        return new AppOptions(accuracy, sound == 1, kilometerNotification == 1, quotes == 1);
    }

    /**
     * Updates the AppOptions object in the SQLite database
     * @param options
     */
    @Override
    public void setOptions(AppOptions options) {
        ContentValues cv = new ContentValues();
        cv.put("accuracy", options.getAccuracy());
        cv.put("sound", options.hasSound());
        cv.put("quotes",options.showQuotes());
        cv.put("kilometerNotification", options.hasKilometerNotification());
        mDatabase.update(SQLiteDataAccess.TABLE_NAME_OPTIONS, cv, "id=" + 1, null);
    }
}
