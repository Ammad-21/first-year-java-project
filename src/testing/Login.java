
package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class Login extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel animatedTextLabel;
    private Timer typingTimer;
    private int charIndex = 0;

    private String introText = "You're not just logging in – you're stepping into a space where comfort meets craving.";
    public Login() {
        setTitle("FoodDash Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
        setLayout(null);
   

        // ✅ Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/background_clean.png"));
        Image bgImage = bgIcon.getImage().getScaledInstance(
            Toolkit.getDefaultToolkit().getScreenSize().width,
            Toolkit.getDefaultToolkit().getScreenSize().height,
            Image.SCALE_SMOOTH
        );
        JLabel bgLabel = new JLabel(new ImageIcon(bgImage));
        bgLabel.setBounds(0, 0, getWidth(), getHeight());
        setContentPane(bgLabel);
        setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = 520;
        int panelHeight = 330;

        // ✅ Login panel with rounded corners
        JPanel loginPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 220)); // semi-transparent black
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setBounds(
            (screenSize.width / 2) + 100,
            (screenSize.height / 2) - 180,
            panelWidth,
            panelHeight
        );
        loginPanel.setBorder(new LineBorder(Color.WHITE, 2, true));
        add(loginPanel);

        // ✅ Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(30, 30, 100, 25);
        loginPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(130, 30, 340, 30);
        loginPanel.add(emailField);

        // ✅ Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(30, 90, 100, 25);
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 90, 340, 30);
        loginPanel.add(passwordField);

        // ✅ Login button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Verdana", Font.BOLD, 14));
        loginButton.setBackground(new Color(255, 69, 0)); // Orange-red
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(50, 160, 180, 45);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginPanel.add(loginButton);

        // ✅ Signup button
        JButton signupButton = new JButton("SIGN UP");
        signupButton.setFont(new Font("Verdana", Font.BOLD, 14));
        signupButton.setBackground(new Color(65, 105, 225)); // Royal blue
        signupButton.setForeground(Color.WHITE);
        signupButton.setBounds(280, 160, 180, 45);
        signupButton.setOpaque(true);
        signupButton.setBorderPainted(false);
        loginPanel.add(signupButton);

        // ✅ Animated text (on screen, above login box)
     // Position text just below the login box
        animatedTextLabel = new JLabel("");
        animatedTextLabel.setFont(new Font("Georgia", Font.ITALIC, 20));
        animatedTextLabel.setForeground(Color.WHITE);
        animatedTextLabel.setBounds(100, screenSize.height - 180, screenSize.width - 200, 40);
        animatedTextLabel.setOpaque(false);
        add(animatedTextLabel);

        animateIntroText();


        // ✅ Button actions
        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> signup());

        setVisible(true);
    }

    private void animateIntroText() {
        animatedTextLabel.setText("");
        charIndex = 0;
        typingTimer = new Timer(30, e -> {
            if (charIndex < introText.length()) {
                animatedTextLabel.setText(animatedTextLabel.getText() + introText.charAt(charIndex));
                charIndex++;
            } else {
                typingTimer.stop();
            }
        });
        typingTimer.start();
    }

    private void login() {
        String username = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:UMS.db")) {
            String sql = "SELECT * FROM USERS WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose(); // closes the login window
                new InstitutionHomePage(); // opens the institution page
            }

            else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void signup() {
        String username = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both fields.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:UMS.db")) {
            String sql = "INSERT INTO USERS (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Signup successful!");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Ensure table exists
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:UMS.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (id INTEGER PRIMARY KEY, username TEXT UNIQUE, password TEXT)");
            stmt.execute("INSERT OR IGNORE INTO USERS (username, password) VALUES ('admin', '12345')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(Login::new);
        
        
        	
        	
        
        
        
    }
}
