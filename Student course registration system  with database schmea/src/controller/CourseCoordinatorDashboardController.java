package controller;

import view.CourseCoordinatorDashboardView;
import model.UserModel;
import model.CourseModel;
import model.AcademicModel;

public class CourseCoordinatorDashboardController {
    private UserModel userModel;
    private CourseCoordinatorDashboardView view;
    private String username;
    private CourseModel courseModel;
    private AcademicModel academicModel;

    public CourseCoordinatorDashboardController(String username, UserModel userModel, CourseModel courseModel, AcademicModel academicModel) {
        this.username = username;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.academicModel = academicModel;
        this.view = new CourseCoordinatorDashboardView(this);
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
            case 0: new OfferCoursesController(this).showView(); break;
            case 1: new UploadSchemeController(this).showView(); break;
            case 2: new EditCoursesController(this).showView(); break;
            case 3: new ViewRegistrationsController(this).showView(); break;
            case 4: new ViewProgressController(this).showView(); break;
            case 5: new GenerateReportController(this).showView(); break;
            case 6: new ChangePrereqController(this).showView(); break;
            case 7: // Logout
                new controller.LoginController(userModel, null, courseModel, academicModel).setLoginView(new view.LoginView(new controller.LoginController(userModel, null, courseModel, academicModel)));
                new view.LoginView(new controller.LoginController(userModel, null, courseModel, academicModel)).setVisible(true);
                break;
        }
    }

    public CourseModel getCourseModel() {
        return courseModel;
    }

    // Add navigation methods for each use case as needed
} 