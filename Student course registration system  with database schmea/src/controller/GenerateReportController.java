package controller;

import view.GenerateReportView;
import model.AcademicModel;
import model.DatabaseConnection;
import java.sql.Connection;
import dao.AcademicReportDAO;

public class GenerateReportController {
    private GenerateReportView view;
    private CourseCoordinatorDashboardController parentController;

    public GenerateReportController(CourseCoordinatorDashboardController parentController) {
        this.parentController = parentController;
        this.view = new GenerateReportView(this);
    }

    public void showView() {
        view.setVisible(true);
    }

    public void backToDashboard() {
        parentController.showView();
    }

    public void checkAndShowReport(String regno) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            AcademicReportDAO dao = new AcademicReportDAO(conn);
            if (!dao.studentExists(regno)) {
                view.showError("No data found for this regno!");
                return;
            }
            AcademicModel model = new AcademicModel(conn, regno);
            if (model.getSemesters().isEmpty()) {
                view.showError("No academic records found for this regno!");
                return;
            }
            view.showReport(regno, model);
        } catch (Exception e) {
            view.showError("Database error: " + e.getMessage());
        }
    }
} 