package dao;

import model.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CourseCoordinatorDAO {
    public Map<String, Object> getCourseCoordinatorById(String id) throws SQLException {
        String query = "SELECT * FROM course_coordinators WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> cc = new HashMap<>();
                cc.put("id", rs.getString("id"));
                cc.put("name", rs.getString("name"));
                cc.put("email", rs.getString("email"));
                cc.put("password", rs.getString("password"));
                cc.put("department", rs.getString("department"));
                return cc;
            }
        }
        return null;
    }
} 