/**
 * View-класс панели статистики команды
 */

package panel.Team.TeamStats;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TeamStatsView {
    public JPanel panel = new JPanel();

    public TeamStatsView (HashMap<TeamStatsModel.TableColumns, Number> dataMap) {
        panel.add(initPanel(dataMap));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    }

    public void redraw (HashMap<TeamStatsModel.TableColumns, Number> dataMap) {
        panel.removeAll();

        panel.add(initPanel(dataMap));

        panel.revalidate();
        panel.repaint();
    }

    private JPanel initPanel (HashMap<TeamStatsModel.TableColumns, Number> dataMap) {
        ArrayList<JLabel> labels = new ArrayList<JLabel>();
        ArrayList<JLabel> valueLabels = new ArrayList<JLabel>();

        dataMap.forEach((tableColumns, number) -> {
            String title = tableColumns.getTitle();
            labels.add(new JLabel(title));
            valueLabels.add(new JLabel(number.toString()));
        });

        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        for (int i = 0; i < labels.size(); i++) {
            constraints.gridx = 0;
            constraints.gridy = i;
            newPanel.add(labels.get(i), constraints);

            constraints.gridx = 1;
            newPanel.add(valueLabels.get(i), constraints);
        }

        newPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "Статистика команды"
                )
        );

        return newPanel;
    }
}
