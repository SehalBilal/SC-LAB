package controller;

import view.OfferCoursesView;
import model.CourseModel;
import java.util.LinkedHashMap;
import java.util.List;

public class OfferCoursesController {
    private OfferCoursesView view;
    private CourseCoordinatorDashboardController parentController;
    private CourseModel courseModel;

    public OfferCoursesController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        this.courseModel = parentController.getCourseModel();
        LinkedHashMap<String, List<CourseModel.CourseInfo>> offeredCourses = courseModel.getOfferedCoursesBySemester();
        this.view = new OfferCoursesView(this, offeredCourses);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }
} 