package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.CourseCoordinatorDashboardController;
import java.io.File;

public class CourseCoordinatorDashboardView extends JFrame {
    private JButton[] menuButtons;
    private String[] buttonLabels = {
        "Offer Courses", "Upload Scheme", "Edit Courses", "View Registrations", "View Progress", "Generate Report", "Change Prerequisites", "Logout"
    };
    private String[] iconFiles = {
        "/offercourses.png", "/uploadscheme.png", "/editcourses.png", "/viewregistrations.png", "/viewprogress.png", "/generatereport.png", "/changeprereq.png", "/logout.png"
    };
    private JPanel contentPanel;
    private int activeIndex = 0;
    private CourseCoordinatorDashboardController controller;

    public CourseCoordinatorDashboardView(CourseCoordinatorDashboardController controller) {
        this.controller = controller;
        setTitle("Course Coordinator Console");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Custom background panel with image and green overlay
        JPanel background = new JPanel(new BorderLayout()) {
            Image bg = new ImageIcon("Student course registration system/background.jpg").getImage();
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

        // HEADER BAR
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 160, 120));
        header.setPreferredSize(new Dimension(0, 80));

        // Left: Logo + Label
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
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
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(0, 0, size-1, size-1);
                g2.dispose();
            }
        };
        logoPanel.setPreferredSize(new Dimension(64, 64));
        logoPanel.setOpaque(false);
        leftPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        leftPanel.add(logoPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        JLabel consoleLabel = new JLabel("COURSE COORDINATOR CONSOLE");
        consoleLabel.setFont(new Font("Century Gothic", Font.BOLD, 24));
        consoleLabel.setForeground(Color.WHITE);
        leftPanel.add(consoleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        header.add(leftPanel, BorderLayout.WEST);

        // Center: Welcome
        JLabel welcome = new JLabel("Welcome : " + controller.getUsername(), SwingConstants.CENTER);
        welcome.setFont(new Font("Century Gothic", Font.BOLD, 20));
        welcome.setForeground(Color.WHITE);
        header.add(welcome, BorderLayout.CENTER);

        background.add(header, BorderLayout.NORTH);

        // MENU BAR
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 24, 24));
        menuButtons = new JButton[buttonLabels.length];

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton btn = new JButton(buttonLabels[i]);
            btn.setFont(new Font("Century Gothic", Font.BOLD, 14));
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            if (buttonLabels[i].equals("Logout")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "logout.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("Offer Courses")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "offercourse.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("Upload Scheme")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "uploadscheme.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("Edit Courses")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "edit.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("View Registrations")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "viewregistrations.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("View Progress")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "progress-report.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("Generate Report")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "viewreport.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else if (buttonLabels[i].equals("Change Prerequisites")) {
                btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "changeprereq.png").getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } else {
                btn.setIcon(new ImageIcon(new ImageIcon("Student course registration system" + iconFiles[i]).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            }
            btn.setPreferredSize(new Dimension(110, 90));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            btn.setBackground(Color.WHITE);
            btn.setOpaque(true);

            final int idx = i;
            btn.addActionListener(e -> {
                setActiveButton(idx);
                controller.navigateTo(idx);
            });

            menuButtons[i] = btn;
            menuPanel.add(btn);
        }
        setActiveButton(activeIndex);

        // Main content area (white panel)
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        contentPanel.setLayout(new BorderLayout());

        // Center panel to hold menu and content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(menuPanel, BorderLayout.NORTH);
        centerPanel.add(contentPanel, BorderLayout.CENTER);

        background.add(centerPanel, BorderLayout.CENTER);
    }

    private void setActiveButton(int idx) {
        for (int i = 0; i < menuButtons.length; i++) {
            if (i == idx) {
                menuButtons[i].setBackground(new Color(0, 160, 120));
                menuButtons[i].setForeground(Color.WHITE);
            } else {
                menuButtons[i].setBackground(Color.WHITE);
                menuButtons[i].setForeground(Color.BLACK);
            }
        }
        activeIndex = idx;
    }

    private Image loadImage(String filename) {
        String basePath = System.getProperty("user.dir") + File.separator;
        return new ImageIcon(basePath + filename).getImage();
    }
} 