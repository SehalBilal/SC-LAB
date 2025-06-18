package controller;

import view.UploadSchemeView;
import model.UploadSchemeModel;
import model.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class UploadSchemeController {
    private UploadSchemeView view;
    private CourseCoordinatorDashboardController parentController;
    private UploadSchemeModel model;

    public UploadSchemeController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.model = new UploadSchemeModel(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            this.model = null;
        }
        this.view = new UploadSchemeView(this, model);
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
} 