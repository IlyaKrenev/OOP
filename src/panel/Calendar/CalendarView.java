package panel.Calendar;

import panel.Team.PlayersStats.PlayersStatsController;
import utils.TeamsUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CalendarView {
    public JTable table = new JTable() {
        public boolean isCellEditable(int row, int column) {
            return false;
        };
    };

    JPanel monthPanel = new JPanel();

    JLabel monthLabel = new JLabel("");

    public JMenuItem addItem = new JMenuItem("Добавить матч");

    public JMenuItem resultItem = new JMenuItem("Внести результат");
    public JMenuItem removeItem = new JMenuItem("Удалить матч");

    public JPopupMenu popupMenu = new JPopupMenu();

    public JPanel calendarPanel = new JPanel();
    public JButton leftChange = new JButton("<");
    public JButton rightChange = new JButton(">");
    public JDialog addMatchDialog;

    public JDialog addResultDialog;

    public PlayersStatsController playersStatsController = new PlayersStatsController(false, false);

    JComboBox<String> teamInput = new JComboBox<>(TeamsUtils.getTeams());
    JTextField dateInput = new JTextField(20);
    JCheckBox isHomeCheckbox = new JCheckBox();
    JButton addButton = new JButton("Добавить");

    JTextField teamGoals = new JTextField(20);
    JTextField enemyGoals = new JTextField(20);

    JButton addResultButton = new JButton("Добавить");

    public String getTeam () {
        Object item = teamInput.getSelectedItem();

        if (item == null) {
            return "";
        }

        return item.toString();
    }

    public String getDate () {
        return dateInput.getText();
    }

    public boolean getIsHome () {
        return isHomeCheckbox.isSelected();
    }

    public void setTableModel (DefaultTableModel model) {
        table.setModel(model);

        model.fireTableDataChanged();
    }

    public void setMonth (String monthName, int yearNum) {
        monthLabel.setText(monthName + " " + yearNum);
    }

    public CalendarView (JFrame mainFrame) {
        initTable();
        initMonthPanel();
        initCalendarPanel();
        initDialogs(mainFrame);
    }

    private void initDialogs(JFrame mainFrame) {
        addMatchDialog = new JDialog(mainFrame, "Добавить матч", true);
        addMatchDialog.add(initAddMatchPanel());
        addMatchDialog.setSize(600, 300);
        addMatchDialog.setLayout(new BoxLayout(addMatchDialog.getContentPane(), BoxLayout.Y_AXIS));
        addMatchDialog.setLocationRelativeTo(mainFrame);

        addResultDialog = new JDialog(mainFrame, "Добавить результат матча", true);
        addResultDialog.add(initMatchResultPanel());
        addResultDialog.setSize(600, 600);
        addResultDialog.setLayout(new BoxLayout(addResultDialog.getContentPane(), BoxLayout.Y_AXIS));
        addResultDialog.setLocationRelativeTo(mainFrame);
    }

    private void initTable () {
        table.setRowHeight(60);

        popupMenu.add(addItem);
        popupMenu.add(resultItem);
        popupMenu.add(removeItem);
    }

    private void initMonthPanel () {
        monthLabel.setFont(new Font("Serif", Font.BOLD, 24));
        monthPanel.add(leftChange);
        monthPanel.add(monthLabel);
        monthPanel.add(rightChange);
        monthPanel.setPreferredSize(new Dimension(100, 30));
    }

    private void initCalendarPanel () {
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));
        calendarPanel.add(monthPanel);
        calendarPanel.add(new JScrollPane(table));
    }

    private JPanel initAddMatchPanel () {
        JLabel[] labels = {
                new JLabel("Команда противника"),
                new JLabel("Домашний матч")
        };

        JComponent[] inputs = {
                teamInput,
                isHomeCheckbox,
        };

        JButton buttonLogin = addButton;
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        for (int i = 0; i < labels.length; i++) {
            constraints.gridx = 0;
            constraints.gridy = i;
            newPanel.add(labels[i], constraints);

            constraints.gridx = 1;
            newPanel.add(inputs[i], constraints);
        }

        constraints.gridx = 0;
        constraints.gridy = labels.length;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        newPanel.add(buttonLogin, constraints);

        newPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Добавить матч"
        ));

        return newPanel;
    }

    private JPanel initMatchResultPanel () {
        JLabel[] labels = {
                new JLabel("Счет команды"),
                new JLabel("Счет противника")
        };

        JComponent[] inputs = {
                teamGoals,
                enemyGoals
        };

        JButton addResultButton = this.addResultButton;
        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        for (int i = 0; i < labels.length; i++) {
            constraints.gridx = 0;
            constraints.gridy = i;
            newPanel.add(labels[i], constraints);

            constraints.gridx = 1;
            newPanel.add(inputs[i], constraints);
        }


        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        main.add(newPanel);
        main.add(this.playersStatsController.view.scrollPane);
        main.add(addResultButton);

        main.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Добавить результат матча"
        ));

        return main;
    }
}
