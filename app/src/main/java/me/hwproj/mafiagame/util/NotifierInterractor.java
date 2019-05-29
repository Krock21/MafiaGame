package me.hwproj.mafiagame.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;

public class NotifierInterractor {
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

    public static void playClick(View view) {
        view.playSoundEffect(SoundEffectConstants.CLICK);
    }
}
