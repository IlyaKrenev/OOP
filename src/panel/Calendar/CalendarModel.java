package panel.Calendar;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Objects;

public class CalendarModel {
    private HashMap<String, String> matchesMap = new HashMap<String, String>();
    private HashMap<Integer, String> resultsMap = new HashMap<Integer, String>();
    private HashMap<String, Integer> matchesIdMap = new HashMap<>();
    public String[] monthNames = {"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};

    public YearMonth yearMonth = YearMonth.now();

    DefaultTableModel currentDataModel;

    public void plusMonth () {
        yearMonth = yearMonth.plusMonths(1);
    }

    public void minusMonth () {
        yearMonth = yearMonth.minusMonths(1);
    }

    public void setMatches (
            HashMap<String, String> matchesMap,
            HashMap<Integer, String> resultsMap,
            HashMap<String, Integer> matchesIdMap
    ) {
        this.matchesMap = matchesMap;
        this.resultsMap = resultsMap;
        this.matchesIdMap = matchesIdMap;
    }

    public String getTeam (int row, int column) {
        Object cellValue = currentDataModel.getValueAt(row, column);

        if (cellValue == null) {
            return "";
        }

        try {
            String date = getCellDate(row, column);
            String team = matchesMap.get(date);

            return Objects.requireNonNullElse(team, "");
        } catch (NumberFormatException nfe) {
            return "";
        }
    }

    public String getResult (String date) {
        if (Objects.equals(date, "")) {
            return null;
        }

        try {
            int matchId = matchesIdMap.get(date);

            return resultsMap.get(matchId);
        } catch (Exception e) {
            return null;
        }
    }

    public String getCellDate (int row, int column) {
        Object cellValue = currentDataModel.getValueAt(row, column);

        if (cellValue == null) {
            return "";
        }

        try {
            Integer parsed = Integer.parseInt(cellValue.toString());
            String stringedDay = parsed.toString();

            if (parsed > 0 && parsed < 10) {
                stringedDay = "0" + stringedDay;
            }

            int month = yearMonth.getMonthValue();
            String stringedMonth = Integer.toString(month);

            if (month > 0 && month < 10) {
                stringedMonth = "0" + month;
            }

            String date = stringedDay + "." + stringedMonth + "." + yearMonth.getYear();

            return date;
        } catch (NumberFormatException nfe) {
            return "";
        }
    }

    public DefaultTableModel buildTableModel () {
        int daysInMonth = yearMonth.lengthOfMonth();
        Object[][] data = new Object[6][7];

        LocalDate date = yearMonth.atDay(1);
        int offset = date.getDayOfWeek().getValue() - 1 % 7;

        // Заполнение календаря номерами дней
        int dayCounter = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j < offset) {
                    data[i][j] = "";
                } else if (dayCounter <= daysInMonth) {
                    data[i][j] = dayCounter;
                    dayCounter++;
                }
            }
        }

        currentDataModel = new DefaultTableModel(data, monthNames);

        return currentDataModel;
    }
}
