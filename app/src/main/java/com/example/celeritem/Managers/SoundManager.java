package com.example.celeritem.Managers;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.celeritem.R;

/**
 * Handles all sound in the application. Currently only two sounds.
 */
public class SoundManager {

    private static MediaPlayer sound; // The sound has to be saved or the garbage collector will remove it too fast

    public static void playClickSound(Context context) {
        sound = MediaPlayer.create(context, R.raw.click);
        sound.start();
    }

    public static void playVictorySound(Context context) {
        sound = MediaPlayer.create(context, R.raw.result);
        sound.start();
    }
}
