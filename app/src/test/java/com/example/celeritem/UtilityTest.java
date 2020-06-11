package com.example.celeritem;

import com.example.celeritem.Exceptions.InvalidInputException;
import com.example.celeritem.Exceptions.InvalidResultException;
import com.example.celeritem.Model.MetActivity;
import com.example.celeritem.Utility.Utility;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class UtilityTest {
    @Test
    public void formatSeconds() {
        int seconds = 120;
        String expected = "2:00";
        String actual = Utility.formatSeconds(seconds);
        assertEquals(actual, expected);
    }

    @Test
    public void formatSeconds2() {
        int seconds = 2;
        String expected = "0:02";
        String actual = Utility.formatSeconds(seconds);
        assertEquals(actual, expected);
    }

    @Test
    public void formatSeconds3() {
        int seconds = 55;
        String expected = "0:55";
        String actual = Utility.formatSeconds(seconds);
        assertEquals(actual, expected);
    }

    @Test
    public void formatDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2005);
        cal.set(Calendar.MONTH,11);
        cal.set(Calendar.DAY_OF_MONTH,5);
        Date toFormat = cal.getTime();
        String expected = "5/12-2005";
        String actual = Utility.formatDate(toFormat);
        assertEquals(expected,actual);
    }

    @Test
    public void formatDate2(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2009);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DAY_OF_MONTH,10);
        Date toFormat = cal.getTime();
        String expected = "10/1-2009";
        String actual = Utility.formatDate(toFormat);
        assertEquals(expected,actual);
    }

    @Test
    public void testAverageSpeed(){
        double distance = 10000; // 10 km
        Date start = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date end = cal.getTime(); // One hour in the future
        int expected = 10;
        double actual = Utility.getAverageSpeed(distance, start,end);
        int roundedActual = (int) actual;
       assertEquals(expected,roundedActual);
    }

    @Test
    public void testAverageSpeed2(){
        double distance = 20000; // 20 km
        Date start = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 2);
        Date end = cal.getTime(); // One hour in the future
        int expected = 10;
        double actual = Utility.getAverageSpeed(distance, start,end);
        int roundedActual = (int) actual;
        assertEquals(expected,roundedActual);
    }

    @Test
    public void testBMI() throws InvalidInputException {
        double height = 180;
        double weight = 80;

        double expected = 24.69;
        double actual = Utility.getBmi(weight, height);

        assertEquals(expected, actual, 0.3);
    }

    @Test
    public void testKcalBurned() throws InvalidInputException {
        double duration = 60;
        double weight = 100;
        MetActivity met = MetActivity.Running;

        double expected = 1239;
        double actual = Utility.getKcalToExercise(duration, met, weight);

        assertEquals(expected, actual, 5);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Test
    public void testKcalBurnedWrongInput() throws InvalidInputException {
        double duration = 60;
        double weight = 0;
        MetActivity met = MetActivity.Running;

        exceptionRule.expect(InvalidInputException.class);
        Utility.getKcalToExercise(duration, met, weight);
    }

    @Rule
    public ExpectedException exceptionRuleBmi = ExpectedException.none();
    @Test
    public void testBmiWrongInput() throws InvalidInputException {
        double height = 180;
        double weight = 0;

        exceptionRuleBmi.expect(InvalidInputException.class);
        Utility.getBmi(weight, height);
    }

}