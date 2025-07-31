package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CreateAccount extends JFrame {
    private JTextField nameField, balanceField;
    private JPasswordField pinField;

    public CreateAccount() {
        setTitle("Create New Account");
        setSize(350, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        nameField = new JTextField();
        pinField = new JPasswordField();  // improved from JTextField
        balanceField = new JTextField();

        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("New PIN:"));
        add(pinField);
        add(new JLabel("Initial Balance:"));
        add(balanceField);
        add(createBtn);
        add(backBtn);

        createBtn.addActionListener(e -> createAccount());
        backBtn.addActionListener(e -> {
            dispose();
            new Welcomepage().setVisible(true);
        });
    }

    private void createAccount() {
        String name = nameField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();
        String balanceStr = balanceField.getText().trim();

        if (name.isEmpty() || pin.isEmpty() || balanceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
            if (balance < 0) {
                JOptionPane.showMessageDialog(this, "Balance cannot be negative!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance amount.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/college", "root", "mysql")) {

            String accountNo = "ACC" + System.currentTimeMillis();

            String sql = "INSERT INTO users (name, account_no, pin, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, accountNo);
            pst.setString(3, pin);
            pst.setDouble(4, balance);

            int inserted = pst.executeUpdate();
            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "✅ Account created!\nAccount No: " + accountNo);
                dispose();
                new Welcomepage().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to create account.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
