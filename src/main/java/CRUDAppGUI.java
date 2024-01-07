import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRUDAppGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://monorail.proxy.rlwy.net:46217/railway";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "A54H4h3eH3h-B4Bea31e65hgh6Ahef4a";

    private Connection connection;
    private JTextField nameTextField;
    private JTable userTable;

    public CRUDAppGUI() {
        initialize();
        connectToDatabase();
        displayUsers(); // Afișează inițial toți utilizatorii
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CRUD App with GUI");
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        nameTextField = new JTextField(20);
        JButton addButton = new JButton("Adaugă");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        inputPanel.add(new JLabel("Nume: "));
        inputPanel.add(nameTextField);
        inputPanel.add(addButton);

        userTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(userTable);

        JButton viewButton = new JButton("Vizualizează");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayUsers(); // Afișează utilizatorii la apăsarea butonului
            }
        });

        JButton updateButton = new JButton("Actualizează");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });

        JButton deleteButton = new JButton("Șterge");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUser() {
        String name = nameTextField.getText();
        if (!name.isEmpty()) {
            try {
                CRUDAppWithoutSpring.addUser(connection, name);
                displayUsers();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Eroare la adăugarea utilizatorului.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Introduceți un nume valid.");
        }
    }

    private void displayUsers() {
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Eroare la afișarea utilizatorilor.");
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int userId = (int) userTable.getValueAt(selectedRow, 0);
                String updatedName = JOptionPane.showInputDialog(this, "Introduceți noul nume:");
                if (updatedName != null && !updatedName.isEmpty()) {
                    CRUDAppWithoutSpring.updateUser(connection, userId, updatedName);
                    displayUsers();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Eroare la actualizarea utilizatorului.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selectați un utilizator pentru actualizare.");
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int userId = (int) userTable.getValueAt(selectedRow, 0);
                int option = JOptionPane.showConfirmDialog(this, "Sigur doriți să ștergeți acest utilizator?");
                if (option == JOptionPane.YES_OPTION) {
                    CRUDAppWithoutSpring.deleteUser(connection, userId);
                    displayUsers();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Eroare la ștergerea utilizatorului.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selectați un utilizator pentru ștergere.");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CRUDAppGUI();
            }
        });
    }
}
