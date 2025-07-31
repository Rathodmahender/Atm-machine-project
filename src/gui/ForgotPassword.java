package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ForgotPassword extends JFrame {
    JTextField nameField, newPinField;

    public ForgotPassword() {
        setTitle("Reset PIN");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        nameField = new JTextField();
        newPinField = new JTextField();

        JButton resetBtn = new JButton("Reset");
        JButton backBtn = new JButton("Back");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("New PIN:"));
        add(newPinField);
        add(resetBtn);
        add(backBtn);

        resetBtn.addActionListener(e -> resetPIN());
        backBtn.addActionListener(e -> {
            dispose();
            new Welcomepage().setVisible(true);  // Use your actual login class name
        });
    }

    void resetPIN() {
        String name = nameField.getText();
        String newPin = newPinField.getText();

        if (name.isEmpty() || newPin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.");
            return;
        }

        // Optional: Validate new PIN (e.g., numeric)
        if (!newPin.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "PIN must be numeric.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load driver

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/college", "root", "mysql")) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE users SET pin = ? WHERE name = ?");
                stmt.setString(1, newPin);
                stmt.setString(2, name);

                int updated = stmt.executeUpdate();

                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "PIN Reset Successful!");
                    dispose();
                    new Welcomepage().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "User not found!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
