package com.example.celeritem;

import com.example.celeritem.BLL.ExerciseApplicationService;
import com.example.celeritem.Exceptions.InvalidResultException;
import com.example.celeritem.Interfaces.IExerciseDataAccess;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.ExerciseLandmark;
import com.example.celeritem.Model.ExerciseResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Mockito.mock;

public class ExerciseApplicationServiceTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Test
    public void addResultWithNoDistance() throws InvalidResultException {
        exceptionRule.expect(InvalidResultException.class);
        exceptionRule.expectMessage("Distance must be greater than 0");
        IExerciseDataAccess mockRep = mock(IExerciseDataAccess.class);
        ExerciseLandmark mark = new ExerciseLandmark(100,100,new Date());
        ExerciseLandmark mark2 = new ExerciseLandmark(100,101,new Date());
        ArrayList<ExerciseLandmark> list = new ArrayList<>();
        list.add(mark);
        list.add(mark2);
        ExerciseResult result = new ExerciseResult(list, Exercise.Run, 30, 2,20,120,new Date(), 0);
        ExerciseApplicationService service = new ExerciseApplicationService(mockRep);
        service.addResult(result);
    }
    @Test
    public void addResultWithNoTime() throws InvalidResultException {
        exceptionRule.expect(InvalidResultException.class);
        exceptionRule.expectMessage("Time must be greater than 0");
        IExerciseDataAccess mockRep = mock(IExerciseDataAccess.class);

        ExerciseLandmark mark = new ExerciseLandmark(100,100,new Date());
        ExerciseLandmark mark2 = new ExerciseLandmark(100,101,new Date());
        ArrayList<ExerciseLandmark> list = new ArrayList<>();
        list.add(mark);
        list.add(mark2);
        ExerciseResult result = new ExerciseResult(list, Exercise.Run, 30, 2,20,0,new Date(), 100);
        ExerciseApplicationService service = new ExerciseApplicationService(mockRep);
        service.addResult(result);
    }
    @Test
    public void addResultWithTooFewLandmarks() throws InvalidResultException {
        exceptionRule.expect(InvalidResultException.class);
        exceptionRule.expectMessage("A result must have at least two landmarks");
        IExerciseDataAccess mockRep = mock(IExerciseDataAccess.class);
        ExerciseLandmark mark = new ExerciseLandmark(100,100,new Date());
        ArrayList<ExerciseLandmark> list = new ArrayList<>();
        list.add(mark);
        ExerciseResult result = new ExerciseResult(list, Exercise.Run, 30, 2,20,100,new Date(), 100);
        ExerciseApplicationService service = new ExerciseApplicationService(mockRep);
        service.addResult(result);
    }

    @Test
    public void addResultWithNoDate() throws InvalidResultException {
        exceptionRule.expect(InvalidResultException.class);
        exceptionRule.expectMessage("A result must have a proper date");
        IExerciseDataAccess mockRep = mock(IExerciseDataAccess.class);
        ExerciseLandmark mark = new ExerciseLandmark(100,100,new Date());
        ExerciseLandmark mark2 = new ExerciseLandmark(100,101,new Date());
        ArrayList<ExerciseLandmark> list = new ArrayList<>();
        list.add(mark);
        list.add(mark2);
        ExerciseResult result = new ExerciseResult(list, Exercise.Run, 30, 2,20,100,null, 100);
        ExerciseApplicationService service = new ExerciseApplicationService(mockRep);
        service.addResult(result);
    }

}