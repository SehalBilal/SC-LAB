package dao;

import model.RegistrationModel;
import model.RegistrationModel.RegistrationRow;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Date;

public class RegistrationDAO {
    private Connection conn;

    public RegistrationDAO(Connection conn) {
        this.conn = conn;
    }

    public void registerCourse(String studentId, int courseId, String semesterLabel) throws SQLException {
        String sql = "INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, new Date(System.currentTimeMillis()));
            ps.setString(4, "REGISTERED"); // Default status to REGISTERED
            ps.setString(5, semesterLabel); // Insert semester_label
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> getRegisteredCoursesByStudentId(String studentId) throws SQLException {
        List<Map<String, Object>> registeredCourses = new ArrayList<>();
        String sql = "SELECT DISTINCT c.code, c.name AS title, oc.faculty, t.room, c.credits, r.status " +
                     "FROM registrations r " +
                     "JOIN courses c ON r.course_id = c.id " +
                     "LEFT JOIN offered_courses oc ON r.course_id = oc.course_id " + 
                     "LEFT JOIN timetable t ON r.course_id = t.course_id " +
                     "WHERE r.student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> course = new HashMap<>();
                    course.put("code", rs.getString("code"));
                    course.put("title", rs.getString("title"));
                    course.put("faculty", rs.getString("faculty"));
                    course.put("room", rs.getString("room"));
                    course.put("credits", rs.getInt("credits"));
                    course.put("status", rs.getString("status"));
                    registeredCourses.add(course);
                }
            }
        }
        return registeredCourses;
    }

    public void deleteRegistration(String studentId, int courseId) throws SQLException {
        String sql = "DELETE FROM registrations WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        }
    }

    public void clearAllRegistrations(String studentId) throws SQLException {
        String sql = "DELETE FROM registrations WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
        }
    }

    public List<RegistrationRow> getRegistrationsForSemester(String studentRegNo, String semester) {
        List<RegistrationRow> registrations = new ArrayList<>();
        String query = "SELECT " +
                       "s.id AS reg_no, s.name AS student_name, " +
                       "c.code AS course_code, c.name AS course_title, " +
                       "COALESCE(oc.section, 'N/A') AS section, " + 
                       "r.status AS registration_status, " +
                       "COALESCE(oc.faculty, 'N/A') AS faculty_name " +
                       "FROM registrations r " +
                       "JOIN students s ON r.student_id = s.id " +
                       "JOIN courses c ON r.course_id = c.id " +
                       "LEFT JOIN offered_courses oc ON c.id = oc.course_id AND r.semester_label = oc.semester_label " +
                       "WHERE s.id = ? AND r.semester_label = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, studentRegNo);
            preparedStatement.setString(2, semester);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                registrations.add(new RegistrationRow(
                    resultSet.getString("reg_no"),
                    resultSet.getString("student_name"),
                    resultSet.getString("course_code"),
                    resultSet.getString("course_title"),
                    resultSet.getString("section"),
                    resultSet.getString("registration_status"),
                    resultSet.getString("faculty_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    public List<String> getRegisteredCourseCodes(String studentId, String semesterLabel) throws SQLException {
        List<String> codes = new ArrayList<>();
        String sql = "SELECT c.code FROM registrations r JOIN courses c ON r.course_id = c.id WHERE r.student_id = ? AND r.semester_label = ? AND r.status = 'REGISTERED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, semesterLabel);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                codes.add(rs.getString("code"));
            }
        }
        return codes;
    }
} 