package me.hwproj.mafiagame.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;

/**
 * A class with a few static methods used to draw player's attention to the device
 */
public class NotifierInterractor {
    /**
     * Tries to vibrate the device.
     * This might not work on some devices. IDK, it does not work on my tablet,
     * but this might be a hardware problem .
     * @param context context is required to vibrate
     * @param millis  how long to vibrate
     */
    public static void vibrate(Context context, int millis) {
        Log.d("Vibro", "vibrate: vibrate!");
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Vibro", "vibrate: new API");
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            Log.d("Vibro", "vibrate: old API");
            vibrator.vibrate(millis);
        }
    }

    /**
     * Plays a button click sound.
     * This does not seem work on my phone.
     * @param view any view is required to play a sound effect
     */
    public static void playClick(View view) {
        view.playSoundEffect(SoundEffectConstants.CLICK);
    }
}
