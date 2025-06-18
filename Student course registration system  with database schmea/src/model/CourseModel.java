package model;

import java.util.*;
import dao.OfferedCoursesDAO;
import dao.CourseDAO;
import java.sql.Connection;
import java.sql.SQLException;
import dao.TimetableDAO;
import dao.RegistrationDAO;

public class CourseModel {
    private Map<String, Map<String, Object>> courses;
    private Map<String, String[][]> customStudentTimetables = new HashMap<>();
    private OfferedCoursesDAO offeredCoursesDAO;
    private CourseDAO courseDAO;
    private TimetableDAO timetableDAO;
    private RegistrationDAO registrationDAO;

    public static final String[] DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    public static final String[] ROOMS = {"201", "217", "235", "F8", "F6", "Lab", "BS Lab"};
    public static final String[] SLOTS = {"9:05 - 10:35", "10:45 - 12:15", "12:25 - 13:55", "14:15 - 15:45", "16:00 - 17:30"};

    public CourseModel() {
        courses = new HashMap<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            courseDAO = new CourseDAO();
            offeredCoursesDAO = new OfferedCoursesDAO(conn, courseDAO);
            timetableDAO = new TimetableDAO(conn, courseDAO);
            registrationDAO = new RegistrationDAO(conn);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to establish database connection for CourseModel. DAOs will be null.");
            e.printStackTrace();
        }
    }

    private void addCourse(String code, String name, int credits, String teacher, String room) {
        Map<String, Object> course = new HashMap<>();
        course.put("name", name);
        course.put("credits", credits);
        course.put("teacher", teacher);
        course.put("room", room);
        courses.put(code, course);
    }

    public Map<String, Object> getCourseDetails(String courseCode) throws SQLException {
        if (courseDAO == null) throw new SQLException("CourseDAO not initialized.");
        return courseDAO.getCourseDetailsByCode(courseCode);
    }

    public boolean isValidCourse(String courseCode) throws SQLException {
        if (courseDAO == null) throw new SQLException("CourseDAO not initialized.");
        return courseDAO.getCourseIdByCode(courseCode) != -1;
    }

    public List<Map<String, Object>> getAllCourses() {
        List<Map<String, Object>> courseList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : courses.entrySet()) {
            Map<String, Object> course = new HashMap<>(entry.getValue());
            course.put("code", entry.getKey());
            courseList.add(course);
        }
        return courseList;
    }

    public LinkedHashMap<String, Map<String, List<String>>> getStudentTimetableMatrix(String studentId) {
        // Always return a full matrix for the timetable view
        LinkedHashMap<String, Map<String, List<String>>> matrix = new LinkedHashMap<>();
        for (String day : DAYS) {
            matrix.put(day, new LinkedHashMap<>());
            for (String room : ROOMS) {
                List<String> slots = new ArrayList<>();
                for (String slot : SLOTS) {
                    slots.add("");
                }
                matrix.get(day).put(room, slots);
            }
        }
        // Fill with actual data from DB
        if (timetableDAO != null) {
            try {
                Map<String, Map<String, List<String>>> dbData = timetableDAO.getFullTimetableBySlots(DAYS, ROOMS, SLOTS);
                for (String day : dbData.keySet()) {
                    for (String room : dbData.get(day).keySet()) {
                        List<String> slotList = dbData.get(day).get(room);
                        matrix.get(day).put(room, slotList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return matrix;
    }

    public String[] getAllDays() { return DAYS; }
    public String[] getAllRooms() { return ROOMS; }
    public String[] getAllSlots() { return SLOTS; }

    public static class CourseInfo {
        public int id;
        public String code, title, faculty;
        public int credits;
        public String department;
        public int semester;
        public String room;

        public CourseInfo(int id, String code, String title, String faculty, int credits, String department, int semester, String room) {
            this.id = id;
            this.code = code;
            this.title = title;
            this.faculty = faculty;
            this.credits = credits;
            this.department = department;
            this.semester = semester;
            this.room = room;
        }

        public CourseInfo(String code, String title, String faculty) {
            this(0, code, title, faculty, 0, null, 0, null);
        }

        @Override
        public String toString() {
            return code + " - " + title + " (" + faculty + ")";
        }
    }

    public LinkedHashMap<String, List<CourseInfo>> getOfferedCoursesBySemester() {
        LinkedHashMap<String, List<CourseInfo>> map = new LinkedHashMap<>();
        if (offeredCoursesDAO == null) {
            System.err.println("ERROR: OfferedCoursesDAO is not initialized. Cannot fetch offered courses. Check database connection.");
            return map;
        }
        try {
            Map<String, List<CourseInfo>> daoMap = offeredCoursesDAO.getOfferedCoursesBySemester();
            for (Map.Entry<String, List<CourseInfo>> entry : daoMap.entrySet()) {
                List<CourseInfo> list = new ArrayList<>();
                for (CourseInfo ci : entry.getValue()) {
                    list.add(new CourseInfo(ci.id, ci.code, ci.title, ci.faculty, ci.credits, ci.department, ci.semester, ci.room));
                }
                map.put(entry.getKey(), list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void setOfferedCoursesBySemester(LinkedHashMap<String, List<CourseInfo>> map) {
        // This method could be implemented to handle bulk updates to the database if needed.
        // For now, individual add/edit/delete operations are handled separately.
    }

    public void addCourse(String semester, CourseInfo course) {
        if (offeredCoursesDAO == null) {
            System.err.println("ERROR: OfferedCoursesDAO is not initialized. Cannot add course.");
            return;
        }
        try {
            offeredCoursesDAO.addOfferedCourse(semester, course);
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void editCourse(String semester, int idx, CourseInfo course) {
        if (offeredCoursesDAO == null) {
            System.err.println("ERROR: OfferedCoursesDAO is not initialized. Cannot edit course.");
            return;
        }
        List<CourseInfo> list = getOfferedCoursesBySemester().get(semester);
        if (list == null || idx < 0 || idx >= list.size()) {
            System.err.println("Error: Course not found for editing at semester " + semester + ", index " + idx);
            return;
        }
        CourseInfo oldCourse = list.get(idx);
        try {
            offeredCoursesDAO.editOfferedCourse(semester, oldCourse, course);
        } catch (SQLException e) {
            System.err.println("Error editing course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteCourse(String semester, int idx) {
        if (offeredCoursesDAO == null) {
            System.err.println("ERROR: OfferedCoursesDAO is not initialized. Cannot delete course.");
            return;
        }
        List<CourseInfo> list = getOfferedCoursesBySemester().get(semester);
        if (list == null || idx < 0 || idx >= list.size()) {
            System.err.println("Error: Course not found for deletion at semester " + semester + ", index " + idx);
            return;
        }
        CourseInfo course = list.get(idx);
        try {
            offeredCoursesDAO.deleteOfferedCourse(semester, course);
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CourseInfo> getAllCoursesInfo() {
        List<CourseInfo> all = new ArrayList<>();
        for (List<CourseInfo> list : getOfferedCoursesBySemester().values()) {
            all.addAll(list);
        }
        return all;
    }

    public List<String> getRegisteredCourseCodes(String studentId, String semesterLabel) {
        if (registrationDAO == null) return new ArrayList<>();
        try {
            return registrationDAO.getRegisteredCourseCodes(studentId, semesterLabel);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Conflict> checkConflicts(String studentId, String semesterLabel) {
        List<String> registeredCourses = getRegisteredCourseCodes(studentId, semesterLabel);
        System.out.println("Registered courses for " + studentId + " in " + semesterLabel + ": " + registeredCourses);
        LinkedHashMap<String, Map<String, List<String>>> timetable = getStudentTimetableMatrix(studentId);
        for (String day : DAYS) {
            for (String room : ROOMS) {
                List<String> slots = timetable.get(day).get(room);
                for (int i = 0; i < SLOTS.length; i++) {
                    String code = slots.get(i);
                    if (code != null && !code.isEmpty()) {
                        System.out.println("Timetable: " + day + " " + room + " " + SLOTS[i] + " = " + code);
                    }
                }
            }
        }
        List<Conflict> conflicts = new ArrayList<>();
        for (String day : DAYS) {
            for (int slotIdx = 0; slotIdx < SLOTS.length; slotIdx++) {
                List<String> conflictRooms = new ArrayList<>();
                List<String> conflictCourses = new ArrayList<>();
                for (String room : ROOMS) {
                    String code = timetable.get(day).get(room).get(slotIdx);
                    if (code != null && !code.isEmpty() && registeredCourses.contains(code)) {
                        conflictRooms.add(room);
                        conflictCourses.add(code);
                    }
                }
                if (conflictCourses.size() > 1) {
                    for (String room : conflictRooms) {
                        conflicts.add(new model.Conflict(day, room, slotIdx, conflictCourses.toArray(new String[0])));
                    }
                }
            }
        }
        return conflicts;
    }

    public void saveCustomTimetable(String studentId, Map<String, Map<String, List<String>>> newTimetable) {
        // For simplicity, clear and re-insert timetable for now (could be optimized)
        if (timetableDAO == null) return;
        try {
            // Remove all timetable entries (or only for this student if personalized)
            // Here, we assume a global timetable, so we clear and re-insert
            // (If you want per-student timetables, you need to add a student_id column to the timetable table)
            // For now, just update the global timetable
            // 1. Delete all
            // 2. Insert new
            // (You may want to backup or version this in a real system)
            // Not implemented: delete all, as it may affect all students
            // Instead, update only changed slots
            LinkedHashMap<String, Map<String, List<String>>> current = getStudentTimetableMatrix(studentId);
            for (String day : DAYS) {
                for (String room : ROOMS) {
                    List<String> oldSlots = current.get(day).get(room);
                    List<String> newSlots = newTimetable.get(day).get(room);
                    for (int i = 0; i < SLOTS.length; i++) {
                        String oldCode = oldSlots.get(i);
                        String newCode = newSlots.get(i);
                        if (!Objects.equals(oldCode, newCode)) {
                            // Update DB: remove old, add new
                            if (oldCode != null && !oldCode.isEmpty()) {
                                int oldCourseId = courseDAO.getCourseIdByCode(oldCode);
                                timetableDAO.deleteTimetableEntry(oldCourseId, day, SLOTS[i], room);
                            }
                            if (newCode != null && !newCode.isEmpty()) {
                                int newCourseId = courseDAO.getCourseIdByCode(newCode);
                                timetableDAO.addTimetableEntry(newCourseId, day, SLOTS[i], room);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}