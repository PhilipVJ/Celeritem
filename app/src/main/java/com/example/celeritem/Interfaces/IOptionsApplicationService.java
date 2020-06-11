package com.example.celeritem.Interfaces;

import com.example.celeritem.Exceptions.InvalidOptionsException;
import com.example.celeritem.Model.AppOptions;

public interface IOptionsApplicationService {
    AppOptions getOptions();
    void setOptions(AppOptions options) throws InvalidOptionsException;
}
