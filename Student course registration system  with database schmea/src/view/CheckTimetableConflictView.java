package view;

import javax.swing.*;
import java.awt.*;
import controller.CheckTimetableConflictController;
import java.util.List;
import java.util.Map;
import model.Conflict;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import model.CourseModel;

public class CheckTimetableConflictView extends JFrame {
    private JTable timetableTable;
    private JButton solveBtn;
    private JLabel infoLabel;

    public CheckTimetableConflictView(CheckTimetableConflictController controller, Map<String, Map<String, List<String>>> timetable, List<Conflict> conflicts) {
        setTitle("Check Timetable Conflict");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Info label
        infoLabel = new JLabel(conflicts.isEmpty() ? "No timetable conflicts found." : "Conflicts detected! Please resolve.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        infoLabel.setForeground(conflicts.isEmpty() ? new Color(0, 160, 120) : Color.RED);
        add(infoLabel, BorderLayout.NORTH);

        // Table setup (match TimeTableView)
        String[] columnNames = {"Day", "Room", "9:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        // Build table rows from timetable map
        for (String day : timetable.keySet()) {
            Map<String, List<String>> rooms = timetable.get(day);
            for (String room : rooms.keySet()) {
                List<String> slots = rooms.get(room);
                String[] displayRow = new String[7];
                displayRow[0] = day;
                displayRow[1] = room;
                for (int i = 0; i < 5; i++) {
                    displayRow[i+2] = (slots != null && slots.size() > i) ? (slots.get(i) == null ? "" : slots.get(i)) : "";
                }
                model.addRow(displayRow);
            }
        }
        timetableTable = new JTable(model);
        timetableTable.setRowHeight(40);
        timetableTable.setFont(new Font("Arial", Font.PLAIN, 14));
        timetableTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        timetableTable.setDefaultRenderer(Object.class, new ConflictCellRenderer(conflicts));
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        add(scrollPane, BorderLayout.CENTER);

        // Solve button
        solveBtn = new JButton("Solve Timetable Conflict");
        solveBtn.setFont(new Font("Arial", Font.BOLD, 16));
        solveBtn.setBackground(new Color(220, 53, 69));
        solveBtn.setForeground(Color.WHITE);
        solveBtn.setVisible(!conflicts.isEmpty());
        solveBtn.addActionListener(e -> controller.solveConflict());

        // Back button
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(0, 160, 120));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> controller.backToDashboard());

        JPanel btnPanel = new JPanel();
        btnPanel.add(backBtn);
        btnPanel.add(solveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // Custom cell renderer to highlight conflicts
    private static class ConflictCellRenderer extends DefaultTableCellRenderer {
        private final List<Conflict> conflicts;
        public ConflictCellRenderer(List<Conflict> conflicts) { this.conflicts = conflicts; }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(Color.WHITE);
            if (column >= 2) {
                String day = (String) table.getValueAt(row, 0);
                String room = (String) table.getValueAt(row, 1);
                int slot = column - 2;
                for (Conflict conflict : conflicts) {
                    if (conflict.getDay().equals(day) && conflict.getRoom().equals(room) && conflict.getTimeSlot() == slot) {
                        c.setBackground(new Color(255, 204, 204)); // light red
                        break;
                    }
                }
            }
            return c;
        }
    }
} 