import panel.Calendar.Calendar;
import panel.Team.Team;
import utils.DatabaseUtils;
import utils.TeamsUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Основной класс отображения приложения
 */
public class MainWindow extends JFrame {
    private static boolean hasError = false;

    public MainWindow() {
        setTitle("Администрирование команды");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Инициация таб-панели
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        String[] buttonTexts = {
                "Команда",
                "Календарь"
        };

        JPanel[] panels = {
                new Team(),
                new Calendar(this)
        };

        // Добавление табов в панель
        for (int i = 0; i < buttonTexts.length; i++) {
            String buttonText = buttonTexts[i];
            JPanel panel = panels[i];

            tabbedPane.addTab(buttonText, panel);
        }

        getContentPane().setLayout(new GridLayout());
        getContentPane().add(tabbedPane);
    }

    public static void main(String[] args) {
        // Создаем пул потоков.
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Создаем семафоры для синхронизации потоков.
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);

        // Поток 1
        executor.submit(() -> {
            try {
                DatabaseUtils.connect("postgres", "postgres");
            } catch (SQLException e) {
                hasError = true;

                System.out.println("Ошибка подключения к БД");
            }

            // Разрешаем выполнение потока 2.
            semaphore1.release();
        });

        // Поток 2
        executor.submit(() -> {
            try {
                // Ждем, пока выполнится поток 1.
                semaphore1.acquire();

                System.out.println("Загрузка данных");

                try {
                    TeamsUtils.init();
                } catch (Exception e) {
                    hasError = true;

                    System.out.println("Ошибка загрузки данных");
                }

                // Разрешаем выполнение потока 3.
                semaphore2.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Поток 3
        executor.submit(() -> {
            try {
                // Ждем, пока выполнится поток 2.
                semaphore2.acquire();

                System.out.println("Инициация окна");

                SwingUtilities.invokeLater(() -> {
                    if (!hasError) {
                        MainWindow window = new MainWindow();
                        window.setVisible(true);
                    } else {
                        System.out.println("При загрузке приложения возникла ошибка");

                        System.exit(0);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Завершаем работу пула потоков после завершения задач.
        executor.shutdown();
    }
}