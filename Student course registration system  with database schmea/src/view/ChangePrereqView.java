package view;

import javax.swing.*;
import java.awt.*;
import controller.ChangePrereqController;
import model.UploadSchemeModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;
import java.io.File;

public class ChangePrereqView extends JFrame {
    private ChangePrereqController controller;
    private UploadSchemeModel model;
    private JTable table;
    private DefaultTableModel tableModel;

    public ChangePrereqView(ChangePrereqController controller, UploadSchemeModel model) {
        this.controller = controller;
        this.model = model;
        setTitle("Change Prerequisites");
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
            private Image loadImage(String filename) {
                String basePath = System.getProperty("user.dir") + File.separator;
                return new ImageIcon(basePath + filename).getImage();
            }
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
        JLabel consoleLabel = new JLabel("CHANGE PREREQUISITES");
        consoleLabel.setFont(new Font("Century Gothic", Font.BOLD, 24));
        consoleLabel.setForeground(Color.WHITE);
        leftPanel.add(consoleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        header.add(leftPanel, BorderLayout.WEST);
        background.add(header, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Course Code", "Course Title", "Pre-Requisite", "Semester"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        refreshTable();
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

        // Context menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem editPreReqItem = new JMenuItem();
        JMenuItem delPreReqItem = new JMenuItem("Delete Pre-Requisite");
        menu.add(editPreReqItem);
        menu.add(delPreReqItem);
        table.setComponentPopupMenu(menu);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                table.getSelectionModel().setSelectionInterval(row, row);
                String preReq = (String) tableModel.getValueAt(row, 2);
                if (preReq == null || preReq.trim().isEmpty()) {
                    editPreReqItem.setText("Add Pre-Requisite");
                } else {
                    editPreReqItem.setText("Edit Pre-Requisite");
                }
            }
        });
        editPreReqItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) showEditPreReqDialog(row);
        });
        delPreReqItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Only modify in-memory table. Persistence happens on Save Changes.
                tableModel.setValueAt("", row, 2);
            }
        });

        // Save and Back buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 16));
        saveBtn.setBackground(new Color(0, 160, 120));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveChangesToModel());
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(220, 53, 69));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> controller.backToDashboard());
        btnPanel.add(backBtn);
        btnPanel.add(saveBtn);
        background.add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        if (controller.getModel() != null) {
            for (UploadSchemeModel.CourseRow row : controller.getModel().getCourses()) {
                tableModel.addRow(new Object[]{row.code, row.title, row.preReq, row.semester});
            }
        }
    }

    private void showEditPreReqDialog(int rowIdx) {
        String currentPreReq = (String) tableModel.getValueAt(rowIdx, 2);
        JTextField preReqField = new JTextField(currentPreReq != null ? currentPreReq : "");
        JPanel panel = new JPanel(new GridLayout(0,1,8,8));
        panel.add(new JLabel("Pre-Requisite:"));
        panel.add(preReqField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Set Pre-Requisite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // Only modify in-memory table. Persistence happens on Save Changes.
            tableModel.setValueAt(preReqField.getText().trim(), rowIdx, 2);
        }
    }

    private void saveChangesToModel() {
        List<UploadSchemeModel.CourseRow> newCourses = new java.util.ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String code = (String) tableModel.getValueAt(i, 0);
            String title = (String) tableModel.getValueAt(i, 1);
            String preReq = (String) tableModel.getValueAt(i, 2);
            String semester = (String) tableModel.getValueAt(i, 3);
            newCourses.add(new UploadSchemeModel.CourseRow(code, title, preReq, semester));
        }
        try {
            controller.saveChanges(newCourses);
            JOptionPane.showMessageDialog(this, "Changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 