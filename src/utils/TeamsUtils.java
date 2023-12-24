package utils;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TeamsUtils {
    private static HashMap<String, Integer> idsMap = new HashMap<>();

    private static HashMap<String, Integer> playerIdMap = new HashMap<>();

    public static void init () throws SQLException {
        String teamQuery = "SELECT * FROM teams;";
        String playersQuery = "SELECT * FROM players;";

        PreparedStatement teamPreparedStatement = DatabaseUtils.connection.prepareStatement(teamQuery);
        ResultSet resultSet = teamPreparedStatement.executeQuery();

        while (resultSet.next()) {
            int team_id = resultSet.getInt("team_id");
            String team_name = resultSet.getString("team_name");

            idsMap.put(team_name, team_id);
        }

        PreparedStatement teamPreparedStatement1 = DatabaseUtils.connection.prepareStatement(playersQuery);
        ResultSet resultSet1 = teamPreparedStatement1.executeQuery();

        while (resultSet1.next()) {
            int player_id = resultSet1.getInt("player_id");
            String full_name = resultSet1.getString("full_name");

            playerIdMap.put(full_name, player_id);
        }
    }

    public static String getTeamIcon (String teamName) {
        String baseUrl = "src/img/teams/";
        String baseExt = ".png";
        String imgUrl = baseUrl + teamName + baseExt;
        File imgFile = new File(imgUrl);

        if (!imgFile.exists()) {
            return baseUrl + "NOT_FOUND" + baseExt;
        }

        return imgUrl;
    }

    public static String[] getTeams () {
        return idsMap.keySet().toArray(new String[0]);
    }

    public static int getTeamId (String teamName) {
        return idsMap.get(teamName);
    }

    public static int getPlayerId (String playerName) {
        return playerIdMap.get(playerName);
    }
}
