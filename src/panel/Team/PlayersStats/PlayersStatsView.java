/**
 * View-класс панели статистики игроков
 */

package panel.Team.PlayersStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PlayersStatsView {
    boolean readOnly;
    public PlayersStatsView (boolean readOnly) {
        this.readOnly = readOnly;
    }

    public JTable table = new JTable() {
        public boolean isCellEditable(int row, int column) {
            return !readOnly;
        };
    };

    public JScrollPane scrollPane = new JScrollPane(table);

    public void setTableModel (DefaultTableModel tableModel) {
        table.setModel(tableModel);

        tableModel.fireTableDataChanged();
    }
}
