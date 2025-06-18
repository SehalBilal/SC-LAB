package controller;

import view.TimetableCoordinatorDashboardView;
import model.UserModel;
import model.CourseModel;
import model.AcademicModel;
import java.util.HashMap;
import java.util.ArrayList;

public class TimetableCoordinatorDashboardController {
    private UserModel userModel;
    private TimetableCoordinatorDashboardView view;
    private String username;
    private CourseModel courseModel;
    private AcademicModel academicModel;

    public TimetableCoordinatorDashboardController(String username, UserModel userModel, CourseModel courseModel, AcademicModel academicModel) {
        this.username = username;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
        this.view = new TimetableCoordinatorDashboardView(this);
    }

    public void showView() {
        view.setVisible(true);
    }

    public String getUsername() {
        return username;
    }

    public void navigateTo(int idx) {
        view.dispose();
        switch (idx) {
            case 0:
                new CheckTimetableConflictController(this, courseModel, "Fall 2024", "04072313001").showView();
                break;
            case 1:
                new ChangeClassScheduleController(this, new HashMap<>(), new ArrayList<>(), courseModel, username).showView();
                break;
            case 2: // Logout
                new controller.LoginController(userModel, null, courseModel, academicModel).setLoginView(new view.LoginView(new controller.LoginController(userModel, null, courseModel, academicModel)));
                new view.LoginView(new controller.LoginController(userModel, null, courseModel, academicModel)).setVisible(true);
                break;
        }
    }

    // Add navigation methods for each use case as needed
} 