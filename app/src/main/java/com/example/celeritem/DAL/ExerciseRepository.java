package com.example.celeritem.DAL;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.celeritem.Interfaces.IExerciseDataAccess;
import com.example.celeritem.MainActivity;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.ExerciseLandmark;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Model.Order;
import com.example.celeritem.Model.OrderBy;
import com.example.celeritem.Model.OrderDirection;
import com.example.celeritem.Utility.Utility;

import java.util.ArrayList;
import java.util.Date;

public class ExerciseRepository implements IExerciseDataAccess {

    private final SQLiteDatabase database;
    private SQLiteStatement exerciseInsert;
    private SQLiteStatement landmarkInsert;
    private SQLiteStatement deleteExercise;
    private SQLiteStatement deleteLandmark;

    public ExerciseRepository(SQLiteDatabase database) {
        this.database = database;
        makeStatements();
    }

    /**
     * Initializes SQLiteStatements for internal use of the class
     */
    private void makeStatements() {
        // Make statements
        String INSERT = "insert into " + SQLiteDataAccess.TABLE_NAME_EXERCISE
                + "(exercise, avgSpeed, totalNumberOfBreaks, breaksInSeconds," +
                " runInSeconds, date, distance) values (?, ?, ?, ?, ?, ?,?)";
        exerciseInsert = database.compileStatement(INSERT);
        String INSERTLANDMARK = "insert into " + SQLiteDataAccess.TABLE_NAME_LANDMARKS
                + "(exerciseId, position, latitude, longitude, timeStamp) values (?, ?, ?, ?, ?)";
        landmarkInsert = database.compileStatement(INSERTLANDMARK);

        String DELETE_EXERCISE = "delete from " + SQLiteDataAccess.TABLE_NAME_EXERCISE + " where exerciseId=?";
        deleteExercise = database.compileStatement(DELETE_EXERCISE);

        String DELETE_LANDMARK = "delete from " + SQLiteDataAccess.TABLE_NAME_LANDMARKS + " where exerciseId=?";
        deleteLandmark = database.compileStatement(DELETE_LANDMARK);
    }

    /**
     * Gets all the results, based on the order object, from the SQLite database
     * @param order
     * @return an ArrayList containing all the ExerciseResult objects.
     */
    @Override
    public ArrayList<ExerciseResult> getAllResults(Order order) {
        Cursor cursor = null;
        if (order == null) {
            cursor = database.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_EXERCISE, null);
        } else if (order.getOrderBy() == OrderBy.DISTANCE) {
            if (order.getOrderDirection() == OrderDirection.ASCENDING) {
                cursor = database.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_EXERCISE + " order by distance ASC", null);
            } else if (order.getOrderDirection() == OrderDirection.DESCENDING) {
                cursor = database.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_EXERCISE + " order by distance DESC", null);
            }
        } else if (order.getOrderBy() == OrderBy.DATE) {
            if (order.getOrderDirection() == OrderDirection.ASCENDING) {
                cursor = database.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_EXERCISE + " order by date ASC", null);
            } else if (order.getOrderDirection() == OrderDirection.DESCENDING) {
                cursor = database.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_EXERCISE + " order by date DESC", null);
            }
        }
        ArrayList<ExerciseResult> results = new ArrayList<ExerciseResult>();
        if (cursor.moveToFirst()) {
            // Fetch all data
            while (!cursor.isAfterLast()) {
                int exerciseId = cursor.getInt(cursor.getColumnIndex("exerciseId"));
                String exercise = cursor.getString(cursor.getColumnIndex("exercise"));
                double avgSpeed = cursor.getDouble(cursor.getColumnIndex("avgSpeed"));
                double totalNumberOfBreaks = cursor.getDouble(cursor.getColumnIndex("totalNumberOfBreaks"));
                double breaksInSeconds = cursor.getDouble(cursor.getColumnIndex("breaksInSeconds"));
                double runInSeconds = cursor.getDouble(cursor.getColumnIndex("runInSeconds"));
                long date = cursor.getLong(cursor.getColumnIndex("date"));
                double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                // Fetch nested Landmarks
                Cursor landmarkCursor = database.rawQuery("select * from " + SQLiteDataAccess.TABLE_NAME_LANDMARKS + " where exerciseId=" + exerciseId + " order by position ASC", null);
                ArrayList<ExerciseLandmark> landmarks = new ArrayList<>();
                if (landmarkCursor.moveToFirst()) {
                    while (!landmarkCursor.isAfterLast()) {
                        double latitude = landmarkCursor.getDouble(landmarkCursor.getColumnIndex("latitude"));
                        double longitude = landmarkCursor.getDouble(landmarkCursor.getColumnIndex("longitude"));
                        long timeStamp = landmarkCursor.getLong(landmarkCursor.getColumnIndex("timeStamp"));
                        ExerciseLandmark landmark = new ExerciseLandmark(latitude, longitude, new Date(timeStamp));
                        landmarks.add(landmark);
                        landmarkCursor.moveToNext();
                    }
                }
                Exercise currentExercise = Utility.getExerciseEnumFromString(exercise);
                ExerciseResult result = new ExerciseResult(landmarks, currentExercise, avgSpeed, (int) totalNumberOfBreaks, (int) breaksInSeconds, (int) runInSeconds, new Date(date), distance);
                result.setId(exerciseId);
                results.add(result);
                cursor.moveToNext();
            }
        }
        return results;
    }

    /**
     * Uses SQLiteStatement objects to add the result object to the SQLite database
     * @param result
     */
    @Override
    public void addResult(ExerciseResult result) {
        try {
            database.beginTransaction();
            // Insert into exercise table
            exerciseInsert.bindString(1, "" + result.getExercise());
            exerciseInsert.bindDouble(2, result.getAvgSpeed());
            exerciseInsert.bindDouble(3, result.getTotalNumberOfBreaks());
            exerciseInsert.bindDouble(4, result.getBreakInSeconds());
            exerciseInsert.bindDouble(5, result.getRunInSeconds());
            exerciseInsert.bindLong(6, result.getDate().getTime());
            exerciseInsert.bindDouble(7, result.getDistance());
            exerciseInsert.executeInsert();
            Cursor cursor = database.rawQuery("SELECT exerciseId from " + SQLiteDataAccess.TABLE_NAME_EXERCISE + " order by exerciseId DESC limit 1", null);
            int exerciseId = -1;
            if (cursor.moveToFirst()) {
                exerciseId = cursor.getInt(cursor.getColumnIndex("exerciseId"));
            }
            // Insert into landmarks table
            int counter = 0;
            for (ExerciseLandmark landmark : result.getLandmarks()) {
                counter++;
                landmarkInsert.bindDouble(1, exerciseId);
                landmarkInsert.bindDouble(2, counter);
                landmarkInsert.bindDouble(3, landmark.getLatitude());
                landmarkInsert.bindDouble(4, landmark.getLongitude());
                landmarkInsert.bindLong(5, landmark.getTimeStamp().getTime());
                landmarkInsert.executeInsert();
            }
            database.setTransactionSuccessful(); // This commits the transaction if there were no exceptions
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        } finally {
            database.endTransaction();
        }

    }

    /**
     * Deletes the result object from the database.
     * @param result
     */
    @Override
    public void deleteResult(ExerciseResult result) {
        try {
            database.beginTransaction();
            deleteLandmark.bindDouble(1, result.getId());
            deleteExercise.bindDouble(1, result.getId());
            deleteLandmark.executeUpdateDelete();
            deleteExercise.executeUpdateDelete();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        } finally {
            database.endTransaction();
        }
    }
}
