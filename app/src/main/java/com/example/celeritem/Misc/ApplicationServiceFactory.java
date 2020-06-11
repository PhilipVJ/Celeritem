package com.example.celeritem.Misc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.celeritem.BLL.ExerciseApplicationService;
import com.example.celeritem.BLL.OptionsApplicationService;
import com.example.celeritem.BLL.QuoteApplicationService;
import com.example.celeritem.BLL.SocializeApplicationService;
import com.example.celeritem.DAL.ExerciseRepository;
import com.example.celeritem.DAL.OptionsRepository;
import com.example.celeritem.DAL.QuoteRepository;
import com.example.celeritem.DAL.SQLiteDataAccess;
import com.example.celeritem.DAL.SocializeRepository;
import com.example.celeritem.Exceptions.InvalidServiceUsageException;
import com.example.celeritem.Interfaces.IExerciseApplicationService;
import com.example.celeritem.Interfaces.IOptionsApplicationService;
import com.example.celeritem.Interfaces.IQuoteApplicationService;
import com.example.celeritem.Interfaces.ISocializeApplicationService;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Factory for getting and creating singleton instances of the various ApplicationService classes.
 */
public class ApplicationServiceFactory {
    private static IOptionsApplicationService optionsService;
    private static IExerciseApplicationService exerciseService;
    private static ISocializeApplicationService socializeService;
    private static IQuoteApplicationService quoteService;
    private static boolean hasBeenSetup = false;

    private ApplicationServiceFactory(){}

    /**
     * Sets up the factory for future use. The context object is used for creating the SQLiteDatabase object.
     * @param context
     */
    public static void setupFactory(Context context) {
        if (!hasBeenSetup) {
            SQLiteDatabase database = SQLiteDataAccess.getDatabase(context);
            ExerciseRepository exerciseRepository = new ExerciseRepository(database);
            OptionsRepository optionsRepository = new OptionsRepository(database);
            SocializeRepository socializeRepository = new SocializeRepository(FirebaseFirestore.getInstance(), SQLiteDataAccess.getDatabase(context));
            QuoteRepository quoteRep = new QuoteRepository();

            optionsService = new OptionsApplicationService(optionsRepository);
            exerciseService = new ExerciseApplicationService(exerciseRepository);
            socializeService = new SocializeApplicationService(socializeRepository);
            quoteService = new QuoteApplicationService(quoteRep);
            hasBeenSetup = true;
        }
    }

    /**
     * All the following get methods return an application service. If the factory hasn't been setup it will throw
     * an InvalidServiceUsage exception telling the developer what the problem is.
     */
    public static IOptionsApplicationService getOptionsApplicationService() throws InvalidServiceUsageException {
        if (!hasBeenSetup) {
            throw new InvalidServiceUsageException("You must call setupFactory()");
        }
        return optionsService;
    }

    public static ISocializeApplicationService getSocializeApplicationService() throws InvalidServiceUsageException {
        if (!hasBeenSetup) {
            throw new InvalidServiceUsageException("You must call setupFactory()");
        }
        return socializeService;
    }
    public static IQuoteApplicationService getQuoteApplicationService() throws InvalidServiceUsageException {
        if (!hasBeenSetup) {
            throw new InvalidServiceUsageException("You must call setupFactory()");
        }
        return quoteService;
    }


    public static IExerciseApplicationService getExerciseApplicationService() throws InvalidServiceUsageException {
        if (!hasBeenSetup) {
            throw new InvalidServiceUsageException("You must call setupFactory()");
        }
        return exerciseService;
    }
}
