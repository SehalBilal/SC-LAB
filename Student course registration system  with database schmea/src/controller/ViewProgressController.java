package controller;

import view.ViewProgressView;
import model.AcademicModel;
import model.DatabaseConnection;
import java.sql.Connection;
import dao.AcademicReportDAO;
import java.sql.SQLException;

public class ViewProgressController {
    private ViewProgressView view;
    private CourseCoordinatorDashboardController parentController;

    public ViewProgressController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        this.view = new ViewProgressView(this);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public void checkAndShowProgress(String regno) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            AcademicReportDAO dao = new AcademicReportDAO(conn);
            if (!dao.studentExists(regno)) {
                view.showError("No record found for student regno: " + regno);
                return;
            }
            AcademicModel academicModel = new AcademicModel(conn, regno);
            if (academicModel.getSemesters().isEmpty()) {
                view.showError("No academic records found for this regno!");
                return;
            }
            view.showAcademicReport(regno, academicModel);
        } catch (SQLException e) {
            view.showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 