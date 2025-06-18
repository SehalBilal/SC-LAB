package dao;

import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CourseDAO {
    
    public List<Map<String, Object>> getCoursesByDepartment(String department) throws SQLException {
        List<Map<String, Object>> courses = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE department = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> course = new HashMap<>();
                course.put("id", rs.getInt("id"));
                course.put("code", rs.getString("code"));
                course.put("name", rs.getString("name"));
                course.put("credits", rs.getInt("credits"));
                course.put("department", rs.getString("department"));
                course.put("semester", rs.getInt("semester"));
                courses.add(course);
            }
        }
        return courses;
    }
    
    public List<Map<String, Object>> getAvailableCourses(String studentId) throws SQLException {
        List<Map<String, Object>> courses = new ArrayList<>();
        String query = "SELECT c.* FROM courses c WHERE c.department = (SELECT department FROM students WHERE id = ?) AND c.semester = (SELECT semester FROM students WHERE id = ?) AND c.id NOT IN (SELECT course_id FROM registrations WHERE student_id = ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, studentId);
            stmt.setString(3, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> course = new HashMap<>();
                course.put("id", rs.getInt("id"));
                course.put("code", rs.getString("code"));
                course.put("name", rs.getString("name"));
                course.put("credits", rs.getInt("credits"));
                course.put("department", rs.getString("department"));
                course.put("semester", rs.getInt("semester"));
                courses.add(course);
            }
        }
        return courses;
    }
    
    public void addCourse(Map<String, Object> course) throws SQLException {
        String query = "INSERT INTO courses (code, name, credits, department, semester) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, (String) course.get("code"));
            stmt.setString(2, (String) course.get("name"));
            stmt.setInt(3, (Integer) course.get("credits"));
            stmt.setString(4, (String) course.get("department"));
            stmt.setInt(5, (Integer) course.get("semester"));
            
            stmt.executeUpdate();
        }
    }

    public int getCourseIdByCode(String courseCode) throws SQLException {
        String query = "SELECT id FROM courses WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Or throw an exception if course not found
    }

    public String getCourseCodeById(int courseId) throws SQLException {
        String query = "SELECT code FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("code");
                }
            }
        }
        return null;
    }

    public Map<String, Object> getCourseDetailsById(int courseId) throws SQLException {
        String query = "SELECT id, code, name, credits, department, semester FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> course = new HashMap<>();
                    course.put("id", rs.getInt("id"));
                    course.put("code", rs.getString("code"));
                    course.put("name", rs.getString("name"));
                    course.put("credits", rs.getInt("credits"));
                    course.put("department", rs.getString("department"));
                    course.put("semester", rs.getInt("semester"));
                    return course;
                }
            }
        }
        return null;
    }

    public Map<String, Object> getCourseDetailsByCode(String courseCode) throws SQLException {
        String query = "SELECT id, code, name, credits, department, semester FROM courses WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> course = new HashMap<>();
                    course.put("id", rs.getInt("id"));
                    course.put("code", rs.getString("code"));
                    course.put("name", rs.getString("name"));
                    course.put("credits", rs.getInt("credits"));
                    course.put("department", rs.getString("department"));
                    course.put("semester", rs.getInt("semester"));
                    return course;
                }
            }
        }
        return null;
    }
} 