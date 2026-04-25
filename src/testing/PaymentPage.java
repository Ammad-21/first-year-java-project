package testing;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Map;

public class PaymentPage extends JFrame {
    private String institutionName;
    private Map<String, Integer> basketMap;
    private Map<String, Integer> priceMap;

    public PaymentPage(String institutionName, Map<String, Integer> basketMap, Map<String, Integer> priceMap) {
        this.institutionName = institutionName;
        this.basketMap = basketMap;
        this.priceMap = priceMap;

        setTitle("Payment Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Complete Your Payment", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 26));
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(new Color(240, 248, 255));
        add(mainPanel, BorderLayout.CENTER);

        // Left side: Order Summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1, true), new EmptyBorder(20, 20, 20, 20)));
        summaryPanel.setPreferredSize(new Dimension(400, 300));

        summaryPanel.add(new JLabel("<html><h3>Order Summary</h3></html>"));
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        int total = 0;
        for (Map.Entry<String, Integer> entry : basketMap.entrySet()) {
            String food = entry.getKey();
            int qty = entry.getValue();
            int price = priceMap.getOrDefault(food, 0);
            total += qty * price;

            JLabel item = new JLabel("• " + food + " x" + qty + " (£" + price + ")");
            item.setFont(new Font("Arial", Font.PLAIN, 14));
            summaryPanel.add(item);
        }

        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel totalLabel = new JLabel("Total: £" + total);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryPanel.add(totalLabel);

        mainPanel.add(summaryPanel);

        // Right side: Payment Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        formPanel.setOpaque(false);

        JTextField nameField = new JTextField();
        JTextField cardField = new JTextField();
        JTextField expiryField = new JTextField();
        JTextField cvvField = new JTextField();

        formPanel.add(new JLabel("Cardholder Name:"));
        nameField.setMaximumSize(new Dimension(300, 30));
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(new JLabel("Card Number:"));
        cardField.setMaximumSize(new Dimension(300, 30));
        formPanel.add(cardField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(new JLabel("Expiry Date (MM/YY):"));
        expiryField.setMaximumSize(new Dimension(150, 30));
        formPanel.add(expiryField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(new JLabel("CVV:"));
        cvvField.setMaximumSize(new Dimension(100, 30));
        formPanel.add(cvvField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setOpaque(false);
        try {
            logoPanel.add(new JLabel(new ImageIcon(getClass().getResource("/visa.jpg"))));
        } catch (Exception e) {
            logoPanel.add(new JLabel("Visa logo not found"));
        }
        formPanel.add(logoPanel);

        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Submit Button
        JButton submitButton = new JButton("Submit Payment");
        submitButton.setPreferredSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(submitButton);

        mainPanel.add(formPanel);

        // Submit Action
        int finalTotal = total;
        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String card = cardField.getText().trim();
            String expiry = expiryField.getText().trim();
            String cvv = cvvField.getText().trim();

            if (name.isEmpty() || card.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                insertPaymentToDatabase(institutionName, name, card, expiry, cvv, basketMap, finalTotal);
                JOptionPane.showMessageDialog(this, "Payment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setVisible(true);
    }

    // ✅ Insert Payment to UMS.db
    private void insertPaymentToDatabase(String institutionName, String cardHolderName, String cardNumber, String expiryDate, String cvv, Map<String, Integer> basketMap, int totalAmount) {
        String url = "jdbc:sqlite:UMS.db";  // ✅ Your uploaded DB

        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, Integer> entry : basketMap.entrySet()) {
            summary.append(entry.getKey()).append(" x").append(entry.getValue()).append(", ");
        }
        String basketSummary = summary.toString().replaceAll(", $", "");

        String sql = "INSERT INTO Payments(institutionName, cardHolderName, cardNumber, expiryDate, cvv, basketSummary, totalAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, institutionName);
            pstmt.setString(2, cardHolderName);
            pstmt.setString(3, cardNumber);
            pstmt.setString(4, expiryDate);
            pstmt.setString(5, cvv);
            pstmt.setString(6, basketSummary);
            pstmt.setInt(7, totalAmount);

            pstmt.executeUpdate();
            System.out.println("✅ Payment saved to UMS.db");
        } catch (SQLException e) {
            System.err.println("❌ Error inserting payment: " + e.getMessage());
        }
    }

    // ✅ Ensure Payments Table Exists
    public static void createPaymentsTableIfNeeded() {
        String url = "jdbc:sqlite:UMS.db";
        String sql = "CREATE TABLE IF NOT EXISTS Payments (" +
                "paymentID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "institutionName TEXT NOT NULL," +
                "cardHolderName TEXT NOT NULL," +
                "cardNumber TEXT NOT NULL," +
                "expiryDate TEXT NOT NULL," +
                "cvv TEXT NOT NULL," +
                "basketSummary TEXT," +
                "totalAmount INTEGER NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Payments table ensured.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to create Payments table: " + e.getMessage());
        }
    }

    public static void openPayment(String institution, Map<String, Integer> basketMap, Map<String, Integer> priceMap) {
        SwingUtilities.invokeLater(() -> new PaymentPage(institution, basketMap, priceMap));
    }

    public static void main(String[] args) {
        createPaymentsTableIfNeeded();

        Map<String, Integer> basket = Map.of("Pizza", 1, "Fries", 2);
        Map<String, Integer> prices = Map.of("Pizza", 15, "Fries", 5);

        openPayment("Brunel University", basket, prices);
    }
}
