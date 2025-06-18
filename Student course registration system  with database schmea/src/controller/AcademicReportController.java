package controller;

import view.AcademicReportView;
import model.AcademicModel;
import model.UserModel;
import model.CourseModel;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class AcademicReportController {
    private AcademicReportView view;
    private AcademicModel academicModel;
    private UserModel userModel;
    private CourseModel courseModel;
    private String username;
    private int currentSemesterIndex = 0;

    public AcademicReportController(AcademicModel academicModel, UserModel userModel, CourseModel courseModel, String username) {
        this.academicModel = academicModel;
        this.userModel = userModel;
        this.courseModel = courseModel;
        this.username = username;
        this.view = new AcademicReportView(this, academicModel, userModel, username, courseModel);
    }

    public void showView() {
        view.setVisible(true);
        currentSemesterIndex = 0;
        onSemesterChanged(currentSemesterIndex);
    }

    public void hideView() {
        view.setVisible(false);
    }

    public void navigateTo(int idx) {
        view.dispose();
        if (idx == 3) { // If navigating to Academic Report
            new AcademicReportController(academicModel, userModel, courseModel, username).showView();
        } else {
            new controller.DashboardController(username, userModel, courseModel, academicModel).navigateTo(idx);
        }
    }

    // Data class for passing subject records
    public static class Record {
        public String name;
        public int marks;
        public String grade;
        public int credits;
        public String passFail;
        public Record(String name, int marks, String grade, int credits, String passFail) {
            this.name = name;
            this.marks = marks;
            this.grade = grade;
            this.credits = credits;
            this.passFail = passFail;
        }
    }

    public String getStudentName() {
        return userModel.getName();
    }
    public String getStudentRegNo() {
        return userModel.getRegNo();
    }
    public List<String> getSemesterLabels() {
        List<String> sems = academicModel.getSemesters();
        List<String> labels = new ArrayList<>();
        for (String s : sems) {
            if (s.contains("Spring")) labels.add(s.replace("Spring ", "Spring ") + " Records");
            else if (s.contains("Fall")) labels.add(s.replace("Fall ", "Fall ") + " Records");
            else labels.add(s + " Records");
        }
        return labels;
    }
    public void onSemesterChanged(int index) {
        String semester = academicModel.getSemesters().get(index);
        Map<String, Map<String, Object>> records = academicModel.getSemesterRecords(username, semester);
        List<Object[]> tableRows = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : records.entrySet()) {
            String code = entry.getKey();
            String name = (String) entry.getValue().get("name");
            int credits = (int) entry.getValue().get("credits");
            int marks = (int) entry.getValue().get("marks");
            String grade = AcademicModel.getGrade(marks);
            double gp = AcademicModel.calculateSubjectGPA(marks);
            tableRows.add(new Object[]{code, name, credits, marks, grade, gp});
        }
        double gpa = academicModel.calculateSemesterCGPA(username, semester);
        double cgpa = academicModel.calculateOverallCGPA(username);
        String studentName = userModel.getName();
        String regNo = userModel.getRegNo();
        String program = userModel.getProgram();
        String semLabel = semester;
        view.displayReportTable(studentName, regNo, program, semLabel, tableRows, gpa, cgpa, index, academicModel.getSemesters().size());
    }

    public void onPrevSemester() {
        if (currentSemesterIndex > 0) {
            currentSemesterIndex--;
            onSemesterChanged(currentSemesterIndex);
        }
    }

    public void onNextSemester() {
        if (currentSemesterIndex < academicModel.getSemesters().size() - 1) {
            currentSemesterIndex++;
            onSemesterChanged(currentSemesterIndex);
        }
    }
} 