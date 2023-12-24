/**
 * Controller-класс панели статистики команды
 */

package panel.Team.TeamStats;

import panel.AbstractController;
import utils.CustomPanelListener;
import utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TeamStatsController extends AbstractController<HashMap<TeamStatsModel.TableColumns, Number>> {
    TeamStatsModel model;
    public TeamStatsView view;
    public TeamStatsController () {
        model = new TeamStatsModel();

        loadData();

        view = new TeamStatsView(model.getDataMap());

        view.panel.addComponentListener(new CustomPanelListener(this));
    }

    @Override
    public Component mainViewComponent() {
        return view.panel;
    }

    public HashMap<TeamStatsModel.TableColumns, Number> loadData () {
        HashMap<TeamStatsModel.TableColumns, Number> data = loadStats();

        model.setData(data);

        return data;
    }

    public void reloadView() {
        view.redraw(model.getDataMap());
        view.panel.repaint();
    }

    private HashMap<TeamStatsModel.TableColumns, Number> loadStats () {
        HashMap<TeamStatsModel.TableColumns, Number> map = new HashMap<TeamStatsModel.TableColumns, Number>();
        String query = "SELECT \n" +
                "    SUM(CASE WHEN goals_scored = goals_missed THEN 1 ELSE 0 END) AS \"draws\",\n" +
                "    SUM(CASE WHEN goals_scored > goals_missed THEN 1 ELSE 0 END) AS \"wins\",\n" +
                "    SUM(goals_scored) AS \"goals_scored\",\n" +
                "    SUM(goals_missed) AS \"goals_missed\",\n" +
                "    SUM(CASE WHEN goals_scored < goals_missed THEN 1 ELSE 0 END) AS \"loses\"\n" +
                "FROM Results";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();

                int draws = resultSet.getInt("draws");
                int wins = resultSet.getInt("wins");
                int goals_scored = resultSet.getInt("goals_scored");
                int goals_missed = resultSet.getInt("goals_missed");
                int loses = resultSet.getInt("loses");

                map.put(TeamStatsModel.TableColumns.GOALS_SCORED, goals_scored);
                map.put(TeamStatsModel.TableColumns.GOALS_MISSED, goals_missed);
                map.put(TeamStatsModel.TableColumns.WINS, wins);
                map.put(TeamStatsModel.TableColumns.LOSSES, loses);
                map.put(TeamStatsModel.TableColumns.DRAWS, draws);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }
}
