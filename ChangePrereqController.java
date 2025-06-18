package controller;

import view.ChangePrereqView;
import model.UploadSchemeModel;
import model.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ChangePrereqController {
    private ChangePrereqView view;
    private CourseCoordinatorDashboardController parentController;
    private UploadSchemeModel model;

    public ChangePrereqController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.model = new UploadSchemeModel(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error, maybe show a message to the user
            this.model = null; // Set model to null to prevent NullPointerException later
        }
        this.view = new ChangePrereqView(this, model);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public UploadSchemeModel getModel() {
        return model;
    }

    public void addCourse(UploadSchemeModel.CourseRow row) {
        if (model != null) {
            model.addCourse(row);
            view.refreshTable();
        }
    }

    public void editCourse(int idx, UploadSchemeModel.CourseRow row) {
        if (model != null) {
            model.editCourse(idx, row);
            view.refreshTable();
        }
    }

    public void deleteCourse(int idx) {
        if (model != null) {
            model.deleteCourse(idx);
            view.refreshTable();
        }
    }

    public void saveChanges(List<UploadSchemeModel.CourseRow> newCourses) {
        if (model != null) {
            try {
                model.saveAllCourses(newCourses);
                // No need to refreshTable here, as saveAllCourses reloads the model and then the view will refresh
                // based on its getModel().getCourses() call.
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle error, show message to user
                System.err.println("ERROR: Failed to save changes to database: " + e.getMessage());
            }
        }
    }
} 