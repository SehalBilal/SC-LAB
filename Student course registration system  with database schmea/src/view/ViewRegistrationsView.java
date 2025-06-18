package view;

import javax.swing.*;
import java.awt.*;
import controller.ViewRegistrationsController;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import model.RegistrationModel;

public class ViewRegistrationsView extends JFrame {
    private ViewRegistrationsController controller;
    private JComboBox<String> semesterBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel studentInfoLabel;

    public ViewRegistrationsView(ViewRegistrationsController controller, List<String> semesters, List<RegistrationModel.RegistrationRow> rows, String studentRegNo, String studentName) {
        this.controller = controller;
        setTitle("View Registrations");
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
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = new ImageIcon("Student course registration system/logo.png").getImage();
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
        JLabel consoleLabel = new JLabel("VIEW REGISTRATIONS");
        consoleLabel.setFont(new Font("Century Gothic", Font.BOLD, 24));
        consoleLabel.setForeground(Color.WHITE);
        leftPanel.add(consoleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        header.add(leftPanel, BorderLayout.WEST);
        background.add(header, BorderLayout.NORTH);

        // Student info label (top)
        studentInfoLabel = new JLabel("Name: " + studentName + "    Reg No: " + studentRegNo, SwingConstants.LEFT);
        studentInfoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        studentInfoLabel.setForeground(new Color(0, 80, 60));
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        infoPanel.setOpaque(false);
        infoPanel.add(studentInfoLabel);
        background.add(infoPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table
        String[] columnNames = {"Course Code", "Course Title", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 17));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 220, 200));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.DARK_GRAY);
        table.getTableHeader().setBackground(new Color(0, 160, 160));
        table.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        background.add(scrollPane, BorderLayout.CENTER);
        updateTable(rows);

        // Remove old semester dropdown and back button panels
        // Create bottom bar with semester dropdown (left) and dashboard button (right)
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(new Color(0, 160, 120));
        bottomBar.setPreferredSize(new Dimension(0, 60));

        // Left: Semester dropdown
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        leftBottom.setOpaque(false);
        JLabel semLabel = new JLabel("Semester:");
        semLabel.setFont(new Font("Arial", Font.BOLD, 17));
        semLabel.setForeground(new Color(0, 80, 60));
        semesterBox = new JComboBox<>(semesters.toArray(new String[0]));
        semesterBox.setFont(new Font("Arial", Font.PLAIN, 16));
        semesterBox.addActionListener(e -> {
            String selected = (String) semesterBox.getSelectedItem();
            controller.onSemesterChanged(selected);
        });
        leftBottom.add(semLabel);
        leftBottom.add(semesterBox);
        bottomBar.add(leftBottom, BorderLayout.WEST);

        // Right: Dashboard button
        JButton backBtn = new JButton("Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(0, 160, 120));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> controller.backToDashboard());
        JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 12));
        rightBottom.setOpaque(false);
        rightBottom.add(backBtn);
        bottomBar.add(rightBottom, BorderLayout.EAST);

        background.add(bottomBar, BorderLayout.SOUTH);
    }

    public void updateTable(List<RegistrationModel.RegistrationRow> rows) {
        tableModel.setRowCount(0);
        for (RegistrationModel.RegistrationRow row : rows) {
            tableModel.addRow(new Object[]{row.courseCode, row.courseTitle, row.status});
        }
    }
} 