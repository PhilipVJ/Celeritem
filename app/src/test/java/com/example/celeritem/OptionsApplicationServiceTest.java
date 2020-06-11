package com.example.celeritem;

import com.example.celeritem.BLL.OptionsApplicationService;
import com.example.celeritem.Exceptions.InvalidOptionsException;
import com.example.celeritem.Interfaces.IOptionsDataAccess;
import com.example.celeritem.Model.AppOptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

public class OptionsApplicationServiceTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    // Boundary value analysis


    @Test
    public void invalidAccuracyTestValue0() throws InvalidOptionsException {
        exceptionRule.expect(InvalidOptionsException.class);
        exceptionRule.expectMessage("Accuracy must be between 1 and 10");
        AppOptions options = new AppOptions(0, false, false, false);
        IOptionsDataAccess mockRep = mock(IOptionsDataAccess.class);
        OptionsApplicationService service = new OptionsApplicationService(mockRep);
        service.setOptions(options);
    }

    @Test
    public void invalidAccuracyTestValue11() throws InvalidOptionsException {
        exceptionRule.expect(InvalidOptionsException.class);
        exceptionRule.expectMessage("Accuracy must be between 1 and 10");
        AppOptions options = new AppOptions(11, false, false, false);
        IOptionsDataAccess mockRep = mock(IOptionsDataAccess.class);
        OptionsApplicationService service = new OptionsApplicationService(mockRep);
        service.setOptions(options);
    }

    @Test
    public void validAccuracyTestValue1() throws InvalidOptionsException {
        AppOptions options = new AppOptions(1, false, false, false);
        IOptionsDataAccess mockRep = mock(IOptionsDataAccess.class);
        OptionsApplicationService service = new OptionsApplicationService(mockRep);
        service.setOptions(options);
        verify(mockRep,times(1)).setOptions(options);
    }
    @Test
    public void validAccuracyTestValue2() throws InvalidOptionsException {
        AppOptions options = new AppOptions(2, false, false, false);
        IOptionsDataAccess mockRep = mock(IOptionsDataAccess.class);
        OptionsApplicationService service = new OptionsApplicationService(mockRep);
        service.setOptions(options);
        verify(mockRep,times(1)).setOptions(options);
    }
    @Test
    public void validAccuracyTestValue9() throws InvalidOptionsException {
        AppOptions options = new AppOptions(9, false, false, false);
        IOptionsDataAccess mockRep = mock(IOptionsDataAccess.class);
        OptionsApplicationService service = new OptionsApplicationService(mockRep);
        service.setOptions(options);
        verify(mockRep,times(1)).setOptions(options);
    }
    @Test
    public void validAccuracyTestValue10() throws InvalidOptionsException {
        AppOptions options = new AppOptions(10, false, false, false);
        IOptionsDataAccess mockRep = mock(IOptionsDataAccess.class);
        OptionsApplicationService service = new OptionsApplicationService(mockRep);
        service.setOptions(options);
        verify(mockRep,times(1)).setOptions(options);
    }



}