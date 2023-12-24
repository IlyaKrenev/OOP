package panel.Calendar;

import utils.TeamsUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class CustomCellRenderer extends DefaultTableCellRenderer {
    CalendarModel model;

    public CustomCellRenderer (CalendarModel model) {
        this.model = model;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel cellPanel = initPanel();

        if (isSelected) {
            cellPanel.setBackground(table.getSelectionBackground());
            cellPanel.setForeground(table.getSelectionForeground());
        } else {
            cellPanel.setBackground(table.getBackground());
            cellPanel.setForeground(table.getForeground());
        }
        if (hasFocus) {
            cellPanel.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                cellPanel.setForeground(UIManager.getColor("Table.focusCellForeground"));
                cellPanel.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        }

        if (value != null) {
            String cellText = value.toString();

            cellPanel.add(new JLabel(cellText));

            String team = model.getTeam(row, column);
            String date = model.getCellDate(row, column);
            String result = model.getResult(date);

            if (!Objects.equals(team, "")) {
                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.setBackground(new Color(255, 255, 255, 0));

                String iconPath = TeamsUtils.getTeamIcon(team);
                ImagePanel imagePanel = new ImagePanel(iconPath);

                container.add(imagePanel);

                if (result != null) {
                    JLabel resultLabel = new JLabel(result);

                    container.add(resultLabel);
                }

                cellPanel.add(container);
            }
        }

        return cellPanel;
    }
    private JPanel initPanel () {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));

        return panel;
    }
}

class ImagePanel extends JPanel {
    private ImageIcon imageIcon;

    public ImagePanel (String iconPath) {
        imageIcon = new ImageIcon(iconPath);

        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);

        setBackground(new Color(255, 255, 255, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        imageIcon.paintIcon(this, g, 0, 0);
    }
}