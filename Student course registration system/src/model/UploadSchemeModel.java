package model;

import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import dao.UploadSchemeDAO;

public class UploadSchemeModel {
    public static class CourseRow {
        public String code;
        public String title;
        public String preReq;
        public String semester;
        public CourseRow(String code, String title, String preReq, String semester) {
            this.code = code;
            this.title = title;
            this.preReq = preReq;
            this.semester = semester;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CourseRow courseRow = (CourseRow) o;
            return Objects.equals(code, courseRow.code) &&
                   Objects.equals(title, courseRow.title) &&
                   Objects.equals(preReq, courseRow.preReq) &&
                   Objects.equals(semester, courseRow.semester);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, title, preReq, semester);
        }
    }

    private List<CourseRow> courses; // This will now represent the in-memory state, mirroring the UI
    private UploadSchemeDAO dao;

    public UploadSchemeModel(Connection conn) {
        this.dao = new UploadSchemeDAO(conn);
        loadCourses();
    }

    private void loadCourses() {
        try {
            this.courses = dao.getAllCourses();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not load scheme of study from database.");
            this.courses = new ArrayList<>();
        }
    }

    public List<CourseRow> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseRow> newCourses) {
        // This method is now used to update the in-memory list only, before a bulk save.
        this.courses = newCourses;
    }

    public void addCourse(CourseRow row) {
        // This only modifies the in-memory list. Persistence happens on bulk save.
        this.courses.add(row);
    }

    public void editCourse(int idx, CourseRow newRow) {
        // This only modifies the in-memory list. Persistence happens on bulk save.
        if (idx >= 0 && idx < courses.size()) {
            this.courses.set(idx, newRow);
        }
    }

    public void deleteCourse(int idx) {
        // This only modifies the in-memory list. Persistence happens on bulk save.
        if (idx >= 0 && idx < courses.size()) {
            this.courses.remove(idx);
        }
    }

    public void saveAllCourses(List<CourseRow> newCoursesFromUI) throws SQLException {
        // Step 1: Get current courses from the database
        List<CourseRow> currentCoursesInDb = dao.getAllCourses();
        
        // Step 2: Create maps for efficient lookup
        // Using code as key. Assuming code is unique for a course. If not, a composite key (e.g., code+semester) would be needed.
        Map<String, CourseRow> dbCoursesMap = new HashMap<>();
        for (CourseRow course : currentCoursesInDb) {
            dbCoursesMap.put(course.code, course);
        }

        // Step 3: Iterate through UI courses to identify adds and updates
        for (CourseRow uiCourse : newCoursesFromUI) {
            CourseRow dbCourse = dbCoursesMap.get(uiCourse.code);
            if (dbCourse != null) {
                // Course exists in DB, check if it needs update
                if (!uiCourse.title.equals(dbCourse.title) || 
                    !Objects.equals(uiCourse.preReq, dbCourse.preReq) || 
                    !uiCourse.semester.equals(dbCourse.semester)) {
                    dao.editSchemeCourse(dbCourse, uiCourse);
                }
                // Remove from map, so remaining entries in dbCoursesMap are truly deleted courses
                dbCoursesMap.remove(uiCourse.code);
            } else {
                // This is a new course not found in DB, so add it
                dao.addSchemeCourse(uiCourse);
            }
        }
        
        // Step 4: Identify courses to delete (present in DB but not in newCoursesFromUI)
        for (CourseRow courseToDelete : dbCoursesMap.values()) {
            dao.deleteSchemeCourse(courseToDelete);
        }

        loadCourses(); // Reload the model's internal list from the database to reflect all saved changes
    }
} 