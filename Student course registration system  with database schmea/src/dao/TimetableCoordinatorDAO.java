package dao;

import model.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TimetableCoordinatorDAO {
    public Map<String, Object> getTimetableCoordinatorById(String id) throws SQLException {
        String query = "SELECT * FROM timetable_coordinators WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> tc = new HashMap<>();
                tc.put("id", rs.getString("id"));
                tc.put("name", rs.getString("name"));
                tc.put("email", rs.getString("email"));
                tc.put("password", rs.getString("password"));
                tc.put("department", rs.getString("department"));
                return tc;
            }
        }
        return null;
    }
} 