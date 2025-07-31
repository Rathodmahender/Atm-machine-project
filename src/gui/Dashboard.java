package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {
    private String userName;
    private JLabel balanceLabel;

    public Dashboard(String name) {
        this.userName = name;
        setTitle("ATM Dashboard - " + name);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        balanceLabel = new JLabel("Loading balance...");
        JButton checkBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton logoutBtn = new JButton("Logout");

        add(balanceLabel);
        add(checkBtn);
        add(depositBtn);
        add(withdrawBtn);
        add(logoutBtn);

        //checkBalance(); // show initial balance

        checkBtn.addActionListener(e -> checkBalance());

        depositBtn.addActionListener(e -> {
            String amount = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
            if (amount != null && !amount.trim().isEmpty()) {
                try {
                    double amt = Double.parseDouble(amount.trim());
                    if (amt <= 0) {
                        JOptionPane.showMessageDialog(this, "Please enter a positive amount.");
                        return;
                    }
                    updateBalance(amt, true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount entered.");
                }
            }
        });

        withdrawBtn.addActionListener(e -> {
            String amount = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
            if (amount != null && !amount.trim().isEmpty()) {
                try {
                    double amt = Double.parseDouble(amount.trim());
                    if (amt <= 0) {
                        JOptionPane.showMessageDialog(this, "Please enter a positive amount.");
                        return;
                    }
                    updateBalance(amt, false);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount entered.");
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new Welcomepage().setVisible(true);
        });
    }

    private void checkBalance() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/college", "root", "mysql")) {

            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE name = ?");
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceLabel.setText("💰 Balance: ₹ " + balance);
            } else {
                balanceLabel.setText("User not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            balanceLabel.setText("Error fetching balance.");
        }
    }

    private void updateBalance(double amount, boolean isDeposit) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/college", "root", "mysql");
            conn.setAutoCommit(false);

            PreparedStatement selectStmt = conn.prepareStatement("SELECT balance FROM users WHERE name = ?");
            selectStmt.setString(1, userName);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                double newBalance = isDeposit ? currentBalance + amount : currentBalance - amount;

                if (newBalance < 0) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!");
                    return;
                }

                PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET balance = ? WHERE name = ?");
                updateStmt.setDouble(1, newBalance);
                updateStmt.setString(2, userName);
                updateStmt.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Transaction Successful!");
                checkBalance();
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Transaction failed: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
    }
}

