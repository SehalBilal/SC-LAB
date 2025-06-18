package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import dao.StudentDAO;
import dao.CourseCoordinatorDAO;
import dao.TimetableCoordinatorDAO;
import dao.RegistrationDAO;
import dao.CourseDAO;

public class UserModel {
    private String name;
    private String regNo;
    private String program;
    private String semester;
    private Map<String, String> userCredentials;

    private RegistrationDAO registrationDAO;
    private CourseDAO courseDAO;

    public UserModel() {
        userCredentials = new HashMap<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.registrationDAO = new RegistrationDAO(conn);
            this.courseDAO = new CourseDAO();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to establish database connection for UserModel. DAOs will be null.");
            e.printStackTrace();
            this.registrationDAO = null;
            this.courseDAO = null;
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public boolean validateStudentLogin(String username, String password) {
        try {
            dao.StudentDAO studentDAO = new dao.StudentDAO();
            Map<String, Object> student = studentDAO.getStudentById(username);
            if (student != null && password.equals(student.get("password"))) {
                this.name = (String) student.get("name");
                this.regNo = (String) student.get("id");
                this.program = (String) student.get("department");
                this.semester = String.valueOf(student.get("semester"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validateCourseCoordinatorLogin(String username, String password) {
        try {
            dao.CourseCoordinatorDAO ccDAO = new dao.CourseCoordinatorDAO();
            Map<String, Object> cc = ccDAO.getCourseCoordinatorById(username);
            if (cc != null && password.equals(cc.get("password"))) {
                this.name = (String) cc.get("name");
                this.regNo = (String) cc.get("id");
                this.program = (String) cc.get("department");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validateTimetableCoordinatorLogin(String username, String password) {
        try {
            dao.TimetableCoordinatorDAO tcDAO = new dao.TimetableCoordinatorDAO();
            Map<String, Object> tc = tcDAO.getTimetableCoordinatorById(username);
            if (tc != null && password.equals(tc.get("password"))) {
                this.name = (String) tc.get("name");
                this.regNo = (String) tc.get("id");
                this.program = (String) tc.get("department");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerCourse(String studentId, String courseCode) {
        if (registrationDAO == null || courseDAO == null) {
            System.err.println("ERROR: DAOs not initialized in UserModel. Cannot register course.");
            return false;
        }
        try {
            int courseId = courseDAO.getCourseIdByCode(courseCode);
            if (courseId != -1) {
                registrationDAO.registerCourse(studentId, courseId, this.semester);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error registering course: " + e.getMessage());
        }
        return false;
    }

    public Set<String> getRegisteredCourses(String studentId) {
        if (registrationDAO == null) {
            System.err.println("ERROR: RegistrationDAO not initialized in UserModel. Cannot get registered courses.");
            return new HashSet<>();
        }
        Set<String> registeredCourseCodes = new HashSet<>();
        try {
            List<Map<String, Object>> registeredCourseDetails = registrationDAO.getRegisteredCoursesByStudentId(studentId);
            for (Map<String, Object> course : registeredCourseDetails) {
                registeredCourseCodes.add((String) course.get("code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving registered courses: " + e.getMessage());
        }
        return registeredCourseCodes;
    }

    public void clearRegisteredCourses(String studentId) {
        if (registrationDAO == null) {
            System.err.println("ERROR: RegistrationDAO not initialized in UserModel. Cannot clear registered courses.");
            return;
        }
        try {
            registrationDAO.clearAllRegistrations(studentId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error clearing registered courses: " + e.getMessage());
        }
    }

    public boolean canRegisterCourse(String username, String courseCode, model.AcademicModel academicModel) {
        return academicModel.allPrerequisitesMet(username, courseCode);
    }

    public void addRegisteredCourse(String studentId, CourseModel.CourseInfo courseInfo) {
        if (registrationDAO == null || courseDAO == null) {
            System.err.println("ERROR: DAOs not initialized in UserModel. Cannot add registered course to DB.");
            return;
        }
        try {
            int courseId = courseInfo.id;
            if (courseId == 0) {
                courseId = courseDAO.getCourseIdByCode(courseInfo.code);
            }
            if (courseId != -1) {
                registrationDAO.registerCourse(studentId, courseId, this.semester);
            } else {
                System.err.println("ERROR: Course not found for registration: " + courseInfo.code);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding registered course to DB: " + e.getMessage());
        }
    }

    public List<CourseModel.CourseInfo> getRegisteredCoursesDetails(String studentId) {
        List<CourseModel.CourseInfo> registeredCourseInfos = new ArrayList<>();
        if (registrationDAO == null || courseDAO == null) {
            System.err.println("ERROR: DAOs not initialized in UserModel. Cannot get registered courses details.");
            return registeredCourseInfos;
        }
        try {
            List<Map<String, Object>> registeredCourseMaps = registrationDAO.getRegisteredCoursesByStudentId(studentId);
            for (Map<String, Object> courseMap : registeredCourseMaps) {
                String code = (String) courseMap.get("code");
                String title = (String) courseMap.get("title");
                String faculty = (String) courseMap.get("faculty");
                String room = (String) courseMap.get("room");
                int credits = (int) courseMap.get("credits");

                Map<String, Object> fullCourseDetails = courseDAO.getCourseDetailsByCode(code);
                String department = (String) fullCourseDetails.get("department");
                int semester = (int) fullCourseDetails.get("semester");
                int id = (int) fullCourseDetails.get("id");

                registeredCourseInfos.add(new CourseModel.CourseInfo(id, code, title, faculty, credits, department, semester, room));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving registered courses details: " + e.getMessage());
        }
        return registeredCourseInfos;
    }
} 