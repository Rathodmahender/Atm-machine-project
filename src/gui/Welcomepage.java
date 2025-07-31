package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Welcomepage extends JFrame {

    JTextField nameField;
    JPasswordField pinField;

    public Welcomepage() {
        setTitle("ATM Login");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        nameField = new JTextField();
        pinField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create New Account");
        JButton forgotBtn = new JButton("Forgot Password");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("PIN:"));
        add(pinField);
        add(loginBtn);
        add(createBtn);
        add(forgotBtn);

        loginBtn.addActionListener(e -> {
            String name = nameField.getText();
            String pin = new String(pinField.getPassword());

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/college", "root", "mysql");

                String query = "SELECT * FROM users WHERE name=? AND pin=?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, pin);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "✅ Login Successful!");
                    dispose();
                    new Dashboard(name).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid credentials.");
                }

                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        createBtn.addActionListener(e -> {
            dispose();
            new CreateAccount().setVisible(true);
        });

        forgotBtn.addActionListener(e -> {
            dispose();
            new ForgotPassword().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Welcomepage().setVisible(true);
        });
    }
}
