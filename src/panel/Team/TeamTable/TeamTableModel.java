/**
 * Model-класс панели таблицы команды
 */

package panel.Team.TeamTable;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class TeamTableModel {
    Object[][] data = new Object[0][0];

    public void setData (Object[][] data) {
        this.data = data;
    }

    public enum TableColumns {
        NUMBER ("№"),
        FULL_NAME ("ФИО"),
        AGE ("Возраст"),
        POSITION ("Позиция"),
        TELEPHONE ("Номер телефона"),
        DATE ("Дата истечения контракта");

        private String title;

        TableColumns(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public DefaultTableModel buildTableModel() {
        TableColumns[] columns = TableColumns.values();
        ArrayList<String> displayNames = new ArrayList<String>();

        for (TableColumns column : columns) {
            displayNames.add(column.getTitle());
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, displayNames.toArray());

        return tableModel;
    }
}
