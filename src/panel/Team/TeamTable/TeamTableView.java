/**
 * View-класс панели таблицы команды
 */
package panel.Team.TeamTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TeamTableView extends JPanel {
    public JTable table = new JTable();

    public JScrollPane scrollPane = new JScrollPane(table);

    public JMenuItem removeItem = new JMenuItem("Удалить игрока");

    public JPopupMenu popupMenu = new JPopupMenu();

    public JButton importButton = new JButton("Импорт");
    public JButton exportButton = new JButton("Экспорт");
    public JRadioButton isTxtButton = new JRadioButton("txt");
    public JRadioButton isXmlButton = new JRadioButton("xml");
    public JTextField pathField = new JTextField(20);
    public JButton submitExportImportButton = new JButton();
    public JFrame importExportFrame = new JFrame();

    public TeamTableView () {
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.add(importButton);
        buttonsPanel.add(exportButton);

        add(buttonsPanel);
        add(scrollPane);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        popupMenu.add(removeItem);

        initFrame();
    }

    private void initFrame() {
        importExportFrame.setSize(400, 200);
        importExportFrame.setLocationRelativeTo(null);
        importExportFrame.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel radioPanel = new JPanel();
        radioPanel.add(isTxtButton);
        radioPanel.add(isXmlButton);

        mainPanel.add(new JLabel("Расширение:"));
        mainPanel.add(radioPanel);

        JPanel pathPanel = new JPanel();
        pathPanel.add(new JLabel("Путь:"));
        pathPanel.add(pathField);

        mainPanel.add(pathPanel);
        mainPanel.add(submitExportImportButton);

        mainPanel.setPreferredSize(new Dimension(300, 100));

        importExportFrame.add(mainPanel);
    }

    public void setTableModel (DefaultTableModel tableModel) {
        table.setModel(tableModel);

        tableModel.fireTableDataChanged();
    }

    public void showFrame(boolean isImport) {
        String title = "";

        if (isImport) {
            title = "Импорт";
        } else {
            title = "Экспорт";
        }

        importExportFrame.setVisible(true);
        importExportFrame.setTitle(title + " данных");

        submitExportImportButton.setText(title);
    }
}
