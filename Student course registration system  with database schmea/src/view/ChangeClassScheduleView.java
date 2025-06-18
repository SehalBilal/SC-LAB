package view;

import javax.swing.*;
import java.awt.*;
import controller.ChangeClassScheduleController;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Arrays;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.LinkedHashMap;

public class ChangeClassScheduleView extends JFrame {
    private JTable timetableTable;
    private EditableTimetableModel tableModel;
    private Set<String> editableCells;
    private Set<String> suggestedSlots;
    private List<String> fourthSemesterCourses = Arrays.asList(
        "CS-212 HCI", "CH-100 GC", "CH-190 GC Lab", "CS-213 COAL", "CS-293 COAL LAB", "CS-225 DBS", "MA-205"
    );
    private String lastDeletedCourse = null;

    public ChangeClassScheduleView(ChangeClassScheduleController controller, Map<String, Map<String, List<String>>> timetable, List<model.Conflict> conflicts) {
        setTitle("Change Class Schedule");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Edit timetable to resolve conflicts (editable cells highlighted, green = suggested slot)", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        add(label, BorderLayout.NORTH);

        // Build a set of editable cells (conflicting slots)
        editableCells = new HashSet<>();
        for (model.Conflict conflict : conflicts) {
            String day = conflict.getDay();
            String room = conflict.getRoom();
            int slot = conflict.getTimeSlot();
            editableCells.add(day + ":" + room + ":" + slot);
        }

        // Use the provided timetable for editing, not just the static timetable
        java.util.List<String[]> timetableRows = new java.util.ArrayList<>();
        for (Map.Entry<String, Map<String, List<String>>> dayEntry : timetable.entrySet()) {
            String day = dayEntry.getKey();
            for (Map.Entry<String, List<String>> roomEntry : dayEntry.getValue().entrySet()) {
                String room = roomEntry.getKey();
                List<String> slots = roomEntry.getValue();
                String[] row = new String[7];
                row[0] = day;
                row[1] = room;
                for (int i = 0; i < 5; i++) row[i+2] = slots.get(i);
                timetableRows.add(row);
            }
        }
        String[][] fullTimetable = timetableRows.toArray(new String[0][0]);
        tableModel = new EditableTimetableModel(fullTimetable, editableCells, suggestedSlots, this);
        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(32);
        timetableTable.setFont(new Font("Arial", Font.PLAIN, 15));
        timetableTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // Build suggestedSlots
        suggestedSlots = new HashSet<>();
        // For each conflict, for each conflicting course, find all empty slots (any day, any room) where no other 4th sem course is scheduled in that slot
        String[][] initialTimetable = new String[tableModel.getRowCount()][7];
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < 7; col++) {
                Object val = tableModel.getValueAt(row, col);
                initialTimetable[row][col] = val == null ? "" : val.toString();
            }
        }
        for (model.Conflict conflict : conflicts) {
            for (String course : conflict.getConflictingCourses()) {
                for (int row = 0; row < initialTimetable.length; row++) {
                    String day = initialTimetable[row][0];
                    String room = initialTimetable[row][1];
                    for (int slot = 0; slot < 5; slot++) {
                        String cellValue = initialTimetable[row][slot + 2];
                        if ((cellValue == null || cellValue.isEmpty()) && !otherFourthSemCourseInSlot(initialTimetable, day, slot, room, course)) {
                            suggestedSlots.add(day + ":" + room + ":" + slot);
                        }
                    }
                }
            }
        }

        // Highlight editable and suggested cells
        timetableTable.setDefaultRenderer(Object.class, new EditableCellRenderer(editableCells, suggestedSlots));

        // Set a custom cell editor that only allows editing for conflicting or suggested slots
        timetableTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(java.util.EventObject e) {
                int row = timetableTable.getSelectedRow();
                int col = timetableTable.getSelectedColumn();
                if (row >= 0 && col >= 2) {
                    String day = (String) timetableTable.getValueAt(row, 0);
                    String room = (String) timetableTable.getValueAt(row, 1);
                    int slot = col - 2;
                    String key = day + ":" + room + ":" + slot;
                    return editableCells.contains(key) || suggestedSlots.contains(key);
                }
                return false;
            }
        });

        // Add TableModelListener for dynamic update and to track last deleted course
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() >= 2) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    Object oldValue = e.getSource() instanceof EditableTimetableModel ? ((EditableTimetableModel) e.getSource()).getOldValue(row, col) : null;
                    Object newValue = tableModel.getValueAt(row, col);
                    if (oldValue != null && !oldValue.toString().isEmpty() && (newValue == null || newValue.toString().isEmpty())) {
                        // Track last deleted course
                        lastDeletedCourse = oldValue.toString();
                    }
                }
                recalculateConflictsAndSuggestions();
            }
        });

        // Add MouseListener for green slot input
        timetableTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = timetableTable.rowAtPoint(e.getPoint());
                int col = timetableTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 2) {
                    String day = (String) timetableTable.getValueAt(row, 0);
                    String room = (String) timetableTable.getValueAt(row, 1);
                    int slot = col - 2;
                    String key = day + ":" + room + ":" + slot;
                    if (suggestedSlots.contains(key) && ((String)timetableTable.getValueAt(row, col)).isEmpty()) {
                        String input = lastDeletedCourse != null ? lastDeletedCourse : JOptionPane.showInputDialog(
                            ChangeClassScheduleView.this,
                            "Paste or type the course code here to move the course:",
                            "Move Course",
                            JOptionPane.PLAIN_MESSAGE
                        );
                        if (input != null && !input.trim().isEmpty()) {
                            tableModel.setValueAt(input.trim(), row, col);
                            lastDeletedCourse = null;
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 16));
        saveBtn.setBackground(new Color(0, 160, 120));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> controller.saveChanges());
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(220, 53, 69));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> controller.backToDashboard());
        JPanel btnPanel = new JPanel();
        btnPanel.add(backBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // Recalculate conflicts and suggestions after any edit
    private void recalculateConflictsAndSuggestions() {
        editableCells.clear();
        suggestedSlots.clear();
        String[][] currentTimetable = new String[tableModel.getRowCount()][7];
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < 7; col++) {
                Object val = tableModel.getValueAt(row, col);
                currentTimetable[row][col] = val == null ? "" : val.toString();
            }
        }
        List<model.Conflict> newConflicts = findConflicts(currentTimetable);
        for (model.Conflict conflict : newConflicts) {
            String day = conflict.getDay();
            String room = conflict.getRoom();
            int slot = conflict.getTimeSlot();
            editableCells.add(day + ":" + room + ":" + slot);
        }
        // For each conflict, for each conflicting course, find all empty slots (any day, any room) where no other 4th sem course is scheduled in that slot
        for (model.Conflict conflict : newConflicts) {
            for (String course : conflict.getConflictingCourses()) {
                for (int row = 0; row < currentTimetable.length; row++) {
                    String day = currentTimetable[row][0];
                    String room = currentTimetable[row][1];
                    for (int slot = 0; slot < 5; slot++) {
                        String cellValue = currentTimetable[row][slot + 2];
                        if ((cellValue == null || cellValue.isEmpty()) && !otherFourthSemCourseInSlot(currentTimetable, day, slot, room, course)) {
                            suggestedSlots.add(day + ":" + room + ":" + slot);
                        }
                    }
                }
            }
        }
        // If lastDeletedCourse is set, highlight all valid empty slots for it
        if (lastDeletedCourse != null) {
            for (int row = 0; row < currentTimetable.length; row++) {
                String day = currentTimetable[row][0];
                String room = currentTimetable[row][1];
                for (int slot = 0; slot < 5; slot++) {
                    String cellValue = currentTimetable[row][slot + 2];
                    if ((cellValue == null || cellValue.isEmpty()) && !otherFourthSemCourseInSlot(currentTimetable, day, slot, room, lastDeletedCourse)) {
                        suggestedSlots.add(day + ":" + room + ":" + slot);
                    }
                }
            }
        }
        timetableTable.setDefaultRenderer(Object.class, new EditableCellRenderer(editableCells, suggestedSlots));
        timetableTable.repaint();
    }

    // Find conflicts for 4th semester courses in the current timetable
    private List<model.Conflict> findConflicts(String[][] timetable) {
        List<model.Conflict> conflicts = new ArrayList<>();
        // Map: day -> slot index -> list of (room, course code) for 4th sem courses
        Map<String, Map<Integer, List<String[]>>> slotMap = new LinkedHashMap<>();
        for (String[] row : timetable) {
            String day = row[0];
            String room = row[1];
            for (int i = 2; i < row.length; i++) {
                String code = row[i];
                if (code == null || code.isEmpty()) continue;
                for (String c : fourthSemesterCourses) {
                    if (code.contains(c)) {
                        slotMap.computeIfAbsent(day, k -> new LinkedHashMap<>());
                        slotMap.get(day).computeIfAbsent(i-2, k -> new ArrayList<>());
                        slotMap.get(day).get(i-2).add(new String[]{room, code});
                    }
                }
            }
        }
        // For each slot, if more than one course, it's a conflict
        for (String day : slotMap.keySet()) {
            for (Map.Entry<Integer, List<String[]>> entry : slotMap.get(day).entrySet()) {
                List<String[]> roomCourses = entry.getValue();
                if (roomCourses.size() > 1) {
                    for (String[] rc : roomCourses) {
                        String room = rc[0];
                        List<String> codes = new ArrayList<>();
                        for (String[] rc2 : roomCourses) codes.add(rc2[1]);
                        conflicts.add(new model.Conflict(day, room, entry.getKey(), codes.toArray(new String[0])));
                    }
                }
            }
        }
        return conflicts;
    }

    // Overload for dynamic check
    private boolean otherFourthSemCourseInSlot(String[][] timetable, String day, int slot, String room, String exceptCourse) {
        for (String[] row : timetable) {
            String d = row[0];
            String r = row[1];
            if (d.equals(day)) {
                String val = row[slot + 2];
                if (val != null && !val.isEmpty()) {
                    for (String c : fourthSemesterCourses) {
                        if (!c.equals(exceptCourse) && val.contains(c)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // EditableTimetableModel now takes the full timetable
    public static class EditableTimetableModel extends AbstractTableModel {
        private final String[] columnNames = {"Day", "Room", "9:05-10:35", "10:45-12:15", "12:25-13:55", "14:15-15:45", "16:00-17:30"};
        private final java.util.List<Object[]> data;
        private final Set<String> editableCells;
        private final Set<String> suggestedSlots;
        private ChangeClassScheduleView parent;
        private Object[][] oldValues;

        public EditableTimetableModel(String[][] fullTimetable, Set<String> editableCells, Set<String> suggestedSlots, ChangeClassScheduleView parent) {
            this.editableCells = editableCells;
            this.suggestedSlots = suggestedSlots;
            this.parent = parent;
            this.data = new ArrayList<>();
            this.oldValues = new Object[fullTimetable.length][7];
            for (int i = 0; i < fullTimetable.length; i++) {
                Object[] newRow = new Object[7];
                System.arraycopy(fullTimetable[i], 0, newRow, 0, 7);
                data.add(newRow);
                System.arraycopy(fullTimetable[i], 0, oldValues[i], 0, 7);
            }
        }

        @Override
        public int getRowCount() { return data.size(); }
        @Override
        public int getColumnCount() { return columnNames.length; }
        @Override
        public String getColumnName(int col) { return columnNames[col]; }
        @Override
        public Object getValueAt(int row, int col) { return data.get(row)[col]; }
        @Override
        public boolean isCellEditable(int row, int col) {
            if (col < 2) return false;
            String day = (String) getValueAt(row, 0);
            String room = (String) getValueAt(row, 1);
            int slot = col - 2;
            String key = day + ":" + room + ":" + slot;
            return editableCells.contains(key) || suggestedSlots.contains(key);
        }
        @Override
        public void setValueAt(Object value, int row, int col) {
            oldValues[row][col] = data.get(row)[col];
            data.get(row)[col] = value;
            fireTableCellUpdated(row, col);
        }
        public Object getOldValue(int row, int col) {
            return oldValues[row][col];
        }
    }

    // Renderer to highlight editable and suggested cells
    private static class EditableCellRenderer extends DefaultTableCellRenderer {
        private final Set<String> editableCells;
        private final Set<String> suggestedSlots;
        public EditableCellRenderer(Set<String> editableCells, Set<String> suggestedSlots) {
            this.editableCells = editableCells;
            this.suggestedSlots = suggestedSlots;
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(Color.WHITE);
            if (column >= 2) {
                String day = (String) table.getValueAt(row, 0);
                String room = (String) table.getValueAt(row, 1);
                int slot = column - 2;
                String key = day + ":" + room + ":" + slot;
                if (suggestedSlots.contains(key)) {
                    c.setBackground(new Color(200, 255, 200)); // light green for suggested move
                } else if (editableCells.contains(key)) {
                    c.setBackground(new Color(255, 255, 200)); // light yellow for editable
                }
            }
            return c;
        }
    }

    public EditableTimetableModel getTableModel() {
        return tableModel;
    }
} 