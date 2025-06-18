package controller;

import view.RegisterCoursesView;
import model.UserModel;
import model.CourseModel;
import model.AcademicModel;
import javax.swing.JOptionPane;

public class RegisterCoursesController {
    private RegisterCoursesView view;
    private UserModel userModel;
    private CourseModel courseModel;
    private AcademicModel academicModel;
    private String username;
    private java.util.List<CourseModel.CourseInfo> eligibleCourses;
    private String currentSemester = "BS Semester IV"; // Reverted to BS Semester IV

    public RegisterCoursesController(String username, UserModel userModel, CourseModel courseModel, AcademicModel academicModel) {
        this.username = username;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
        // Get eligible courses for the current semester
        this.eligibleCourses = academicModel.getEligibleCoursesForStudent(currentSemester, username, courseModel);
        this.view = new RegisterCoursesView(this, courseModel, userModel, username, eligibleCourses);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void navigateTo(int idx) {
        view.dispose();
        new DashboardController(username, userModel, courseModel, academicModel).navigateTo(idx);
    }

    public java.util.List<CourseModel.CourseInfo> getEligibleCourses() {
        return eligibleCourses;
    }
} 