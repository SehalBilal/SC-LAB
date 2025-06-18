package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrerequisiteDAO {
    private Connection conn;

    public PrerequisiteDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Integer> getPrerequisiteCourseIds(int courseId) throws SQLException {
        List<Integer> prereqIds = new ArrayList<>();
        String sql = "SELECT prereq_course_id FROM prerequisites WHERE course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prereqIds.add(rs.getInt("prereq_course_id"));
                }
            }
        }
        return prereqIds;
    }

    public void addPrerequisite(int courseId, int prereqCourseId) throws SQLException {
        String sql = "INSERT INTO prerequisites (course_id, prereq_course_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, prereqCourseId);
            ps.executeUpdate();
        }
    }

    public void deletePrerequisite(int courseId, int prereqCourseId) throws SQLException {
        String sql = "DELETE FROM prerequisites WHERE course_id = ? AND prereq_course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, prereqCourseId);
            ps.executeUpdate();
        }
    }
} 