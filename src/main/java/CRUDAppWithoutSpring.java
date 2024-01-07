import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CRUDAppWithoutSpring {

    private static final String DB_URL = "jdbc:mysql://monorail.proxy.rlwy.net:46217/railway";
    private static final String DB_USER = "root";

    //    mysql://root:A54H4h3eH3h-B4Bea31e65hgh6Ahef4a@monorail.proxy.rlwy.net:46217/railway
    private static final String DB_PASSWORD = "A54H4h3eH3h-B4Bea31e65hgh6Ahef4a";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Adaugă utilizator");
                System.out.println("2. Afișează utilizatori");
                System.out.println("3. Actualizează utilizator");
                System.out.println("4. Șterge utilizator");
                System.out.println("5. Ieșire");

                System.out.print("Alege o opțiune: ");
                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        System.out.print("Introdu numele utilizatorului: ");
                        String name = scanner.next();
                        addUser(connection, name);
                        break;
                    case 2:
                        displayUsers(connection);
                        break;
                    case 3:
                        System.out.print("Introdu numărul utilizatorului pentru actualizare: ");
                        int userId = scanner.nextInt();
                        System.out.print("Introdu noul nume: ");
                        String updatedName = scanner.next();
                        updateUser(connection, userId, updatedName);
                        break;
                    case 4:
                        System.out.print("Introdu numărul utilizatorului pentru ștergere: ");
                        int deleteUserId = scanner.nextInt();
                        deleteUser(connection, deleteUserId);
                        break;
                    case 5:
                        System.out.println("Aplicația se închide.");
                        System.exit(0);
                    default:
                        System.out.println("Opțiune invalidă.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(Connection connection, String name) throws SQLException {
        String insertQuery = "INSERT INTO Users (name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilizator adăugat cu succes.");
            } else {
                System.out.println("Eroare la adăugarea utilizatorului.");
            }
        }
    }

    public static void displayUsers(Connection connection) throws SQLException {
        JTable userTable = new JTable();
        String selectQuery = "SELECT * FROM Users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Nume");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                tableModel.addRow(new Object[]{id, name});
            }

            userTable.setModel(tableModel);
        }
    }

    public static void updateUser(Connection connection, int userId, String updatedName) throws SQLException {
        String updateQuery = "UPDATE Users SET name = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, updatedName);
            preparedStatement.setInt(2, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilizator actualizat cu succes.");
            } else {
                System.out.println("Eroare la actualizarea utilizatorului.");
            }
        }
    }

    public static void deleteUser(Connection connection, int userId) throws SQLException {
        String deleteQuery = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilizator șters cu succes.");
            } else {
                System.out.println("Eroare la ștergerea utilizatorului.");
            }
        }
    }
}