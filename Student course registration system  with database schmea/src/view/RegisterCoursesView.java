package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.List;
import java.util.ArrayList;
import controller.RegisterCoursesController;
import model.CourseModel;
import model.UserModel;
import java.io.File;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.AbstractTableModel;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class RegisterCoursesView extends JFrame {
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
    private int activeIndex = 1;
    private DefaultTableModel tableModel;
    private JTextField courseCodeField;
    private JLabel creditLabel;
    private int totalCredits = 0;
    private Set<String> addedCourses = new HashSet<>();
    private RegisterCoursesController controller;
    private CourseModel courseModel;
    private UserModel userModel;
    private String username;
    private JTable courseTable;
    private JButton confirmButton;
    private java.util.List<CourseModel.CourseInfo> eligibleCourses;
    private JTable eligibleTable;
    private EligibleCoursesTableModel eligibleTableModel;
    private JButton deleteButton;
    private JButton enrollButton;
    private JLabel totalCreditsLabel;

    private Image loadImage(String filename) {
        String basePath = System.getProperty("user.dir") + File.separator;
        return new ImageIcon(basePath + filename).getImage();
    }

    public RegisterCoursesView(RegisterCoursesController controller, CourseModel courseModel, UserModel userModel, String username, java.util.List<CourseModel.CourseInfo> eligibleCourses) {
        this.controller = controller;
        this.courseModel = courseModel;
        this.userModel = userModel;
        this.username = username;
        this.eligibleCourses = controller.getEligibleCourses();
        setTitle("QAU Student Console");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Custom background panel with image and green overlay (like TimeTableView)
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

        // HEADER BAR (like TimeTableView)
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

        // MENU BAR (like TimeTableView)
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

        // Table Model
        eligibleTableModel = new EligibleCoursesTableModel(this.eligibleCourses);
        eligibleTableModel.addTableModelListener(e -> updateTotalCredits());
        eligibleTable = new JTable(eligibleTableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return super.getCellRenderer(row, column);
            }
        };
        eligibleTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        eligibleTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        eligibleTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        eligibleTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        eligibleTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        JScrollPane tableScroll = new JScrollPane(eligibleTable);
        tableScroll.setPreferredSize(new Dimension(900, 300));

        // Total credits label
        totalCreditsLabel = new JLabel("Total Credits: 0");
        totalCreditsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateTotalCredits();

        // Buttons
        deleteButton = new JButton("Delete");
        enrollButton = new JButton("Enroll");
        deleteButton.setBackground(new Color(255, 204, 102));
        enrollButton.setBackground(new Color(0, 160, 120));
        enrollButton.setForeground(Color.WHITE);
        enrollButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.addActionListener(e -> deleteSelectedCourses());
        enrollButton.addActionListener(e -> confirmRegistration());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(new JScrollPane(eligibleTable), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalCreditsLabel, BorderLayout.WEST);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(enrollButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

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

    private void updateTotalCredits() {
        int total = 0;
        for (int i = 0; i < eligibleTableModel.getRowCount(); i++) {
            if ((Boolean) eligibleTableModel.getValueAt(i, 0)) {
                total += (Integer) eligibleTableModel.getValueAt(i, 4);
            }
        }
        totalCreditsLabel.setText("Total Credits: " + total);
    }

    private void deleteSelectedCourses() {
        eligibleTableModel.deleteSelected();
        updateTotalCredits();
    }

    private void confirmRegistration() {
        int total = 0;
        userModel.clearRegisteredCourses(username);
        for (int i = 0; i < eligibleTableModel.getRowCount(); i++) {
            if ((Boolean) eligibleTableModel.getValueAt(i, 0)) {
                CourseModel.CourseInfo courseInfo = eligibleTableModel.getCourseInfoAt(i);
                userModel.addRegisteredCourse(username, courseInfo);
                total += courseInfo.credits;
            }
        }
        updateTotalCredits();
        JOptionPane.showMessageDialog(this, "Courses registered successfully! Total Credits: " + total, "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // Table Model for eligible courses
    private static class EligibleCoursesTableModel extends AbstractTableModel {
        private final String[] columns = {"Select", "Course Code", "Course Name", "Instructor", "Units"};
        private final java.util.List<Object[]> data = new java.util.ArrayList<>();
        private final java.util.List<CourseModel.CourseInfo> originalCourses; // To hold original CourseInfo objects

        public EligibleCoursesTableModel(java.util.List<CourseModel.CourseInfo> eligibleCourses) {
            this.originalCourses = eligibleCourses; // Store original list
            for (CourseModel.CourseInfo info : eligibleCourses) {
                data.add(new Object[]{false, info.code, info.title, info.faculty, info.credits});
            }
        }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public Object getValueAt(int row, int col) { return data.get(row)[col]; }
        @Override public void setValueAt(Object aValue, int row, int col) { data.get(row)[col] = aValue; fireTableCellUpdated(row, col); }
        @Override public boolean isCellEditable(int row, int col) { return col == 0; }
        @Override public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : super.getColumnClass(col); }
        
        public CourseModel.CourseInfo getCourseInfoAt(int row) {
            // Return the original CourseInfo object associated with this row
            return originalCourses.get(row);
        }

        public void deleteSelected() {
            // Collect indices to delete in reverse order to avoid issues with shifting indices
            List<Integer> selectedRows = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if ((Boolean) data.get(i)[0]) {
                    selectedRows.add(i);
                }
            }

            // Delete from both data and originalCourses lists
            Collections.sort(selectedRows, Collections.reverseOrder());
            for (int rowIdx : selectedRows) {
                data.remove(rowIdx);
                originalCourses.remove(rowIdx); // Keep original list in sync
            }
            fireTableDataChanged();
        }
    }
} 