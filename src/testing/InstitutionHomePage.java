package testing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class InstitutionHomePage extends JFrame {
    private JComboBox<String> searchDropdown;
    private JPanel contentPanel;
    private final int cardWidth = 300;
    private final int cardHeight = 160;
    private final int gapX = 50;
    private final int gapY = 40;
    private ArrayList<String> allInstitutions = new ArrayList<>();

    public InstitutionHomePage() {
        setTitle("Institution Explorer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadAllInstitutions();

        // Top search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        searchPanel.setPreferredSize(new Dimension(getWidth(), 80));
        searchPanel.setBackground(new Color(240, 240, 240));

        searchDropdown = new JComboBox<>(allInstitutions.toArray(new String[0]));
        searchDropdown.setEditable(true);
        searchDropdown.setPreferredSize(new Dimension(600, 35));
        searchPanel.add(searchDropdown);

        JButton searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchPanel.add(searchBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        searchPanel.add(refreshBtn);

        searchBtn.addActionListener(e -> performSearch());
        refreshBtn.addActionListener(e -> refreshPage());

        add(searchPanel, BorderLayout.NORTH);

        // Background Panel
        BackgroundPanel background = new BackgroundPanel();
        background.setLayout(null); // allow manual positioning
        contentPanel = background;

        add(contentPanel, BorderLayout.CENTER);

        displayRandomInstitutions();

        setVisible(true);
    }

    private void loadAllInstitutions() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT LEGAL_NAME FROM INSTITUTION ORDER BY LEGAL_NAME";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                allInstitutions.add(rs.getString("LEGAL_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        String keyword = ((String) searchDropdown.getEditor().getItem()).trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clearCards();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT LEGAL_NAME FROM INSTITUTION WHERE LEGAL_NAME LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            addInstitutionCards(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void refreshPage() {
        clearCards();
        displayRandomInstitutions();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void displayRandomInstitutions() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT LEGAL_NAME FROM INSTITUTION ORDER BY RANDOM() LIMIT 12";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            addInstitutionCards(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addInstitutionCards(ResultSet rs) throws SQLException {
        int x = 60, y = 60, count = 0;
        while (rs.next()) {
            String name = rs.getString("LEGAL_NAME");
            JButton btn = new JButton("<html><center>" + name + "</center></html>");
            btn.setBounds(x, y, cardWidth, cardHeight);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(255, 255, 255, 230));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            btn.setBorder(BorderFactory.createEmptyBorder());

            // Example: Replace with real navigation
            btn.addActionListener(e -> FoodsPage.openWithInstitution(name));

            contentPanel.add(btn);

            x += cardWidth + gapX;
            count++;
            if (count % 4 == 0) {
                x = 60;
                y += cardHeight + gapY;
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this, "No results found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearCards() {
        contentPanel.removeAll();
    }

    // ✅ Background Panel with random Islamic image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                String[] images = {
                    "resources/background1.jpg",
                   
                };

                int randomIndex = (int) (Math.random() * images.length);
                String selected = images[randomIndex];
                backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("background1.jpg")).getImage();

                if (backgroundImage == null) {
                    System.out.println("❌ Could not load: " + selected);
                } else {
                    System.out.println("✅ Loaded background: " + selected);
                }

            } catch (Exception e) {
                System.out.println("❌ Failed to load background: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.RED);
                g.drawString("BACKGROUND NOT FOUND", 100, 100);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InstitutionHomePage::new);
    }
}
