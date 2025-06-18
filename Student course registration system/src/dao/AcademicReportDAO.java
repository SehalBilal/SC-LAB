package dao;

import java.sql.*;
import java.util.*;

public class AcademicReportDAO {
    private Connection conn;

    public AcademicReportDAO(Connection conn) {
        this.conn = conn;
    }

    // Get all semesters for a student (from grades table)
    public List<String> getSemestersForStudent(String studentId) throws SQLException {
        List<String> semesters = new ArrayList<>();
        String sql = "SELECT DISTINCT semester_label FROM grades WHERE student_id = ? " +
                     "ORDER BY CAST(SUBSTRING_INDEX(semester_label, ' ', -1) AS UNSIGNED), " +
                     "CASE WHEN semester_label LIKE 'Spring%' THEN 1 WHEN semester_label LIKE 'Fall%' THEN 2 ELSE 3 END";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                semesters.add(rs.getString("semester_label"));
            }
        }
        return semesters;
    }

    // Get all courses, grades, etc. for a student in a semester
    public List<Map<String, Object>> getAcademicReport(String studentId, String semesterLabel) throws SQLException {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = """
            SELECT c.code, c.name, c.credits, g.marks, g.grade
            FROM grades g
            JOIN courses c ON g.course_id = c.id
            WHERE g.student_id = ? AND g.semester_label = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, semesterLabel);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("code", rs.getString("code"));
                row.put("name", rs.getString("name"));
                row.put("credits", rs.getInt("credits"));
                row.put("marks", rs.getInt("marks"));
                row.put("grade", rs.getString("grade"));
                report.add(row);
            }
        }
        return report;
    }

    // Check if a student exists in the database
    public boolean studentExists(String studentId) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
} 