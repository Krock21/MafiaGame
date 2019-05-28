package me.hwproj.mafiagame.util;

import android.app.AlertDialog;
import android.content.Context;

public class Alerter {
    public static void alert(Context context, String title, String text) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(text);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", (dialog, which) -> dialog.dismiss());
        alert.show();
    }
}
