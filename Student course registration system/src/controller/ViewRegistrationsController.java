package controller;

import view.ViewRegistrationsView;
import model.RegistrationModel;
import java.util.*;

public class ViewRegistrationsController {
    private ViewRegistrationsView view;
    private CourseCoordinatorDashboardController parentController;
    private RegistrationModel model;
    private String studentRegNo;
    private String studentName;

    public ViewRegistrationsController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        this.studentRegNo = "04072313001"; // Hardcoded for testing, will be dynamic
        this.studentName = "Muhammad Ali"; // Hardcoded for testing, will be dynamic
        this.model = new RegistrationModel();
        this.model.setStudentRegNo(this.studentRegNo);
        this.view = new ViewRegistrationsView(this, getSemesters(), getRegistrationsForSemester("Spring 2023"), this.studentRegNo, this.studentName);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public List<String> getSemesters() {
        return model.getSemesters();
    }

    public List<RegistrationModel.RegistrationRow> getRegistrationsForSemester(String semester) {
        return model.getRegistrationsForSemester(semester);
    }

    public void onSemesterChanged(String semester) {
        view.updateTable(getRegistrationsForSemester(semester));
    }
} 