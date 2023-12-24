package panel.Calendar;

import Types.Tuple;
import panel.AbstractController;
import utils.DatabaseUtils;
import utils.TeamsUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CalendarController extends AbstractController<HashMap<String, String>> {
    CalendarModel model;
    JFrame mainFrame;
    public CalendarView view;

    private int contextRow = -1;
    private int contextColumn = -1;

    HashMap<String, Integer> matchesIdMap = new HashMap<>();

    @Override
    public Component mainViewComponent() {
        return view.calendarPanel;
    }

    public CalendarController (JFrame mainFrame) {
        this.model = new CalendarModel();
        this.view = new CalendarView(mainFrame);

        this.loadData();

        view.table.setDefaultRenderer(Object.class, new CustomCellRenderer(model));

        TableColumnModel tcm = view.playersStatsController.view.table.getColumnModel();
        tcm.removeColumn(tcm.getColumn(1));

        updateCalendar();

        initHandlers();
    }

    public HashMap<String, String> loadData () {
        HashMap<String, String> matchesMap = new HashMap<String, String>();
        Object[][] data = readMatches();
        HashMap<Integer, String> resultsMap = readResults();

        int rows = Array.getLength(data);

        for (int i = 0; i < rows; i++) {
            assert data != null;

            Object team = data[i][0];
            Object date = data[i][1];

            if (team != null && date != null) {
                matchesMap.put(date.toString(), team.toString());
            }
        }

        model.setMatches(matchesMap, resultsMap, matchesIdMap);

        return matchesMap;
    }

    public void reloadView() {
        updateCalendar();
    }

    private Object[][] readMatches() {
        List<Object[]> matchesList = new ArrayList<>();
        HashMap<String, Integer> matchesId = new HashMap<>();
        String query = "SELECT * FROM matches\n" +
                "LEFT JOIN teams ON matches.enemy_team_id = teams.team_id";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int match_id = resultSet.getInt("match_id");
                    Date match_date = resultSet.getDate("match_date");
                    String team_name = resultSet.getString("team_name");
                    boolean is_home_match = resultSet.getBoolean("is_home_match");

                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    String formattedDate = format.format(match_date);

                    Object[] playerData = {team_name, formattedDate, is_home_match};

                    matchesList.add(playerData);
                    matchesId.put(formattedDate, match_id);
                }

                this.matchesIdMap = matchesId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchesList.toArray(new Object[0][]);
    }

    private HashMap<Integer, String> readResults () {
        HashMap<Integer, String> results = new HashMap<>();
        String query = "SELECT * FROM results;";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int match_id = resultSet.getInt("match_id");
                    int goals_scored = resultSet.getInt("goals_scored");
                    int goals_missed = resultSet.getInt("goals_missed");
                    String resultString = goals_scored + " - " + goals_missed;


                    results.put(match_id, resultString);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    private void initHandlers () {
        view.leftChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.minusMonth();

                updateCalendar();
            }
        });

        view.rightChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.plusMonth();

                updateCalendar();
            }
        });

        view.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = view.table.rowAtPoint(e.getPoint());
                    int column = view.table.columnAtPoint(e.getPoint());
                    if (row >= 0 && row < view.table.getRowCount() && column >= 0 && column < view.table.getColumnCount()) {
                        String team = model.getTeam(row, column);
                        String stringDate = model.getCellDate(row, column);
                        Date cellDate = null;
                        Date today = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                        if (Objects.equals(stringDate, "")) {
                            return;
                        }

                        try {
                            cellDate = format.parse(stringDate);
                        } catch (Exception ex) {
                            ex.printStackTrace();

                            return;
                        }

                        String resultString = model.getResult(stringDate);

                        if (team != null && team != "") {
                            view.removeItem.setEnabled(true);
                            view.addItem.setEnabled(false);

                            if (cellDate != null && cellDate.before(today) && resultString == null) {
                                view.resultItem.setEnabled(true);
                            } else {
                                view.resultItem.setEnabled(false);
                            }
                        } else {
                            view.removeItem.setEnabled(false);
                            view.addItem.setEnabled(true);
                            view.resultItem.setEnabled(false);
                        }

                        view.table.changeSelection(row, column, false, false);
                        view.popupMenu.show(view.table, e.getX(), e.getY());

                        contextRow = row;
                        contextColumn = column;
                    }
                }
            }
        });

        view.removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String team = model.getTeam(contextRow, contextColumn);

                if (team == null || team == "") {
                    return;
                }

                String date = model.getCellDate(contextRow, contextColumn);

                removeMatch(date);

                loadData();
                reloadView();
            }
        });
        view.addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String team = model.getTeam(contextRow, contextColumn);

                if (team != null && team != "") {
                    return;
                }

                view.addMatchDialog.setVisible(true);
            }
        });

        view.resultItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.addResultDialog.setVisible(true);
            }
        });

        view.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String team = view.getTeam();
                String date = model.getCellDate(contextRow, contextColumn);
                boolean isHomeMatch = view.getIsHome();

                CompletableFuture.runAsync(() -> {
                    addMatch(team, date, isHomeMatch);
                }).thenRun(() -> {
                    view.addMatchDialog.setVisible(false);

                    loadData();
                    reloadView();
                });
            }
        });

        view.addResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                                String teamGoals = view.teamGoals.getText();
                String enemyGoals = view.enemyGoals.getText();

                try {
                    int parsedTeamGoals = Integer.parseInt(teamGoals);
                    int parsedEnemyGoals = Integer.parseInt(enemyGoals);

                    DefaultTableModel playerStatsModel = (DefaultTableModel)view.playersStatsController.view.table.getModel();
                    HashMap<String, Tuple<Integer, Float>> statsToWrite = new HashMap<>();
                    int rows = playerStatsModel.getRowCount();

                    for (int i = 0; i < rows; i++) {
                        Vector rowData = playerStatsModel.getDataVector().elementAt(i);
                        Object playerName = rowData.get(0);
                        Object goals = rowData.get(2);
                        Object successfulCoefficient = rowData.get(3);

                        if (!Objects.equals(playerName, "") && !Objects.equals(goals, "") && !Objects.equals(successfulCoefficient, "")) {
                            try {
                                String parsedPlayerName = playerName.toString();
                                int parsedGoals = Integer.parseInt(goals.toString());
                                float parsedSC = Float.parseFloat(successfulCoefficient.toString());

                                statsToWrite.put(parsedPlayerName, new Tuple<>(parsedGoals, parsedSC));
                            } catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    String matchDate = model.getCellDate(contextRow, contextColumn);
                    int matchId = matchesIdMap.get(matchDate);

                    addMatchResult(matchId, parsedTeamGoals, parsedEnemyGoals, statsToWrite);

                    loadData();
                    reloadView();
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                view.addResultDialog.setVisible(false);
            }
        });

        KeyAdapter numberInputAdapter = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        };

        view.teamGoals.addKeyListener(numberInputAdapter);
        view.enemyGoals.addKeyListener(numberInputAdapter);
    }

    private void addMatchResult (int matchId, int teamGoals, int enemyGoals, HashMap<String, Tuple<Integer, Float>> playerStats) {
        String matchQuery = "insert into results (match_id, goals_scored, goals_missed)\n" +
                "values (?, ?, ?);";

        String statsQuery = "insert into player_stats (player_id, goals_amount, successper, match_id) values (?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(matchQuery);
            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, teamGoals);
            preparedStatement.setInt(3, enemyGoals);

            preparedStatement.executeUpdate();

            PreparedStatement preparedStatement1 = DatabaseUtils.connection.prepareStatement(statsQuery);

            for(Map.Entry<String, Tuple<Integer, Float>> entry : playerStats.entrySet()) {
                String fullName = entry.getKey();
                int playerId = TeamsUtils.getPlayerId(fullName);
                Tuple<Integer, Float> tuple = entry.getValue();

                preparedStatement1.setInt(1, playerId);
                preparedStatement1.setInt(2, tuple.x);
                preparedStatement1.setFloat(3, tuple.y);
                preparedStatement1.setInt(4, matchId);
                preparedStatement1.addBatch();
            }

            preparedStatement1.executeBatch();

            JOptionPane.showMessageDialog(view.table, "Результат добавлен");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCalendar () {
        YearMonth yearMonth = model.yearMonth;
        int monthNum = yearMonth.getMonthValue();
        int yearNum = yearMonth.getYear();
        Month month = Month.of(monthNum);
        Locale loc = Locale.forLanguageTag("ru");
        String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, loc);

        DefaultTableModel newModel = model.buildTableModel();

        view.setTableModel(newModel);
        view.setMonth(monthName, yearNum);
    }

    private Date parseDate (String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date parsedDate = dateFormat.parse(date);

        return parsedDate;
    }

    private void removeMatch (String dateToRemove) {
        String query = "DELETE FROM matches\n" +
                "\tWHERE matches.match_date = ?;";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            Date matchDate = parseDate(dateToRemove);
            preparedStatement.setDate(1, new java.sql.Date(matchDate.getTime()));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(view.table, "Матч удален");
        } catch (SQLException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(view.table, "Ошибка удаления матча");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void addMatch(String teamName, String date, boolean isHomeMatch) {
        int teamId = TeamsUtils.getTeamId(teamName);

        String query = "INSERT INTO public.matches(\n" +
                "\tmatch_date, enemy_team_id, is_home_match)\n" +
                "\tVALUES (?, ?, ?);";

        try {
            Date matchDate = parseDate(date);
            PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query);
            preparedStatement.setDate(1, new java.sql.Date(matchDate.getTime()));
            preparedStatement.setInt(2, teamId);
            preparedStatement.setBoolean(3, isHomeMatch);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(view.table, "Матч добавлен");
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getSQLState().equals("23505")) {
                JOptionPane.showMessageDialog(view.table, "Номер игрока должен быть уникальным");
            }
        }
        catch (NumberFormatException | ParseException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(view.table, "Данные заполнены некорректно");
        }
    }
}
