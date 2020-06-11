package com.example.celeritem.Interfaces;

import com.example.celeritem.Model.SocializeRequest;

public interface ISocializeDataAccess {

    void addRequest(SocializeRequest request, ISuccessListener listener, boolean saveForNextTime);

    void removeListener();

    void addListener(String id, String city, IChangeListener listener);

    void removeRequest(String id);

    void deleteOldRequest();

    boolean hasUndeletedRequest();

    SocializeRequest getLastSavedRequest();
}
