package com.example.celeritem.BLL;

import com.example.celeritem.Exceptions.InvalidOptionsException;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Interfaces.IOptionsDataAccess;
import com.example.celeritem.Model.AppOptions;

public class OptionsApplicationService implements IOptionsApplicationService {
    private final IOptionsDataAccess rep;

    public OptionsApplicationService(IOptionsDataAccess rep) {
        this.rep = rep;
    }

    /**
     * Validates the options objects accuracy property.
     * Throws InvalidOptionsException if conditions aren't met.
     * @param options
     * @throws InvalidOptionsException
     */
    private void validateOptions(AppOptions options) throws InvalidOptionsException {
        if(options.getAccuracy()<=0 || options.getAccuracy()>10){
            throw new InvalidOptionsException("Accuracy must be between 1 and 10");
        }
    }

    @Override
    public AppOptions getOptions() {
        return rep.getOptions();
    }

    /**
     * Updates the options object in the repository.
     * Uses the validateOptions method to validate the object.
     * @param options
     * @throws InvalidOptionsException
     */
    @Override
    public void setOptions(AppOptions options) throws InvalidOptionsException {
        validateOptions(options);
        rep.setOptions(options);
    }
}
