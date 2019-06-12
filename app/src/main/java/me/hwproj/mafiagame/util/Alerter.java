package me.hwproj.mafiagame.util;

import android.app.AlertDialog;
import android.content.Context;

public class Alerter {
    /**
     * Shows an AlertDialog with provided title and text
     * @param context context is required to show AlertDialogs
     * @param title
     * @param text
     */
    public static void alert(Context context, String title, String text) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(text);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", (dialog, which) -> dialog.dismiss());
        alert.show();
    }
}
