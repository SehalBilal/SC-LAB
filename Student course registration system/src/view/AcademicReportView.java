package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.*;
import controller.AcademicReportController;
import model.AcademicModel;
import model.UserModel;
import model.CourseModel;
import java.io.File;

public class AcademicReportView extends JFrame {
    private JButton[] menuButtons;
    private String[] buttonLabels = {
        "Dashboard", "Register Courses", "Courses List", "Academic Report", "Time Table", "Logout"
    };
    private String[] iconFiles = {
        "dashboard.png",
        "registercourses.png",
        "courseslist.png",
        "academicreport.png",
        "timetable.png",
        "logout.png"
    };
    private JPanel contentPanel;
    private int activeIndex = 3;
    private AcademicReportController controller;
    private AcademicModel academicModel;
    private UserModel userModel;
    private String username;
    private JTable reportTable;
    private JLabel gpaLabel;
    private JLabel cgpaLabel;
    private CourseModel courseModel;
    private JComboBox<String> semesterComboBox;
    private int currentSemesterIndex = 0;
    private JLabel statusLabel;
    private JButton prevButton, nextButton;

    private Image loadImage(String filename) {
        String basePath = System.getProperty("user.dir") + File.separator;
        return new ImageIcon(basePath + filename).getImage();
    }

    public AcademicReportView(AcademicReportController controller, AcademicModel academicModel, UserModel userModel, String username, CourseModel courseModel) {
        this.controller = controller;
        this.academicModel = academicModel;
        this.userModel = userModel;
        this.username = username;
        this.courseModel = courseModel;
        
        setTitle("QAU Student Console");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Custom background panel with image and green overlay
        JPanel background = new JPanel(new BorderLayout()) {
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
        JLabel consoleLabel = new JLabel("QAU STUDENT CONSOLE");
        consoleLabel.setFont(new Font("Century Gothic", Font.BOLD, 24));
        consoleLabel.setForeground(Color.WHITE);
        leftPanel.add(consoleLabel);
        header.add(leftPanel, BorderLayout.WEST);

        // Center: Welcome
        JLabel welcome = new JLabel("Welcome : " + username, SwingConstants.CENTER);
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
            btn.setIcon(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + iconFiles[i]).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
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

        // Academic Report Panel
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(Color.WHITE);

        // Top section with GPA and CGPA
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        statsPanel.setBackground(Color.WHITE);
        gpaLabel = new JLabel("GPA: 0.00");
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cgpaLabel = new JLabel("CGPA: 0.00");
        cgpaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statsPanel.add(gpaLabel);
        statsPanel.add(cgpaLabel);
        reportPanel.add(statsPanel, BorderLayout.NORTH);

        // Center section with table
        String[] columnNames = {"Course Code", "Course Title", "Credit Hours", "Marks", "Grade", "G.P"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnNames.length; i++) {
            reportTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JScrollPane scrollPane = new JScrollPane(reportTable);
        reportPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Scholastic Status and navigation
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Scholastic Status: ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        JPanel navPanel = new JPanel();
        navPanel.setBackground(Color.WHITE);
        prevButton = new JButton("< Prev");
        nextButton = new JButton("Next >");
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        bottomPanel.add(navPanel, BorderLayout.EAST);
        reportPanel.add(bottomPanel, BorderLayout.SOUTH);

        contentPanel.add(reportPanel, BorderLayout.CENTER);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(menuPanel, BorderLayout.NORTH);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        background.add(centerPanel, BorderLayout.CENTER);

        // Navigation actions
        prevButton.addActionListener(e -> {
            controller.onPrevSemester();
        });
        nextButton.addActionListener(e -> {
            controller.onNextSemester();
        });
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

    public void displayReportTable(String name, String regNo, String program, String semLabel, java.util.List<Object[]> tableRows, double gpa, double cgpa, int index, int total) {
        String headerText = String.format(
            "<html><b>Name:</b> %s &nbsp;&nbsp; <b>Reg No:</b> %s &nbsp;&nbsp; <b>Program:</b> %s &nbsp;&nbsp; <b>Semester:</b> %s</html>",
            name, regNo, program, semLabel
        );
        statusLabel.setText(headerText);

        gpaLabel.setText(String.format("GPA: %.2f", gpa));
        cgpaLabel.setText(String.format("CGPA: %.2f", cgpa));
        setTitle(semLabel);

        DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
        model.setRowCount(0);
        for (Object[] row : tableRows) {
            model.addRow(row);
        }
        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < total - 1);
    }
} 