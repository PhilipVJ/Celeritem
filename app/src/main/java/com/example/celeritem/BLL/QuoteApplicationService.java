package com.example.celeritem.BLL;

import com.example.celeritem.Interfaces.IQuoteApplicationService;
import com.example.celeritem.Interfaces.IQuoteDataAccess;
import com.example.celeritem.Interfaces.IQuoteListener;

public class QuoteApplicationService implements IQuoteApplicationService {
    private final IQuoteDataAccess rep;

    public QuoteApplicationService(IQuoteDataAccess rep) {
        this.rep = rep;
    }

    /**
     * Gets a quote from the repository. The IQuoteListener object named listener functions as a callback.
     * @param listener
     */
    @Override
    public void getQuote(IQuoteListener listener) {
        rep.getQuote(listener);
    }
}
