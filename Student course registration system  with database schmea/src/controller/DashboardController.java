package controller;

import view.DashboardView;
import view.LoginView;
import model.UserModel;
import model.CourseModel;
import model.AcademicModel;

public class DashboardController {
    private UserModel userModel;
    private DashboardView view;
    private String username;
    private CourseModel courseModel;
    private AcademicModel academicModel;

    public DashboardController(String username, UserModel userModel, CourseModel courseModel, AcademicModel academicModel) {
        this.username = username;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
        this.view = new DashboardView(this);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void navigateTo(int screenIndex) {
        view.dispose();
        switch (screenIndex) {
            case 0: // Dashboard
                new DashboardController(username, userModel, courseModel, academicModel).showView();
                break;
            case 1: // Register Courses
                new RegisterCoursesController(username, userModel, courseModel, academicModel).showView();
                break;
            case 2: // Courses List
                new CoursesListController(username, userModel, courseModel, academicModel).showView();
                break;
            case 3: // Academic Report
                new AcademicReportController(academicModel, userModel, courseModel, username).showView();
                break;
            case 4: // Time Table
                new TimeTableController(username, userModel, courseModel, academicModel).showView();
                break;
            case 5: // Logout
                LoginController loginController = new LoginController(userModel, null, courseModel, academicModel);
                LoginView loginView = new LoginView(loginController);
                loginController.setLoginView(loginView);
                loginView.setVisible(true);
                break;
        }
    }

    public String getUsername() {
        return username;
    }

    public UserModel getUserModel() {
        return userModel;
    }
} 