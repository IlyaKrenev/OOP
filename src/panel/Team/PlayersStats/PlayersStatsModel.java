/**
 * Model-класс панели статистики игроков
 */

package panel.Team.PlayersStats;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class PlayersStatsModel {
    Object[][] data = new Object[0][0];

    public void setData (Object[][] data) {
        this.data = data;
    }

    public enum TableColumns {
        FULL_NAME ("ФИО"),
        GAMES_AMOUNT ("Количество игр"),
        GOALS_AMOUNT ("Количество голов"),
        SUCCESSFUL_ACTIONS ("% успешных действий");

        private String title;

        TableColumns(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public DefaultTableModel buildTableModel() {
        PlayersStatsModel.TableColumns[] columns = PlayersStatsModel.TableColumns.values();
        ArrayList<String> displayNames = new ArrayList<String>();

        for (PlayersStatsModel.TableColumns column : columns) {
            displayNames.add(column.getTitle());
        }

        return new DefaultTableModel(data, displayNames.toArray());
    }
}
