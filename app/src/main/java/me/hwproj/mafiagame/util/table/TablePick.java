package me.hwproj.mafiagame.util.table;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import me.hwproj.mafiagame.R;
import me.hwproj.mafiagame.gameflow.ClientGameData;
import me.hwproj.mafiagame.gameflow.Player;
import me.hwproj.mafiagame.util.RadiobuttonList;
import me.hwproj.mafiagame.util.RadiolistPickListener;

/**
 * A table with a row for every players and a few columns with RadiobuttonList-s.
 * Some of them are activated and some are not.
 *
 * It also provides interface for disabling/enabling ability to pick a certain player and
 * change picked players
 */
public class TablePick {

    private final boolean[] disabledRows;
    private final boolean[] disabledColumns;
    private final RadiobuttonList[] columnsButtons;

    public TablePick(Context context, ClientGameData data, TableLayout table, int enabledCount) {
        this(context, data, table, enabledCount, 0, false);
    }

    public TablePick(Context context, ClientGameData data, TableLayout table, int enabledCount, int disabledCount, boolean addNobodyRow) {
        int rowCount = data.players.size();
        if (addNobodyRow) {
            rowCount++;
        }

        int columns = enabledCount + disabledCount;
        disabledRows = new boolean[rowCount];
        this.disabledColumns = new boolean[columns];
        this.columnsButtons = new RadiobuttonList[columns];

        for (int columnId = 0; columnId < columns; columnId++) {
            columnsButtons[columnId] = new RadiobuttonList(context, rowCount);
        }
        for (int disabledColumnId = enabledCount; disabledColumnId < columns; disabledColumnId++) {
            setEnablePickingColumn(disabledColumnId, false);
        }

        for (int playerId = 0; playerId < data.players.size(); playerId++) {
            Player currentPlayer = data.players.get(playerId);

            TableRow row = new TableRow(context);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView name = new TextView(context);
            name.setText(String.format("%s%s", currentPlayer.name, currentPlayer.dead ? " X_X" : ""));
            row.addView(name);

            for (int columnId = 0; columnId < columns; columnId++) {
                row.addView(columnsButtons[columnId].getButton(playerId));
            }

            table.addView(row);
        }

        if (addNobodyRow) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView name = new TextView(context);
            name.setText(context.getString(R.string.nobody_pick));
            row.addView(name);

            for (int columnId = 0; columnId < columns; columnId++) {
                row.addView(columnsButtons[columnId].getButton(rowCount - 1));
            }

            table.addView(row);
        }
    }

    public void setEnablePickingRow(int row, boolean enable) {
        disabledRows[row] = !enable;
        for (int column = 0; column < disabledColumns.length; column++) {
            updateEnabled(row, column);
        }
    }

    public void setEnablePickingColumn(int column, boolean enable) {
        disabledColumns[column] = !enable;
        for (int row = 0; row < disabledRows.length; row++) {
            updateEnabled(row, column);
        }
    }

    public void setColumnListener(int column, RadiolistPickListener listener) {
        columnsButtons[column].setOnPickListener(listener);
    }

    public void setColumnPick(int column, int pick) {
        columnsButtons[column].setNewCurrentPick(pick);
    }

    private boolean isEnabled(int row, int column) {
        return !(disabledRows[row] || disabledColumns[column]);
    }

    private void updateEnabled(int row, int column) {
        RadioButton button = columnsButtons[column].getButton(row);
        if (!isEnabled(row, column)) {
            if (button.isChecked()) {
                columnsButtons[column].setNewCurrentPick(-1);
            }
            button.setEnabled(false);
        } else {
            button.setActivated(true);
        }
    }
}