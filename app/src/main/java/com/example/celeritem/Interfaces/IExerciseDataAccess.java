package com.example.celeritem.Interfaces;

import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Model.Order;

import java.util.ArrayList;

public interface IExerciseDataAccess {
    ArrayList<ExerciseResult> getAllResults(Order order);
    void addResult(ExerciseResult result);
    void deleteResult(ExerciseResult result);
}
