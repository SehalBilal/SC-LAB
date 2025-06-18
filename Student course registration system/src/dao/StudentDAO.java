package dao;

import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class StudentDAO {
    
    public Map<String, Object> getStudentById(String id) throws SQLException {
        String query = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("id", rs.getString("id"));
                student.put("name", rs.getString("name"));
                student.put("email", rs.getString("email"));
                student.put("password", rs.getString("password"));
                student.put("department", rs.getString("department"));
                student.put("semester", rs.getInt("semester"));
                return student;
            }
        }
        return null;
    }
    
    public Map<String, Object> getStudentByEmail(String email) throws SQLException {
        String query = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("id", rs.getString("id"));
                student.put("name", rs.getString("name"));
                student.put("email", rs.getString("email"));
                student.put("password", rs.getString("password"));
                student.put("department", rs.getString("department"));
                student.put("semester", rs.getInt("semester"));
                return student;
            }
        }
        return null;
    }
    
    public List<Map<String, Object>> getAllStudents() throws SQLException {
        List<Map<String, Object>> students = new ArrayList<>();
        String query = "SELECT * FROM students";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("id", rs.getString("id"));
                student.put("name", rs.getString("name"));
                student.put("email", rs.getString("email"));
                student.put("password", rs.getString("password"));
                student.put("department", rs.getString("department"));
                student.put("semester", rs.getInt("semester"));
                students.add(student);
            }
        }
        return students;
    }
    
    public void addStudent(Map<String, Object> student) throws SQLException {
        String query = "INSERT INTO students (name, email, password, department, semester) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, (String) student.get("name"));
            stmt.setString(2, (String) student.get("email"));
            stmt.setString(3, (String) student.get("password"));
            stmt.setString(4, (String) student.get("department"));
            stmt.setInt(5, (int) student.get("semester"));
            
            stmt.executeUpdate();
        }
    }
} 