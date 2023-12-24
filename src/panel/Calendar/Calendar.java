package panel.Calendar;

import utils.CustomPanelListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

/**
 * Класс для отображения календаря
 */
public class Calendar extends JPanel {
    public Calendar (JFrame mainFrame) {
        setLayout(new GridLayout());

        CalendarController controller = new CalendarController(mainFrame);


        addComponentListener(new CustomPanelListener(controller));

        add(controller.mainViewComponent());
    }
}
