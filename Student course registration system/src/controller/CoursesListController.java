package controller;

import view.CoursesListView;
import model.UserModel;
import model.CourseModel;
import model.AcademicModel;

public class CoursesListController {
    private CoursesListView view;
    private UserModel userModel;
    private CourseModel courseModel;
    private AcademicModel academicModel;
    private String username;

    public CoursesListController(String username, UserModel userModel, CourseModel courseModel, AcademicModel academicModel) {
        this.username = username;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
        this.view = new CoursesListView(this, courseModel, userModel, username);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void navigateTo(int idx) {
        view.dispose();
        new controller.DashboardController(username, userModel, courseModel, academicModel).navigateTo(idx);
    }
} 