package controller;

import view.ChangeClassScheduleView;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import model.Conflict;
import model.CourseModel;

public class ChangeClassScheduleController {
    private ChangeClassScheduleView view;
    private TimetableCoordinatorDashboardController parentController;
    private Map<String, Map<String, List<String>>> timetable;
    private List<Conflict> conflicts;
    private CourseModel courseModel;
    private String studentId;

    public ChangeClassScheduleController(TimetableCoordinatorDashboardController parentController, Map<String, Map<String, List<String>>> timetable, List<Conflict> conflicts, CourseModel courseModel, String studentId) {
        this.parentController = parentController;
        this.timetable = timetable;
        this.conflicts = conflicts;
        this.courseModel = courseModel;
        this.studentId = studentId;
        this.view = new ChangeClassScheduleView(this, timetable, conflicts);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public void saveChanges() {
        // Collect the edited timetable from the view's table model
        Map<String, Map<String, List<String>>> editedTimetable = new HashMap<>();
        for (int row = 0; row < view.getTableModel().getRowCount(); row++) {
            String day = (String) view.getTableModel().getValueAt(row, 0);
            String room = (String) view.getTableModel().getValueAt(row, 1);
            if (!editedTimetable.containsKey(day)) editedTimetable.put(day, new HashMap<>());
            List<String> slots = new ArrayList<>();
            for (int col = 2; col < view.getTableModel().getColumnCount(); col++) {
                slots.add((String) view.getTableModel().getValueAt(row, col));
            }
            editedTimetable.get(day).put(room, slots);
        }
        // Save the edited timetable to the database using the model
        courseModel.saveCustomTimetable(studentId, editedTimetable);
        System.out.println("Edited timetable: " + editedTimetable);
        view.dispose();
        parentController.showView();
    }
} 