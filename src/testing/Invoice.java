package testing;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class Invoice extends JFrame {
    private String institutionName;
    private Map<String, Integer> basketMap;
    private Map<String, Integer> priceMap;

    public Invoice(String institutionName, Map<String, Integer> basketMap, Map<String, Integer> priceMap) {
        this.institutionName = institutionName;
        this.basketMap = basketMap;
        this.priceMap = priceMap;

        setTitle("Invoice - Food");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(173, 216, 230)); // Light Blue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // ✅ Instruction label at top
        JLabel instructions = new JLabel("Press the invoice to proceed for payment");
        instructions.setFont(new Font("Arial", Font.BOLD, 20));
        instructions.setForeground(Color.BLACK);
        instructions.setHorizontalAlignment(JLabel.CENTER);
        instructions.setBorder(new EmptyBorder(20, 0, 30, 0));
        add(instructions, BorderLayout.NORTH);

        // ✅ The entire invoice card is a JButton
        JButton invoiceButton = new JButton();
        invoiceButton.setLayout(new BoxLayout(invoiceButton, BoxLayout.Y_AXIS));
        invoiceButton.setPreferredSize(new Dimension(500, 600));
        invoiceButton.setBackground(Color.WHITE);
        invoiceButton.setFocusPainted(false);
        invoiceButton.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        invoiceButton.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel title = new JLabel("Invoice - Food");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("University Management System");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel institution = new JLabel("Institution: " + institutionName);
        institution.setFont(new Font("Arial", Font.BOLD, 16));
        institution.setAlignmentX(Component.CENTER_ALIGNMENT);
        institution.setBorder(new EmptyBorder(10, 0, 10, 0));

        invoiceButton.add(title);
        invoiceButton.add(subtitle);
        invoiceButton.add(institution);
        invoiceButton.add(Box.createRigidArea(new Dimension(0, 10)));

        int totalPrice = 0;

        for (Map.Entry<String, Integer> entry : basketMap.entrySet()) {
            String item = entry.getKey();
            int qty = entry.getValue();
            int price = priceMap.getOrDefault(item, 0);
            totalPrice += qty * price;

            JLabel itemLabel = new JLabel("• " + item + " x" + qty + " - £" + (price * qty) + " (£" + price + " each)");
            itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            itemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            invoiceButton.add(itemLabel);
            invoiceButton.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        invoiceButton.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel total = new JLabel("Total Price: £" + totalPrice);
        total.setFont(new Font("Arial", Font.BOLD, 18));
        total.setAlignmentX(Component.CENTER_ALIGNMENT);
        invoiceButton.add(total);

        // ✅ On button click → open PaymentPage
        invoiceButton.addActionListener(e -> {
            dispose(); // close invoice page
            PaymentPage.openPayment(institutionName, basketMap, priceMap);
        });

        mainPanel.add(invoiceButton, gbc);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void openInvoice(String institution, Map<String, Integer> basketMap, Map<String, Integer> priceMap) {
        SwingUtilities.invokeLater(() -> new Invoice(institution, basketMap, priceMap));
    }

    public static void main(String[] args) {
        Map<String, Integer> basket = Map.of("Burger", 2, "Pizza", 1);
        Map<String, Integer> prices = Map.of("Burger", 12, "Pizza", 15);
        openInvoice("Brunel University", basket, prices);
    }
}
