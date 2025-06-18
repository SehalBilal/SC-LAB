package dao;

import model.DatabaseConnection;
import java.sql.*;
import java.util.*;
import model.CourseModel;

public class OfferedCoursesDAO {
    private Connection conn;
    private CourseDAO courseDAO;

    public OfferedCoursesDAO(Connection conn, CourseDAO courseDAO) {
        this.conn = conn;
        this.courseDAO = courseDAO;
    }

    public Map<String, List<CourseModel.CourseInfo>> getOfferedCoursesBySemester() throws SQLException {
        Map<String, List<CourseModel.CourseInfo>> offeredCourses = new LinkedHashMap<>();
        
        String sql = """
            SELECT
                c.id, c.code, c.name AS title, c.credits, c.department, c.semester AS course_semester,
                oc.faculty, oc.semester_label,
                (SELECT t.room FROM timetable t WHERE t.course_id = oc.course_id LIMIT 1) AS room
            FROM offered_courses oc
            JOIN courses c ON oc.course_id = c.id
            ORDER BY oc.semester_label, c.code
            """;
            
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String semesterLabel = rs.getString("semester_label");
                String code = rs.getString("code");
                String title = rs.getString("title");
                String faculty = rs.getString("faculty");
                int credits = rs.getInt("credits");
                String department = rs.getString("department");
                int courseSemester = rs.getInt("course_semester");
                String room = rs.getString("room"); // Get the room from the subquery
                
                offeredCourses.computeIfAbsent(semesterLabel, k -> new ArrayList<>())
                    .add(new CourseModel.CourseInfo(id, code, title, faculty, credits, department, courseSemester, room));
            }
        }
        
        return offeredCourses;
    }

    public void addOfferedCourse(String semester, CourseModel.CourseInfo course) throws SQLException {
        int courseId = course.id;
        if (courseId == 0) {
            courseId = courseDAO.getCourseIdByCode(course.code);
            if (courseId == -1) {
                throw new SQLException("Course code not found: " + course.code);
            }
        }

        String sql = "INSERT INTO offered_courses (course_id, semester_label, faculty) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, semester);
            ps.setString(3, course.faculty);
            ps.executeUpdate();
        }
    }

    public void editOfferedCourse(String semester, CourseModel.CourseInfo oldCourse, CourseModel.CourseInfo newCourse) throws SQLException {
        int oldCourseId = oldCourse.id;
        if (oldCourseId == 0) {
            oldCourseId = courseDAO.getCourseIdByCode(oldCourse.code);
            if (oldCourseId == -1) throw new SQLException("Old course code not found: " + oldCourse.code);
        }

        int newCourseId = newCourse.id;
        if (newCourseId == 0) {
            newCourseId = courseDAO.getCourseIdByCode(newCourse.code);
            if (newCourseId == -1) throw new SQLException("New course code not found: " + newCourse.code);
        }

        String sql = "UPDATE offered_courses SET course_id=?, faculty=? WHERE course_id=? AND semester_label=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCourseId);
            ps.setString(2, newCourse.faculty);
            ps.setInt(3, oldCourseId);
            ps.setString(4, semester);
            ps.executeUpdate();
        }
    }

    public void deleteOfferedCourse(String semester, CourseModel.CourseInfo course) throws SQLException {
        int courseId = course.id;
        if (courseId == 0) {
            courseId = courseDAO.getCourseIdByCode(course.code);
            if (courseId == -1) throw new SQLException("Course code not found: " + course.code);
        }

        String sql = "DELETE FROM offered_courses WHERE course_id=? AND semester_label=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, semester);
            ps.executeUpdate();
        }
    }
} 