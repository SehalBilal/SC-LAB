package dao;

import java.sql.*;
import java.util.*;
import model.UploadSchemeModel.CourseRow;

public class UploadSchemeDAO {
    private Connection conn;

    public UploadSchemeDAO(Connection conn) {
        this.conn = conn;
    }

    public List<CourseRow> getAllCourses() throws SQLException {
        List<CourseRow> list = new ArrayList<>();
        String sql = "SELECT code, title, pre_req, semester FROM scheme_of_study ORDER BY semester, code";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CourseRow(
                    rs.getString("code"),
                    rs.getString("title"),
                    rs.getString("pre_req"),
                    rs.getString("semester")
                ));
            }
        }
        return list;
    }

    public void addSchemeCourse(CourseRow course) throws SQLException {
        String sql = "INSERT INTO scheme_of_study (code, title, pre_req, semester) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.code);
            ps.setString(2, course.title);
            ps.setString(3, course.preReq);
            ps.setString(4, course.semester);
            ps.executeUpdate();
        }
    }

    public void editSchemeCourse(CourseRow oldCourse, CourseRow newCourse) throws SQLException {
        String sql = "UPDATE scheme_of_study SET code=?, title=?, pre_req=?, semester=? WHERE code=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newCourse.code);
            ps.setString(2, newCourse.title);
            ps.setString(3, newCourse.preReq);
            ps.setString(4, newCourse.semester);
            ps.setString(5, oldCourse.code);
            ps.executeUpdate();
        }
    }

    public void deleteSchemeCourse(CourseRow course) throws SQLException {
        String sql = "DELETE FROM scheme_of_study WHERE code=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.code);
            ps.executeUpdate();
        }
    }
} 