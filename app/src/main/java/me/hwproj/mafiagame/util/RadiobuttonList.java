package me.hwproj.mafiagame.util;

import android.content.Context;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of radiobuttons from which only one can be picked simultaneously.
 */
public class RadiobuttonList {
    private final List<RadioButton> buttons = new ArrayList<>();
    private int currentPick = -1;
    private final List<RadiolistPickListener> listeners = new ArrayList<>();

    public RadiobuttonList(Context context, int buttonsNumber) {
        for (int i = 0; i < buttonsNumber; i++) {
            buttons.add(new RadioButton(context));
        }

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton b = buttons.get(i);
            final int thisButtonNumber = i;
            b.setOnCheckedChangeListener((button, state) -> {
                if (!state) {
                    return;
                } // proceed only if checked a button

                setNewCurrentPick(thisButtonNumber);
            });
        }
    }

    public void setNewCurrentPick(int newPick) {
        for (int j = 0; j < buttons.size(); j++) {
            if (j != newPick) {
                buttons.get(j).setChecked(false);
            } else {
                buttons.get(j).setChecked(true);
            }
        }
        updateCurrentPick(newPick);
    }

    public void setOnPickListener(RadiolistPickListener listener) {
        listeners.add(listener);
    }

    public RadioButton getButton(int index) {
        return buttons.get(index);
    }

    public void setEnabledAll(boolean enabled) {
        for (RadioButton button :
                buttons) {
            button.setEnabled(enabled);
        }
    }

    public int size() {
        return buttons.size();
    }

    public int getCurrentPick() {
        return currentPick;
    }

    private void updateCurrentPick(int newPick) {
        currentPick = newPick;
        for (RadiolistPickListener listener : listeners) {
            listener.acceptNewPick(newPick);
        }
    }
}
