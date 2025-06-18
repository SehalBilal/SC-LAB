package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.LoginController;
import model.UserModel;
import java.io.File;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private LoginController controller;
    private JComboBox<String> roleComboBox;
    private JLabel userLabel;

    private Image loadImage(String filename) {
        String basePath = System.getProperty("user.dir") + File.separator;
        return new ImageIcon(basePath + filename).getImage();
    }

    public LoginView(LoginController controller) {
        this.controller = controller;
        setTitle("QAU Course Registration System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        // Custom background panel with image and green overlay
        JPanel background = new JPanel(new GridBagLayout()) {
            Image bg = loadImage("background.jpg");
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(34, 139, 34, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        setContentPane(background);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(10, 0, 10, 0);

        // Logo image (round)
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = loadImage("logo.png");
                int size = Math.min(getWidth(), getHeight());
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
                g2.drawImage(img, 0, 0, size, size, this);
                g2.setClip(null);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4));
                g2.drawOval(0, 0, size-1, size-1);
                g2.dispose();
            }
        };
        logoPanel.setPreferredSize(new Dimension(100, 100));
        logoPanel.setOpaque(false);
        background.add(logoPanel, gbc);

        // Title
        gbc.gridy++;
        JLabel title = new JLabel("QAU COURSE REGISTRATION SYSTEM");
        title.setFont(new Font("Century Gothic", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        background.add(title, gbc);

        // Login panel
        gbc.gridy++;
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 40, 40);
                g2.setColor(new Color(0, 128, 0, 180));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 40, 40);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        panel.setMaximumSize(new Dimension(400, 400));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center the login label
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 22));
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loginLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add role selection dropdown
        gbc.gridy++;
        JLabel roleLabel = new JLabel("Select Role");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(roleLabel);
        roleComboBox = new JComboBox<>(new String[]{"Student", "Course Coordinator", "Timetable Coordinator"});
        roleComboBox.setMaximumSize(new Dimension(300, 30));
        roleComboBox.setPreferredSize(new Dimension(300, 30));
        roleComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(roleComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Username label (make it a field for dynamic update)
        userLabel = new JLabel("Student Username");
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 30));
        usernameField.setPreferredSize(new Dimension(300, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameField);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setPreferredSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(passwordField);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JCheckBox keepLogged = new JCheckBox("Keep me logged in");
        keepLogged.setOpaque(false);
        keepLogged.setForeground(Color.WHITE);
        JLabel forgotPass = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPass.setForeground(Color.WHITE);
        forgotPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        optionsPanel.add(keepLogged);
        optionsPanel.add(Box.createHorizontalGlue());
        optionsPanel.add(forgotPass);
        panel.add(optionsPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(120, 200, 120));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 20));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        loginBtn.setFocusPainted(false);
        loginBtn.setMaximumSize(new Dimension(300, 48));
        loginBtn.setPreferredSize(new Dimension(300, 48));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Update label on role change
        roleComboBox.addActionListener(e -> {
            String role = (String) roleComboBox.getSelectedItem();
            if (role.equals("Student")) userLabel.setText("Student Username");
            else if (role.equals("Course Coordinator")) userLabel.setText("Course Coordinator Name");
            else userLabel.setText("Timetable Coordinator Name");
        });

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            if (controller.validateLogin(username, password, role)) {
                controller.showDashboard(username, role);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        panel.add(loginBtn);
        background.add(panel, gbc);
    }
} 