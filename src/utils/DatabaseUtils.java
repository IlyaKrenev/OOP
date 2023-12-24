package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {
    public static Connection connection;

    public static void connect (String user, String password) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/kurs";

        connection = DriverManager.getConnection(url, user, password);

        System.out.println("Успешное подключение к базе данных");
    }

    public static void disconnect () {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
