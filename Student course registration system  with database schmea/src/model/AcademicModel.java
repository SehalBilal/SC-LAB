package model;

import java.util.*;
import dao.AcademicReportDAO;
import dao.PrerequisiteDAO;
import dao.CourseDAO;
import java.sql.Connection;
import java.sql.SQLException;

public class AcademicModel {
    public static class Subject {
        String code, title;
        int creditHours;
        double marks;
        public Subject(String code, String title, int creditHours, double marks) {
            this.code = code;
            this.title = title;
            this.creditHours = creditHours;
            this.marks = marks;
        }
    }

    private List<String> semesters;
    private Map<String, List<Subject>> semesterSubjects;
    private Map<String, List<Map<String, Object>>> studentCourses;
    private Map<String, Map<String, Double>> studentStats;
    private static final String STUDENT_USERNAME = "04072313001";
    private Map<String, Map<String, Map<String, Object>>> studentSemesterRecords;
    private Map<String, List<String>> studentSemesters;
    private AcademicReportDAO reportDAO;
    private PrerequisiteDAO prereqDAO;
    private CourseDAO courseDAO;
    private String studentId;

    public AcademicModel(Connection conn, String studentId) {
        this.reportDAO = new AcademicReportDAO(conn);
        this.prereqDAO = new PrerequisiteDAO(conn);
        this.courseDAO = new CourseDAO();
        this.studentId = studentId;
        semesters = Arrays.asList("Spring 2023", "Fall 2023", "Spring 2024", "Fall 2024");
        semesterSubjects = new HashMap<>();
        studentCourses = new HashMap<>();
        studentStats = new HashMap<>();
        studentSemesterRecords = new HashMap<>();
        studentSemesters = new HashMap<>();
    }

    public List<String> getSemesters() {
        try {
            return reportDAO.getSemestersForStudent(studentId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not retrieve semesters for student " + studentId);
            return Collections.emptyList();
        }
    }

    public List<Subject> getSubjectsForSemester(String semester) {
        return semesterSubjects.get(semester);
    }

    public static double calculateSubjectGPA(double marksObtained) {
        if (marksObtained >= 80) return 4.0;
        else if (marksObtained >= 76) return 3.8;
        else if (marksObtained >= 72) return 3.5;
        else if (marksObtained >= 68) return 3.0;
        else if (marksObtained >= 64) return 2.8;
        else if (marksObtained >= 60) return 2.5;
        else if (marksObtained >= 55) return 2.0;
        else if (marksObtained >= 50) return 1.0;
        else return 0.0;
    }

    public static String getGrade(double mark) {
        if (mark >= 80) return "A";
        else if (mark >= 76) return "A-";
        else if (mark >= 72) return "B+";
        else if (mark >= 68) return "B";
        else if (mark >= 64) return "B-";
        else if (mark >= 60) return "C+";
        else if (mark >= 55) return "C";
        else if (mark >= 50) return "D";
        else return "F";
    }

    public double calculateSemesterGPA(String semesterLabel) {
        List<Map<String, Object>> courses = getSemesterReport(semesterLabel);
        double totalPoints = 0;
        int totalCredits = 0;
        for (Map<String, Object> course : courses) {
            int credits = (int) course.get("credits");
            String grade = (String) course.get("grade");
            double gp = getGradePoint(grade);
            totalPoints += gp * credits;
            totalCredits += credits;
        }
        return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }

    public double calculateCGPA() {
        List<String> semesters = getSemesters();
        double totalPoints = 0;
        int totalCredits = 0;
        for (String sem : semesters) {
            List<Map<String, Object>> courses = getSemesterReport(sem);
            for (Map<String, Object> course : courses) {
                int credits = (int) course.get("credits");
                String grade = (String) course.get("grade");
                double gp = getGradePoint(grade);
                totalPoints += gp * credits;
                totalCredits += credits;
            }
        }
        return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }

    public List<Map<String, Object>> getStudentCourses(String username) {
        return studentCourses.getOrDefault(username, new ArrayList<>());
    }

    public Map<String, Double> getStudentStats(String username) {
        return studentStats.getOrDefault(username, new HashMap<>());
    }

    public Map<String, Map<String, Object>> getSemesterRecords(String username, String semester) {
        try {
            List<Map<String, Object>> report = reportDAO.getAcademicReport(username, semester);
            Map<String, Map<String, Object>> result = new LinkedHashMap<>();
            for (Map<String, Object> row : report) {
                result.put((String) row.get("code"), row);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: Could not retrieve academic report for student " + username + " in semester " + semester);
            return new LinkedHashMap<>();
        }
    }

    public boolean allPrerequisitesMet(String studentUsername, String courseCode) {
        try {
            int courseId = courseDAO.getCourseIdByCode(courseCode);
            if (courseId == -1) {
                System.err.println("ERROR: Course not found in database for prerequisite check: " + courseCode);
                return false; 
            }

            List<Integer> prereqIds = prereqDAO.getPrerequisiteCourseIds(courseId);
            if (prereqIds.isEmpty()) {
                return true;
            }

            for (int prereqId : prereqIds) {
                String prereqCode = courseDAO.getCourseCodeById(prereqId);
                if (prereqCode == null) {
                    System.err.println("ERROR: Prerequisite course ID " + prereqId + " not found in database.");
                    return false;
                }
                if (!isCoursePassed(studentUsername, prereqCode)) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking if all prerequisites are met for " + courseCode + " for student " + studentUsername);
            return false;
        }
    }

    public boolean isCoursePassed(String username, String courseCode) {
        try {
            List<String> semesters = reportDAO.getSemestersForStudent(username);
            for (String sem : semesters) {
                Map<String, Map<String, Object>> recs = getSemesterRecords(username, sem);
                if (recs != null && recs.containsKey(courseCode)) {
                    return (int) recs.get(courseCode).get("marks") >= 50;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking if course " + courseCode + " is passed for student " + username);
            return false;
        }
    }

    public boolean isCourseFailed(String username, String courseCode) {
        try {
            List<String> semesters = reportDAO.getSemestersForStudent(username);
            for (String sem : semesters) {
                Map<String, Map<String, Object>> recs = getSemesterRecords(username, sem);
                if (recs != null && recs.containsKey(courseCode)) {
                    return (int) recs.get(courseCode).get("marks") < 50; // Check if marks are less than 50 (failed)
                }
            }
            return false; // Course not found or not failed
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking if course " + courseCode + " is failed for student " + username);
            return false;
        }
    }

    public boolean isCourseAvailableForRegistration(String courseCode) {
        return true;
    }

    public double calculateSemesterCGPA(String username, String semester) {
        Map<String, Map<String, Object>> recs = getSemesterRecords(username, semester);
        double totalPoints = 0;
        int totalCredits = 0;
        for (Map<String, Object> subj : recs.values()) {
            int marks = (int) subj.get("marks");
            int credits = (int) subj.get("credits");
            double gp = calculateSubjectGPA(marks);
            totalPoints += gp * credits;
            totalCredits += credits;
        }
        return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }

    public double calculateOverallCGPA(String username) {
        try {
            double totalPoints = 0;
            int totalCredits = 0;
            for (String sem : reportDAO.getSemestersForStudent(username)) {
                Map<String, Map<String, Object>> recs = getSemesterRecords(username, sem);
                for (Map<String, Object> subj : recs.values()) {
                    int marks = (int) subj.get("marks");
                    int credits = (int) subj.get("credits");
                    double gp = calculateSubjectGPA(marks);
                    totalPoints += gp * credits;
                    totalCredits += credits;
                }
            }
            return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error calculating overall CGPA for student " + username);
            return 0.0;
        }
    }

    public boolean studentExists(String regno) {
        try {
            return reportDAO.studentExists(regno);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking student existence for " + regno);
            return false;
        }
    }

    public java.util.List<model.CourseModel.CourseInfo> getEligibleCoursesForStudent(String semester, String username, model.CourseModel courseModel) {
        java.util.List<model.CourseModel.CourseInfo> eligible = new java.util.ArrayList<>();
        java.util.List<model.CourseModel.CourseInfo> offered = courseModel.getOfferedCoursesBySemester().getOrDefault(semester, java.util.Collections.emptyList());
        for (model.CourseModel.CourseInfo info : offered) {
            boolean passed = isCoursePassed(username, info.code);
            boolean failed = isCourseFailed(username, info.code);
            boolean prereqsMet = allPrerequisitesMet(username, info.code);

            if (!passed && (prereqsMet || failed)) {
                eligible.add(info);
            }
        }
        return eligible;
    }

    public List<Map<String, Object>> getSemesterReport(String semesterLabel) {
        try {
            return reportDAO.getAcademicReport(studentId, semesterLabel);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static double getGradePoint(String grade) {
        switch (grade) {
            case "A+": return 4.0;
            case "A": return 4.0;
            case "A-": return 3.8;
            case "B+": return 3.5;
            case "B": return 3.0;
            case "B-": return 2.8;
            case "C+": return 2.5;
            case "C": return 2.0;
            case "C-": return 1.8;
            case "D+": return 1.5;
            case "D": return 1.0;
            default: return 0.0;
        }
    }
} 