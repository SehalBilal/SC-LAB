package controller;

import model.UserModel;
import view.LoginView;
import model.CourseModel;
import model.AcademicModel;

public class LoginController {
    private UserModel userModel;
    private LoginView loginView;
    private CourseModel courseModel;
    private AcademicModel academicModel;

    public LoginController(UserModel userModel, LoginView loginView, CourseModel courseModel, AcademicModel academicModel) {
        this.userModel = userModel;
        this.loginView = loginView;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public boolean validateLogin(String username, String password, String role) {
        if (role.equals("Student")) {
            return userModel.validateStudentLogin(username, password);
        } else if (role.equals("Course Coordinator")) {
            return userModel.validateCourseCoordinatorLogin(username, password);
        } else if (role.equals("Timetable Coordinator")) {
            return userModel.validateTimetableCoordinatorLogin(username, password);
        }
        return false;
    }

    public void showDashboard(String username, String role) {
        loginView.dispose();
        if (role.equals("Student")) {
            new DashboardController(username, userModel, courseModel, academicModel).showView();
        } else if (role.equals("Course Coordinator")) {
            new CourseCoordinatorDashboardController(username, userModel, courseModel, academicModel).showView();
        } else if (role.equals("Timetable Coordinator")) {
            new TimetableCoordinatorDashboardController(username, userModel, courseModel, academicModel).showView();
        }
    }
} 