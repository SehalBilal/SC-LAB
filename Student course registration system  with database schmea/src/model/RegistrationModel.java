package model;

import dao.RegistrationDAO;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class RegistrationModel {
    public static class RegistrationRow {
        public String regno, name, courseCode, courseTitle, section, status, faculty;
        public RegistrationRow(String regno, String name, String courseCode, String courseTitle, String section, String status, String faculty) {
            this.regno = regno;
            this.name = name;
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.section = section;
            this.status = status;
            this.faculty = faculty;
        }
    }

    private final RegistrationDAO registrationDAO;
    private String studentRegNo;

    public RegistrationModel() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            this.registrationDAO = new RegistrationDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection for RegistrationModel", e);
        }
        // The student registration number will be set by the controller
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public List<RegistrationRow> getRegistrationsForSemester(String semester) {
        if (studentRegNo == null || studentRegNo.isEmpty()) {
            System.err.println("Student registration number not set in RegistrationModel.");
            return Collections.emptyList();
        }

        // All semesters now fetch directly from registrations table
        return registrationDAO.getRegistrationsForSemester(studentRegNo, semester);
    }

    public List<String> getSemesters() {
        // Use the exact semester labels as in your SQL
        return Arrays.asList("Spring 2023", "Fall 2023", "Spring 2024", "Fall 2024");
    }
} 