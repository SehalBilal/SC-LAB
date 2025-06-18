package controller;

import view.TimeTableView;
import model.UserModel;
import model.CourseModel;
import model.AcademicModel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class TimeTableController {
    private TimeTableView view;
    private UserModel userModel;
    private CourseModel courseModel;
    private AcademicModel academicModel;
    private String username;

    public TimeTableController(String username, UserModel userModel, CourseModel courseModel, AcademicModel academicModel) {
        this.username = username;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
        this.view = new TimeTableView(this, courseModel, userModel, username);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void navigateTo(int idx) {
        view.dispose();
        new controller.DashboardController(username, userModel, courseModel, academicModel).navigateTo(idx);
    }

    public LinkedHashMap<String, Map<String, List<String>>> getStudentTimetable() {
        return courseModel.getStudentTimetableMatrix(username);
    }
} 