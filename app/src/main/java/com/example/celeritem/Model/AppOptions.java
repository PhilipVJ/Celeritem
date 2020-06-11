package com.example.celeritem.Model;

import java.io.Serializable;

public class AppOptions implements Serializable {
    private int accuracy;
    private boolean sound;
    private boolean kilometerNotification;
    private boolean showQuotes;

    public AppOptions(int accuracy, boolean sound, boolean kilometerNotification, boolean showQuotes) {
        this.accuracy = accuracy;
        this.sound = sound;
        this.kilometerNotification = kilometerNotification;
        this.showQuotes = showQuotes;
    }

    public boolean showQuotes() {
        return showQuotes;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public boolean hasSound() {
        return sound;
    }

    public boolean hasKilometerNotification() {
        return kilometerNotification;
    }
}
