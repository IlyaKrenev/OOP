/**
 * Controller-класс панели статистики игроков
 */

package panel.Team.PlayersStats;

import panel.AbstractController;
import utils.CustomPanelListener;
import utils.DatabaseUtils;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayersStatsController extends AbstractController<Object[][]> {
    public PlayersStatsView view;
    private PlayersStatsModel model;

    boolean readOnly;
    boolean needSetStats;

    @Override
    public Component mainViewComponent() {
        return view.scrollPane;
    }

    public PlayersStatsController (boolean readOnly, boolean needSetStats) {
        this.view = new PlayersStatsView(readOnly);
        this.model = new PlayersStatsModel();
        this.readOnly = readOnly;
        this.needSetStats = needSetStats;

        loadData();

        DefaultTableModel tableModel = model.buildTableModel();

        view.setTableModel(tableModel);

        view.scrollPane.addComponentListener(new CustomPanelListener(this));
    }

    public Object[][] loadData () {
        Object[][] data = getStats();

        model.setData(data);

        return data;
    }

    public void reloadView() {
        DefaultTableModel tableModel = model.buildTableModel();

        view.setTableModel(tableModel);

        if (tableModel instanceof DefaultTableModel) {
            ((DefaultTableModel) tableModel).fireTableDataChanged();
        }
    }

    private Object[][] getStats () {
        List<Object[]> playersList = new ArrayList<>();
        String query = "SELECT players.full_name as full_name, \n" +
                "       COUNT(player_stats.player_id) AS games_played, \n" +
                "       SUM(player_stats.goals_amount) AS total_goals, \n" +
                "       AVG(player_stats.successper) AS average_success_rate\n" +
                "FROM players\n" +
                "LEFT JOIN player_stats ON players.player_id = player_stats.player_id\n" +
                "GROUP BY players.full_name;";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String full_name = resultSet.getString("full_name");
                    int games_played = resultSet.getInt("games_played");
                    int total_goals = resultSet.getInt("total_goals");
                    float average_success_rate = resultSet.getInt("average_success_rate");

                    if (needSetStats) {
                        Object[] playerData = {full_name, games_played, total_goals, average_success_rate};

                        playersList.add(playerData);
                    } else {
                        Object[] playerData = {full_name, "", "", ""};

                        playersList.add(playerData);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playersList.toArray(new Object[0][]);
    }
}
