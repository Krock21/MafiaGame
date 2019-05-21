package me.hwproj.mafiagame.util;

import android.content.Context;
import android.widget.RadioButton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class RadiobuttonList {
    private final List<RadioButton> buttons;
    private final MutableLiveData<Integer> currentPick = new MutableLiveData<>();

    public RadiobuttonList(Context context, int buttonNumber) {
        buttons = new ArrayList<>();
        for (int i = 0; i < buttonNumber; i++) {
            buttons.add(new RadioButton(context));
        }

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton b = buttons.get(i);
            final int iCopy = i;
            b.setOnCheckedChangeListener((button, state) -> {
                if (!state) {
                    return;
                }

                for (int j = 0; j < buttons.size(); j++) {
                    if (j != iCopy) {
                        buttons.get(j).setChecked(false);
                    }
                }

                currentPick.setValue(iCopy);
            });
        }
    }

    public LiveData<Integer> getCurrentPick() {
        return currentPick;
    }
}
