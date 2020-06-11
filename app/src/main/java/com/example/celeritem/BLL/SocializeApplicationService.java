package com.example.celeritem.BLL;

import com.example.celeritem.Exceptions.InvalidRequestException;
import com.example.celeritem.Interfaces.IChangeListener;
import com.example.celeritem.Interfaces.ISocializeApplicationService;
import com.example.celeritem.Interfaces.ISocializeDataAccess;
import com.example.celeritem.Interfaces.ISuccessListener;
import com.example.celeritem.Model.SocializeRequest;

public class SocializeApplicationService implements ISocializeApplicationService {
    private final ISocializeDataAccess dataAccess;

    public SocializeApplicationService(ISocializeDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Adds a SocializeRequest to the repository. The listener object functions as a callback and
     * saveForNextTime indicates if the requests information needs to be stored locally
     * Throws InvalidRequestException is several conditions aren't met.
     * @param request
     * @param listener
     * @param saveForNextTime
     * @throws InvalidRequestException
     */
    @Override
    public void addRequest(SocializeRequest request, ISuccessListener listener, boolean saveForNextTime) throws InvalidRequestException {
        if (request.getName().length() <= 1)
            throw new InvalidRequestException("Name is too short");
        if (request.getAge() < 0 || request.getAge() > 110)
            throw new InvalidRequestException("Not a valid age");
        String phone = "" + request.getPhoneNumber();
        if (phone.length() != 8)
            throw new InvalidRequestException("Not a valid phone number");
        if(hasUndeletedRequest()) // Delete old request if there is one
            deleteOldRequest();
        dataAccess.addRequest(request, listener, saveForNextTime);
    }

    /**
     * Calls the repository and deletes the listener made with the function below
     */
    @Override
    public void removeListener() {
        dataAccess.removeListener();
    }

    /**
     * Calls the addListener method in the repository and sends the id of the current user, the chosen city
     * and a IChangeListener objects, which functions as a callback
     * @param id
     * @param city
     * @param listener
     */
    @Override
    public void addListener(String id, String city, IChangeListener listener) {
        dataAccess.addListener(id, city, listener);
    }

    /**
     * Removes an specific request from the repository with the given id in form of a String
     * @param id
     */
    @Override
    public void removeRequest(String id) {
        dataAccess.removeRequest(id);
    }

    /**
     * Removes the old request from the repository
     */
    @Override
    public void deleteOldRequest() {
        dataAccess.deleteOldRequest();
    }

    /**
     * Checks if there is an undeleted request in the repository.
     * @return a boolean indicating if there is one or not.
     */
    @Override
    public boolean hasUndeletedRequest() {
        return dataAccess.hasUndeletedRequest();
    }

    /**
     * Fetches the last saved socialize request from the repository.
     * @return a SocializeRequest object, which is the last one saved
     */
    @Override
    public SocializeRequest getLastSavedRequest() {
        return dataAccess.getLastSavedRequest();
    }
}
