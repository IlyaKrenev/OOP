/**
 * Controller-класс панели таблицы команды
 */

package panel.Team.TeamTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import panel.AbstractController;
import utils.CustomPanelListener;
import utils.DatabaseUtils;
import utils.FileUtils;
import utils.XmlUtils;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

public class TeamTableController extends AbstractController<Object[][]> {
    public TeamTableView view;
    private TeamTableModel model;

    private int rowToDelete = -1;

    String selectedExt = null;

    String currentAction = null;

    @Override
    public Component mainViewComponent() {
        return view;
    }

    public TeamTableController () {
        this.view = new TeamTableView();
        this.model = new TeamTableModel();

        loadData();

        view.scrollPane.addComponentListener(new CustomPanelListener(this));
    }

    public Object[][] loadData () {
        Object[][] tableData = readPlayers();

        model.setData(tableData);

        DefaultTableModel tableModel = model.buildTableModel();

        view.setTableModel(tableModel);

        tableModel.addRow(new Vector());

        initListeners();

        return tableData;
    }

    public void reloadView() {
        TableModel tableModel = view.table.getModel();

        if (tableModel instanceof DefaultTableModel) {
            ((DefaultTableModel) tableModel).fireTableDataChanged();
        }
    }

    private void initListeners () {
        // Обработчик изменения данных таблицы
        view.table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int rowCount = view.table.getRowCount();
                DefaultTableModel model = (DefaultTableModel) view.table.getModel();
                Vector newData = model.getDataVector().elementAt(rowCount - 1);

                boolean hasEmptyFields = false;

                for (int j = 0; j < newData.size(); j++) {
                    Object value = newData.get(j);

                    if (value == null || Objects.equals(value.toString(), "")) {
                        hasEmptyFields = true;
                    }
                }

                if (!hasEmptyFields) {
                    CompletableFuture.runAsync(() -> {
                        addPlayer(newData);
                    }).thenRun(() -> {
                        loadData();
                        reloadView();
                    });
                }
            }
        });

        view.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }

                JTable table = view.table;
                int rowCount = table.getRowCount();
                int row = table.rowAtPoint(e.getPoint());

                view.popupMenu.show(view.table, e.getX(), e.getY());

                if (row == rowCount - 1) {
                    view.removeItem.setEnabled(false);
                    rowToDelete = -1;
                } else {
                    view.removeItem.setEnabled(true);
                    rowToDelete = row;
                }
            }
        });


        view.removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rowToDelete == -1) {
                    return;
                }

                DefaultTableModel model = (DefaultTableModel) view.table.getModel();
                Vector player = model.getDataVector().elementAt(rowToDelete);
                Object num = player.get(0);

                try {
                    int parsedNum = Integer.parseInt(num.toString());

                    CompletableFuture.runAsync(() -> {
                        removePlayer(parsedNum);
                    }).thenRun(() -> {
                        loadData();
                    });
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });

        view.exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.showFrame(false);
                currentAction = "export";
            }
        });

        view.importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.showFrame(true);
                currentAction = "import";
            }
        });

        view.isTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.isTxtButton.setSelected(true);
                view.isXmlButton.setSelected(false);
                selectedExt = "txt";
            }
        });

        view.isXmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.isTxtButton.setSelected(false);
                view.isXmlButton.setSelected(true);
                selectedExt = "xml";
            }
        });
        view.submitExportImportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentExt = selectedExt;
                String path = view.pathField.getText();

                if (!Objects.equals(currentExt, "xml") && !Objects.equals(currentExt, "txt")) {
                    JOptionPane.showMessageDialog(view, "Не выбрано расширение");

                    return;
                }

                if (currentAction == null) {
                    return;
                }

                if (Objects.equals(currentAction, "import")) {
                    importData(path, currentExt);
                    loadData();
                    reloadView();
                } else {
                    exportData(path, currentExt);
                }

                currentAction = null;
            }
        });
    }

    private void importData (String path, String ext) {
        File pathFile = new File(path);

        String extension = "";

        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }

        if (!pathFile.isFile()) {
            JOptionPane.showMessageDialog(view, "Указанный путь не является файлом");

            return;
        }

        if (!Objects.equals(ext, extension)) {
            JOptionPane.showMessageDialog(view, "Файл имеет некорректное расширение");

            return;
        }

        Vector<Vector> playersData = new Vector<>();

        if (ext == "txt") {
            try {
                Object[][] players = FileUtils.readFile(path, 6);

                for (Object[] row : players) {
                    Vector playerData = new Vector();

                    for (int j = 0; j < row.length; j++) {
                        playerData.add(row[j]);
                    }

                    playersData.add(playerData);
                }
            } catch (Exception e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(view, "Ошибка чтения файла");

                return;
            }
        } else {
            try {
                Document xml = XmlUtils.readXML(path);

                NodeList players = xml.getElementsByTagName("player");

                int playersLength = players.getLength();

                try {
                    for (i = 0; i < playersLength; i++) {
                        Element match = (Element) players.item(i);
                        Node number = match.getElementsByTagName("number").item(0);
                        Node full_name = match.getElementsByTagName("full_name").item(0);
                        Node age = match.getElementsByTagName("age").item(0);
                        Node position = match.getElementsByTagName("position").item(0);
                        Node phone = match.getElementsByTagName("phone").item(0);
                        Node expire_date = match.getElementsByTagName("expire_date").item(0);

                        Vector playerData = new Vector<>();

                        playerData.add(number.getTextContent());
                        playerData.add(full_name.getTextContent());
                        playerData.add(age.getTextContent());
                        playerData.add(position.getTextContent());
                        playerData.add(phone.getTextContent());
                        playerData.add(expire_date.getTextContent());

                        playersData.add(playerData);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(view, "Ошибка чтения файла");

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(view, "Ошибка чтения файла");

                return;
            }
        }

        String query = "DELETE FROM players;";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for (int k = 0; k < playersData.size(); k++) {
            addPlayer(playersData.get(k));
        }

        JOptionPane.showMessageDialog(view.importExportFrame, "Данные успешно импортированы");
        view.importExportFrame.setVisible(false);
    }

    private void addElement (String key, Object value, Document doc, Element rootEl) {
        Element property = doc.createElement(key);
        property.appendChild(doc.createTextNode(value.toString()));
        rootEl.appendChild(property);
    }

    private void exportData (String path, String ext) {
        File pathFile = new File(path);

        if (!pathFile.isDirectory()) {
            JOptionPane.showMessageDialog(view, "Указанный путь не является директорией");

            return;
        }

        Object[][] players = readPlayers();
        String fileName = path + "\\" + "exportedTeams" + "." + ext;

        boolean isSuccessful = false;

        if (ext == "txt") {
            try {
                FileUtils.writeFile(players, fileName);

                isSuccessful = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("players");
                doc.appendChild(rootElement);

                for (Object[] player : players) {
                    Element playerElement = doc.createElement("player");

                    addElement("number", player[0], doc, playerElement);
                    addElement("full_name", player[1], doc, playerElement);
                    addElement("age", player[2], doc, playerElement);
                    addElement("position", player[3], doc, playerElement);
                    addElement("phone", player[4], doc, playerElement);
                    addElement("expire_date", player[5], doc, playerElement);

                    rootElement.appendChild(playerElement);
                }

                try {
                    XmlUtils.saveXML(fileName, doc);

                    isSuccessful = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isSuccessful) {
            view.importExportFrame.setVisible(false);

            JOptionPane.showMessageDialog(view, "Данные успешно экспортированы");
        } else {
            JOptionPane.showMessageDialog(view, "При экспорте возникла ошибка");
        }
    }

    private Object[][] readPlayers () {
        List<Object[]> playersList = new ArrayList<>();
        String query = "SELECT * FROM players";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int player_id = resultSet.getInt("player_id");
                    String full_name = resultSet.getString("full_name");
                    int age = resultSet.getInt("age");
                    String position = resultSet.getString("position");
                    String phone = resultSet.getString("phone");
                    String expire_date = resultSet.getString("expire_date");
                    Object[] playerData = {player_id, full_name, age, position, phone, expire_date};

                    playersList.add(playerData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playersList.toArray(new Object[0][]);
    }

    private void addPlayer (Vector playerData) {
        String query = "INSERT INTO public.players(\n" +
                "\tplayer_id, player_name, full_name, age, \"position\", phone, expire_date)\n" +
                "\tVALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt((String) playerData.get(0)));
            preparedStatement.setString(2, playerData.get(1).toString());
            preparedStatement.setString(3, playerData.get(1).toString());
            preparedStatement.setInt(4, Integer.parseInt((String) playerData.get(2)));
            preparedStatement.setString(5, playerData.get(3).toString());
            preparedStatement.setString(6, playerData.get(4).toString());
            preparedStatement.setString(7, playerData.get(5).toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getSQLState().equals("23505")) {
                JOptionPane.showMessageDialog(view.table, "Номер игрока должен быть уникальным");
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(view.table, "Данные заполнены некорректно");
        }
    }

    private void removePlayer (int playerNum) {
        String query = "DELETE FROM public.players WHERE player_id = ?;";

        try (PreparedStatement preparedStatement = DatabaseUtils.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, playerNum);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
