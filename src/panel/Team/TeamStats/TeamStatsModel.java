/**
 * Model-класс панели статистики команды
 */

package panel.Team.TeamStats;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamStatsModel {
    HashMap<TableColumns, Number> dataMap = new HashMap<>();

    public enum TableColumns {
        GOALS_SCORED ("Забито голов:"),
        GOALS_MISSED ("Пропущено голов:"),
        WINS("Выиграно матчей:"),
        LOSSES ("Проиграно матчей:"),
        DRAWS ("Ничейных матчей:");

        private String title;

        TableColumns(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public static String[] getTitles () {
            ArrayList<String> displayNames = new ArrayList<String>();

            for (TableColumns column : TableColumns.values()) {
                displayNames.add(column.getTitle());
            }

            return displayNames.toArray(String[]::new);
        }
    }

    public void setData (HashMap<TableColumns, Number> dataMap) {
        this.dataMap = dataMap;
    }

    public HashMap<TableColumns, Number> getDataMap () {
        return dataMap;
    }
}
