import javax.swing.SwingUtilities;
import view.LoginView;
import controller.LoginController;
import model.UserModel;
import view.DashboardView;
import controller.DashboardController;
import model.CourseModel;
import model.AcademicModel;
import controller.RegisterCoursesController;
import controller.CoursesListController;
import controller.AcademicReportController;
import controller.TimeTableController;
import java.util.Arrays;
import java.util.List;
import model.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UserModel userModel = new UserModel();
                CourseModel courseModel = new CourseModel();
                Connection conn = DatabaseConnection.getConnection();
                AcademicModel academicModel = new AcademicModel(conn, "04072313001");
                LoginController loginController = new LoginController(userModel, null, courseModel, academicModel);
                LoginView loginView = new LoginView(loginController);
                loginController.setLoginView(loginView);
                loginView.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                // Optionally show an error dialog here
            }
        });
    }
} 