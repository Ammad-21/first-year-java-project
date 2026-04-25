package testing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FoodsPage extends JFrame {
    private ImagePanel leftPanel, rightPanel;
    private JPanel basketPanel;
    private JLabel institutionLabel, foodDetailLabel, totalPriceLabel;
    private String selectedInstitution;

    private final int MAX_BASKET_SIZE = 10;
    private Map<String, Integer> basketMap = new LinkedHashMap<>();
    private Map<String, Integer> priceMap = new HashMap<>();

    public FoodsPage(String institutionName) {
        this.selectedInstitution = institutionName;
        setTitle("Foods Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ✅ Top Bar with Institution Name + Generate Invoice Button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));
        topPanel.setBackground(new Color(180, 220, 240));

        institutionLabel = new JLabel("  Selected Institution: " + selectedInstitution);
        institutionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        institutionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton generateInvoiceBtn = new JButton("Generate Invoice");
        generateInvoiceBtn.setFont(new Font("Arial", Font.BOLD, 14));
        generateInvoiceBtn.setBackground(new Color(0, 123, 255));
        generateInvoiceBtn.setForeground(Color.WHITE);
        generateInvoiceBtn.setFocusPainted(false);
        generateInvoiceBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        generateInvoiceBtn.addActionListener(e -> {
            if (basketMap.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Your basket is empty. Please add items first.",
                        "Empty Basket", JOptionPane.WARNING_MESSAGE);
            } else {
                Invoice.openInvoice(selectedInstitution, basketMap, priceMap);
            }
        });

        topPanel.add(institutionLabel, BorderLayout.WEST);
        topPanel.add(generateInvoiceBtn, BorderLayout.EAST); // ✅ added here

        add(topPanel, BorderLayout.NORTH);

        // Left Panel with red background
        leftPanel = new ImagePanel("/redbackground.png");
        leftPanel.setLayout(new GridLayout(4, 3, 20, 20));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        add(leftScrollPane, BorderLayout.CENTER);

        // Right Panel with gold background
        rightPanel = new ImagePanel("/goldbackground.png");
        rightPanel.setPreferredSize(new Dimension(500, getHeight()));
        rightPanel.setLayout(new BorderLayout());

        foodDetailLabel = new JLabel("<html><h3>Food Info</h3><p>Select a food to see its description.</p></html>");
        foodDetailLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        rightPanel.add(foodDetailLabel, BorderLayout.NORTH);

        basketPanel = new JPanel();
        basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));
        basketPanel.setOpaque(false);
        JScrollPane basketScrollPane = new JScrollPane(basketPanel);
        basketScrollPane.setBorder(BorderFactory.createTitledBorder("Basket (0/10 Items)"));
        rightPanel.add(basketScrollPane, BorderLayout.CENTER);

        totalPriceLabel = new JLabel("Total Price: £0");
        totalPriceLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(totalPriceLabel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        displayRandomFoodsForInstitution();
        setVisible(true);
    }

    private void updateBasketPanel() {
        basketPanel.removeAll();

        int totalItems = 0;
        int totalPrice = 0;

        for (Map.Entry<String, Integer> entry : basketMap.entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();
            int itemPrice = priceMap.getOrDefault(item, 0);
            totalItems += quantity;
            totalPrice += itemPrice * quantity;

            JLabel itemLabel = new JLabel("• " + item + " x" + quantity + " (£" + itemPrice + " each)");
            itemLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            basketPanel.add(itemLabel);
        }

        ((TitledBorder) ((JScrollPane) rightPanel.getComponent(1)).getBorder())
                .setTitle("Basket (" + totalItems + "/10 Items)");
        totalPriceLabel.setText("Total Price: £" + totalPrice);

        basketPanel.revalidate();
        basketPanel.repaint();
    }

    private void displayRandomFoodsForInstitution() {
        leftPanel.removeAll();
        basketMap.clear();
        priceMap.clear();
        updateBasketPanel();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM FOODS ORDER BY RANDOM() LIMIT 12";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String foodName = rs.getString(1);
                String foodDesc = rs.getString(2);
                int price = 10 + new Random().nextInt(41);
                priceMap.put(foodName, price);

                String expiry = LocalDate.now().plusDays(new Random().nextInt(3) + 1)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

                final String displayText = "<html><h3>" + foodName + "</h3>" +
                        "<p><b>Description:</b> " + foodDesc + "</p>" +
                        "<p><b>Price:</b> £" + price + "</p>" +
                        "<p><b>Expiry:</b> " + expiry + "</p></html>";

                JButton foodBtn = new JButton("<html><center>" + foodName + "<br>£" + price + "</center></html>");
                foodBtn.setFont(new Font("Arial", Font.BOLD, 16));
                foodBtn.setFocusPainted(false);
                foodBtn.setOpaque(true);
                foodBtn.setBackground(Color.WHITE);

                foodBtn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        foodDetailLabel.setText(displayText);

                        if (e.getClickCount() == 2) {
                            basketMap.remove(foodName);
                        } else if (e.getClickCount() == 1) {
                            int totalItems = basketMap.values().stream().mapToInt(i -> i).sum();
                            if (totalItems < MAX_BASKET_SIZE) {
                                basketMap.put(foodName, basketMap.getOrDefault(foodName, 0) + 1);
                            } else {
                                JOptionPane.showMessageDialog(FoodsPage.this,
                                        "You can only add 10 items to the basket.",
                                        "Basket Full", JOptionPane.WARNING_MESSAGE);
                            }
                        }

                        updateBasketPanel();
                    }
                });

                leftPanel.add(foodBtn);
            }

            leftPanel.revalidate();
            leftPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void openWithInstitution(String institutionName) {
        SwingUtilities.invokeLater(() -> new FoodsPage(institutionName));
    }

    public static void main(String[] args) {
        openWithInstitution("Demo Institution");
    }
}

// ✅ ImagePanel helper for background images
class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        java.net.URL location = getClass().getResource(imagePath);
        if (location != null) {
            backgroundImage = new ImageIcon(location).getImage();
        } else {
            System.err.println("ERROR: Image not found -> " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
