package com.example.celeritem;

import com.example.celeritem.BLL.SocializeApplicationService;
import com.example.celeritem.Exceptions.InvalidRequestException;
import com.example.celeritem.Interfaces.ISocializeDataAccess;
import com.example.celeritem.Interfaces.ISuccessListener;
import com.example.celeritem.Model.Exercise;
import com.example.celeritem.Model.Gender;
import com.example.celeritem.Model.SocializeRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.mock;

public class SocializeApplicationServiceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void nameTooShortTest() throws InvalidRequestException {
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("Name is too short");
        SocializeRequest request = new SocializeRequest("K",21,
                Gender.Female,22332211, Exercise.Bike, "New York");
        ISuccessListener listener = new ISuccessListener() {
            @Override
            public void onSuccess(String id) {
                // Do nothing
            }
        };

        ISocializeDataAccess mockRep = mock(ISocializeDataAccess.class);
        SocializeApplicationService service = new SocializeApplicationService(mockRep);
        service.addRequest(request,listener, false);
    }

    @Test
    public void invalidAgeTest() throws InvalidRequestException {
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("Not a valid age");
        SocializeRequest request = new SocializeRequest("Kaj",-2,
                Gender.Female,22332211, Exercise.Bike, "New York");
        ISuccessListener listener = new ISuccessListener() {
            @Override
            public void onSuccess(String id) {
                // Do nothing
            }
        };

        ISocializeDataAccess mockRep = mock(ISocializeDataAccess.class);
        SocializeApplicationService service = new SocializeApplicationService(mockRep);
        service.addRequest(request,listener, false);
    }

    @Test
    public void invalidAgeTest2() throws InvalidRequestException {
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("Not a valid age");
        SocializeRequest request = new SocializeRequest("Kaj",111,
                Gender.Female,22332211, Exercise.Bike, "New York");
        ISuccessListener listener = new ISuccessListener() {
            @Override
            public void onSuccess(String id) {
                // Do nothing
            }
        };

        ISocializeDataAccess mockRep = mock(ISocializeDataAccess.class);
        SocializeApplicationService service = new SocializeApplicationService(mockRep);
        service.addRequest(request,listener, false);
    }

    @Test
    public void invalidPhoneNumber() throws InvalidRequestException {
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("Not a valid phone number");
        SocializeRequest request = new SocializeRequest("Kaj",19,
                Gender.Female,12, Exercise.Bike, "New York");
        ISuccessListener listener = new ISuccessListener() {
            @Override
            public void onSuccess(String id) {
                // Do nothing
            }
        };

        ISocializeDataAccess mockRep = mock(ISocializeDataAccess.class);
        SocializeApplicationService service = new SocializeApplicationService(mockRep);
        service.addRequest(request,listener, false);
    }

    @Test
    public void invalidPhoneNumber2() throws InvalidRequestException {
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("Not a valid phone number");
        SocializeRequest request = new SocializeRequest("Kaj",19,
                Gender.Female,1265454565, Exercise.Bike, "New York");
        ISuccessListener listener = new ISuccessListener() {
            @Override
            public void onSuccess(String id) {
                // Do nothing
            }
        };

        ISocializeDataAccess mockRep = mock(ISocializeDataAccess.class);
        SocializeApplicationService service = new SocializeApplicationService(mockRep);
        service.addRequest(request,listener, false);
    }

    @Test
    public void validInput() throws InvalidRequestException {
        SocializeRequest request = new SocializeRequest("Kaj",19,
                Gender.Female,12545655, Exercise.Bike, "New York");
        ISuccessListener listener = new ISuccessListener() {
            @Override
            public void onSuccess(String id) {
                // Do nothing
            }
        };

        ISocializeDataAccess mockRep = mock(ISocializeDataAccess.class);
        SocializeApplicationService service = new SocializeApplicationService(mockRep);
        service.addRequest(request,listener, false);
        verify(mockRep,times(1)).addRequest(request,listener, false);
    }


}