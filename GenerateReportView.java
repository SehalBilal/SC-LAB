package view;

import javax.swing.*;
import java.awt.*;
import controller.GenerateReportController;
import model.AcademicModel;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GenerateReportView extends JFrame {
    private GenerateReportController controller;
    private JPanel contentPanel;
    private int currentSemesterIndex = 0;
    private List<String> semesterList = null;
    private AcademicModel lastModel = null;
    private String lastRegno = null;
    private JTable currentTable = null;
    private JLabel errorLabel;

    public GenerateReportView(GenerateReportController controller) {
        this.controller = controller;
        setTitle("Generate Report");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Custom background panel with image and green overlay
        JPanel background = new JPanel(new GridBagLayout()) {
            Image bg = GenerateReportView.this.loadImage("background.jpg");
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

        // Centered search card
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        card.setMaximumSize(new Dimension(500, 300));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Century Gothic", Font.BOLD, 18));
        errorLabel.setForeground(new Color(220, 53, 69));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);

        JLabel prompt = new JLabel("Enter Student Reg No:");
        prompt.setFont(new Font("Century Gothic", Font.BOLD, 20));
        prompt.setForeground(Color.BLACK);
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(prompt);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JTextField regField = new JTextField(16);
        regField.setFont(new Font("Arial", Font.PLAIN, 20));
        regField.setMaximumSize(new Dimension(350, 40));
        regField.setPreferredSize(new Dimension(350, 40));
        regField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,160,120), 2),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        card.add(regField);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Century Gothic", Font.BOLD, 20));
        searchBtn.setBackground(new Color(0,160,120));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,160,120), 2),
            BorderFactory.createEmptyBorder(8, 32, 8, 32)));
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(searchBtn);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        background.add(card, gbc);

        // Content panel for report
        gbc.gridy++;
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        background.add(contentPanel, gbc);

        // Bottom panel with Back only
        gbc.gridy++;
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
        backBtn.setBackground(new Color(220, 53, 69));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 2),
            BorderFactory.createEmptyBorder(6, 18, 6, 18)));
        backBtn.addActionListener(e -> controller.backToDashboard());
        bottomPanel.add(backBtn);
        background.add(bottomPanel, gbc);

        searchBtn.addActionListener(e -> {
            errorLabel.setText("");
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            controller.checkAndShowReport(regField.getText().trim());
        });
    }

    public void showReport(String regno, AcademicModel model) {
        contentPanel.removeAll();
        this.lastRegno = regno;
        this.lastModel = model;
        this.semesterList = model.getSemesters();
        if (currentSemesterIndex < 0 || currentSemesterIndex >= semesterList.size()) currentSemesterIndex = 0;
        JPanel report = new JPanel();
        report.setLayout(new BoxLayout(report, BoxLayout.Y_AXIS));
        report.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,160,120), 2),
                "Academic Report for Reg No: " + regno,
                0, 0, new Font("Century Gothic", Font.BOLD, 20), new Color(0,160,120)),
            BorderFactory.createEmptyBorder(24, 48, 24, 48)));
        report.setBackground(Color.WHITE);

        // Only show one semester at a time
        if (semesterList.size() > 0) {
            String sem = semesterList.get(currentSemesterIndex);
            JPanel semPanel = new JPanel(new BorderLayout());
            semPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0,160,120), 2, true), sem, 0, 0, new Font("Century Gothic", Font.BOLD, 17), new Color(0,160,120)),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)));
            semPanel.setBackground(new Color(245, 255, 255));
            semPanel.setMaximumSize(new Dimension(900, 320));
            semPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            Map<String, Map<String, Object>> recs = model.getSemesterRecords(regno, sem);
            String[] colNames = {"Course Name", "Marks", "Grade", "Credits", "Pass/Fail"};
            String[][] data = new String[recs.size()][5];
            int i = 0;
            double semPoints = 0;
            int semCredits = 0;
            for (String course : recs.keySet()) {
                Map<String, Object> info = recs.get(course);
                data[i][0] = (String) info.getOrDefault("name", course);
                double marks = (info.get("marks") instanceof Integer) ? ((Integer)info.get("marks")).doubleValue() : (Double)info.get("marks");
                data[i][1] = String.valueOf(marks);
                data[i][2] = AcademicModel.getGrade(marks);
                data[i][3] = info.get("credits")+"";
                double gp = AcademicModel.calculateSubjectGPA(marks);
                int credits = Integer.parseInt(data[i][3]);
                semPoints += gp * credits;
                semCredits += credits;
                boolean passed = marks >= 50; // Assuming 50 is passing
                data[i][4] = passed ? "Pass" : "Fail";
                i++;
            }
            JTable table = new JTable(data, colNames);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            table.setRowHeight(34);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
            table.setEnabled(false);
            table.setShowGrid(true);
            table.setGridColor(new Color(180, 220, 220));
            table.setBackground(Color.WHITE);
            table.setForeground(Color.DARK_GRAY);
            table.getTableHeader().setBackground(new Color(0, 160, 160));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setBorder(BorderFactory.createLineBorder(new Color(0,160,120), 1, true));
            // Set preferred column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(350); // Course Name wider
            table.getColumnModel().getColumn(1).setPreferredWidth(90);  // Marks
            table.getColumnModel().getColumn(2).setPreferredWidth(90);  // Grade
            table.getColumnModel().getColumn(3).setPreferredWidth(90);  // Credits
            table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Pass/Fail
            int visibleRows = Math.max(8, data.length); // Show at least 8 rows
            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setPreferredSize(new Dimension(800, 40 + 34 * visibleRows));
            semPanel.add(scroll, BorderLayout.CENTER);
            this.currentTable = table;
            // Semester GPA only
            double semGPA = semCredits > 0 ? Math.round((semPoints / semCredits) * 100.0) / 100.0 : 0.0;
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 8));
            bottom.setOpaque(false);
            JLabel gpaLabel = new JLabel("Semester GPA: " + semGPA);
            gpaLabel.setFont(new Font("Century Gothic", Font.BOLD, 19));
            gpaLabel.setForeground(new Color(0,160,120));
            bottom.add(gpaLabel);
            semPanel.add(bottom, BorderLayout.SOUTH);
            semPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,160,120), 2, true),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)));
            semPanel.setBackground(new Color(245, 255, 255, 240));
            semPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            semPanel.setMaximumSize(new Dimension(900, 360));
            semPanel.setPreferredSize(new Dimension(900, 320));
            semPanel.setMinimumSize(new Dimension(700, 180));
            report.add(Box.createVerticalStrut(20));
            report.add(semPanel);

            // Navigation buttons
            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
            navPanel.setOpaque(false);
            JButton prevBtn = new JButton("< Previous Semester");
            JButton nextBtn = new JButton("Next Semester >");
            prevBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
            nextBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
            prevBtn.setBackground(new Color(0,160,120));
            prevBtn.setForeground(Color.WHITE);
            nextBtn.setBackground(new Color(0,160,120));
            nextBtn.setForeground(Color.WHITE);
            prevBtn.setFocusPainted(false);
            nextBtn.setFocusPainted(false);
            prevBtn.setEnabled(currentSemesterIndex > 0);
            nextBtn.setEnabled(currentSemesterIndex < semesterList.size() - 1);
            prevBtn.addActionListener(e -> {
                currentSemesterIndex--;
                showReport(lastRegno, lastModel);
            });
            nextBtn.addActionListener(e -> {
                currentSemesterIndex++;
                showReport(lastRegno, lastModel);
            });
            navPanel.add(prevBtn);
            navPanel.add(nextBtn);
            // Export/Print buttons
            JButton exportCsvBtn = new JButton("Export to CSV");
            exportCsvBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
            exportCsvBtn.setBackground(new Color(0, 160, 120));
            exportCsvBtn.setForeground(Color.WHITE);
            exportCsvBtn.setFocusPainted(false);
            exportCsvBtn.addActionListener(e -> exportCurrentTableToCSV());
            navPanel.add(exportCsvBtn);
            JButton printBtn = new JButton("Print");
            printBtn.setFont(new Font("Century Gothic", Font.BOLD, 16));
            printBtn.setBackground(new Color(0, 160, 120));
            printBtn.setForeground(Color.WHITE);
            printBtn.setFocusPainted(false);
            printBtn.addActionListener(e -> printCurrentTable());
            navPanel.add(printBtn);
            report.add(navPanel);
        }
        // Overall CGPA
        double totalPoints = 0;
        int totalCredits = 0;
        for (String sem : semesterList) {
            Map<String, Map<String, Object>> recs = model.getSemesterRecords(regno, sem);
            for (String course : recs.keySet()) {
                Map<String, Object> info = recs.get(course);
                double marks = (info.get("marks") instanceof Integer) ? ((Integer)info.get("marks")).doubleValue() : (Double)info.get("marks");
                double gp = AcademicModel.calculateSubjectGPA(marks);
                int credits = Integer.parseInt(info.get("credits")+"");
                totalPoints += gp * credits;
                totalCredits += credits;
            }
        }
        double cgpa = totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
        JLabel cgpaLabel = new JLabel("Overall CGPA: " + cgpa);
        cgpaLabel.setFont(new Font("Century Gothic", Font.BOLD, 26));
        cgpaLabel.setForeground(new Color(0,160,120));
        cgpaLabel.setBorder(BorderFactory.createEmptyBorder(32, 0, 0, 0));
        cgpaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        report.add(cgpaLabel);
        contentPanel.add(report, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showError(String msg) {
        errorLabel.setText(msg);
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Export/Print methods
    private void exportCurrentTableToCSV() {
        if (currentTable == null) return;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }
            try (FileWriter fw = new FileWriter(fileToSave)) {
                // Write header
                for (int i = 0; i < currentTable.getColumnCount(); i++) {
                    fw.write(currentTable.getColumnName(i));
                    if (i < currentTable.getColumnCount() - 1) fw.write(",");
                }
                fw.write("\n");
                // Write rows
                for (int row = 0; row < currentTable.getRowCount(); row++) {
                    for (int col = 0; col < currentTable.getColumnCount(); col++) {
                        fw.write(currentTable.getValueAt(row, col).toString());
                        if (col < currentTable.getColumnCount() - 1) fw.write(",");
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "CSV exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printCurrentTable() {
        if (currentTable == null) return;
        try {
            boolean complete = currentTable.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("Semester Report"), null);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Print successful!", "Print", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Print cancelled.", "Print", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error printing: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Image loadImage(String filename) {
        String basePath = System.getProperty("user.dir") + java.io.File.separator;
        return new ImageIcon(basePath + filename).getImage();
    }
} 