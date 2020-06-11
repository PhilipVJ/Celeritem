package com.example.celeritem.BLL;

import com.example.celeritem.Exceptions.InvalidResultException;
import com.example.celeritem.Interfaces.IExerciseApplicationService;
import com.example.celeritem.Interfaces.IExerciseDataAccess;
import com.example.celeritem.Model.ExerciseResult;
import com.example.celeritem.Model.Order;

import java.util.ArrayList;

public class ExerciseApplicationService implements IExerciseApplicationService {

    private final IExerciseDataAccess rep;

    public ExerciseApplicationService(IExerciseDataAccess rep) {
        this.rep = rep;
    }

    /**
     * Fetches all results from the data repository based on the given order object
     * @param order
     * @return an arraylist containing ExerciseResult objects
     */
    @Override
    public ArrayList<ExerciseResult> getAllResults(Order order) {
        return rep.getAllResults(order);
    }

    /**
     * Adds a result to the repository.
     * Throws InvalidResultException if several conditions aren't met.
     * @param result
     * @throws InvalidResultException
     */
    @Override
    public void addResult(ExerciseResult result) throws InvalidResultException {
        if(result.getDistance()<=0){
            throw new InvalidResultException("Distance must be greater than 0");
        }
        if(result.getRunInSeconds()<=0){
            throw new InvalidResultException("Time must be greater than 0");
        }
        if(result.getLandmarks().size()<2){
            throw new InvalidResultException("A result must have at least two landmarks");
        }
        if(result.getDate()==null){
            throw new InvalidResultException("A result must have a proper date");
        }
        rep.addResult(result);
    }

    /**
     * Calls the repository and asks for a deletion of the result object
     * @param result
     */
    @Override
    public void deleteResult(ExerciseResult result) {
        rep.deleteResult(result);
    }
}
