/**
 * Класс для отображения tabbed-панели упарвления командой
 */


package panel.Team;

import panel.AbstractController;
import panel.Team.PlayersStats.PlayersStatsController;
import panel.Team.TeamTable.TeamTableController;
import panel.Team.TeamStats.TeamStatsController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Класс для отображения данных о команде
 */
public class Team extends JPanel {
    AbstractController[] controllers = {
            new TeamTableController(),
            new PlayersStatsController(true, true),
            new TeamStatsController()
    };

    public Team () {
        String[] tabTexts = {
                "Данные игроков",
                "Статистика игроков",
                "Статистика команды"
        };

        Component[] components = getViewComponents();

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        for (int i = 0; i < tabTexts.length; i++) {
            tabbedPane.addTab(tabTexts[i], components[i]);
        }

        setLayout(new GridLayout());
        add(tabbedPane);
    }

    private Component[] getViewComponents() {
        int controllersLength = controllers.length;

        Component[] components = new Component[controllersLength];

        for (int i = 0; i < controllersLength; i++) {
            components[i] = controllers[i].mainViewComponent();
        }

        return components;
    }
}
