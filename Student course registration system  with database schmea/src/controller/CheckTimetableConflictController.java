package controller;

import view.CheckTimetableConflictView;
import model.CourseModel;
import model.Conflict;
import java.util.List;
import java.util.Map;

public class CheckTimetableConflictController {
    private CheckTimetableConflictView view;
    private TimetableCoordinatorDashboardController parentController;
    private CourseModel courseModel;
    private List<String> studentCourses;
    private List<Conflict> conflicts;
    private Map<String, Map<String, List<String>>> fullTimetable;
    private String studentId;
    private String semesterLabel;

    public CheckTimetableConflictController(TimetableCoordinatorDashboardController parentController, CourseModel courseModel, String semesterLabel, String studentId) {
        this.parentController = parentController;
        this.courseModel = courseModel;
        this.studentId = studentId;
        this.semesterLabel = semesterLabel;
        this.fullTimetable = courseModel.getStudentTimetableMatrix(studentId);
        this.conflicts = courseModel.checkConflicts(studentId, semesterLabel);
        this.view = new CheckTimetableConflictView(this, fullTimetable, conflicts);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public void solveConflict() {
        // Navigate to ChangeClassScheduleController with timetable and conflicts
        new ChangeClassScheduleController(parentController, fullTimetable, conflicts, courseModel, studentId).showView();
        view.dispose();
    }
} 