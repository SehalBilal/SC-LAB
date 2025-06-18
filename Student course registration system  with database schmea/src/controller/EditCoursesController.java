package controller;

import view.EditCoursesView;
import model.CourseModel;
import java.util.LinkedHashMap;
import java.util.List;

public class EditCoursesController {
    private EditCoursesView view;
    private CourseCoordinatorDashboardController parentController;
    private CourseModel courseModel;

    public EditCoursesController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        this.courseModel = parentController.getCourseModel();
        this.view = new EditCoursesView(this, courseModel.getOfferedCoursesBySemester());
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public void deleteCourse(String semester, int row) {
        courseModel.deleteCourse(semester, row);
        view.refresh(courseModel.getOfferedCoursesBySemester());
    }

    public void addCourse(String semester) {
        courseModel.addCourse(semester, new CourseModel.CourseInfo("", "", ""));
        view.refresh(courseModel.getOfferedCoursesBySemester());
    }

    public void addCourse(String semester, CourseModel.CourseInfo course) {
        courseModel.addCourse(semester, course);
        view.refresh(courseModel.getOfferedCoursesBySemester());
    }

    public void editCourse(String semester, int idx, CourseModel.CourseInfo course) {
        courseModel.editCourse(semester, idx, course);
        view.refresh(courseModel.getOfferedCoursesBySemester());
    }

    public void saveChanges(LinkedHashMap<String, List<CourseModel.CourseInfo>> map) {
        courseModel.setOfferedCoursesBySemester(map);
        view.refresh(courseModel.getOfferedCoursesBySemester());
    }
} 