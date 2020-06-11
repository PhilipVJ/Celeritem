package com.example.celeritem.Interfaces;

import com.example.celeritem.Exceptions.InvalidResultException;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Model.Order;

import java.util.ArrayList;

public interface IExerciseApplicationService {
    ArrayList<ExerciseResult> getAllResults(Order order);
    void addResult(ExerciseResult result) throws InvalidResultException;
    void deleteResult(ExerciseResult result);
}
