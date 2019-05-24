package me.hwproj.mafiagame.util.table;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.LiveData;

import me.hwproj.mafiagame.gameflow.ClientGameData;
import me.hwproj.mafiagame.gameflow.Player;

public class TablePickData {
    private final ClientGameData data;
    private final int pickColumns;
    private final int additionalColumns;

    private final boolean[] forbiddenToPick;
//    private final RadiobuttonList[] enabledColumnsButtons;
//    private final RadiobuttonList[] disabledColumnsButtons;

    public TablePickData(ClientGameData data, int enabledColumns, int disabledColumns) {
        this.data = data;
        this.pickColumns = enabledColumns;
        this.additionalColumns = disabledColumns;
        forbiddenToPick = new boolean[data.players.size()];
//        this.enabledColumnsButtons = new RadiobuttonList[enabledColumns];
//        this.disabledColumnsButtons = new RadiobuttonList[disabledColumns];

//        for (int enabledColumnId = 0; enabledColumnId < enabledColumns; enabledColumnId++) {
//            enabledColumnsButtons[enabledColumnId] = new RadiobuttonList(context, data.players.size());
//        }
//        for (int disabledColumnId = 0; disabledColumnId < disabledColumns; disabledColumnId++) {
//            disabledColumnsButtons[disabledColumnId] = new RadiobuttonList(context, data.players.size());
//            disabledColumnsButtons[disabledColumnId].setEnabledAll(false);
//        }

//        for (int playerId = 0; playerId < data.players.size(); playerId++) {
//            Player currentPlayer = data.players.get(playerId);
//
//            TableRow row = new TableRow(context);
//            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            TextView name = new TextView(context);
//            name.setText(currentPlayer.name);
//            row.addView(name);
//
//            for (int enabledColumnId = 0; enabledColumnId < enabledColumns; enabledColumnId++) {
//                row.addView(enabledColumnsButtons[enabledColumnId].getButton(playerId));
//            }
//            for (int disabledColumnId = 0; disabledColumnId < disabledColumns; disabledColumnId++) {
//                row.addView(disabledColumnsButtons[disabledColumnId].getButton(playerId));
//            }
//
//            table.addView(row);
//        }
    }

    public void setEnablePicking(int playerId, boolean enable) {
        forbiddenToPick[playerId] = enable;
//        for (int i = 0; i < enabledColumnsButtons.length; i++) {
//            RadioButton button = enabledColumnsButtons[i].getButton(playerId);
//            if (!enable) {
//                button.setActivated(false);
//                button.setEnabled(false);
//            } else {
//                button.setActivated(true);
//            }
//        }
    }
}
