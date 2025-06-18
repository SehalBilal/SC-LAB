import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import model.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            // Test database connection
            System.out.println("Testing database connection...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Database connection successful!");

            // Create tables if they don't exist
            createTables(conn);
            
            System.out.println("All tables created successfully!");
            conn.close();
            
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Create Students table
            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "id VARCHAR(20) PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password VARCHAR(100) NOT NULL," +
                "department VARCHAR(50) NOT NULL," +
                "semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8)" +
                ")";
            stmt.execute(createStudentsTable);
            System.out.println("Students table created/verified");

            // Create Course Coordinators table
            String createCourseCoordinatorsTable = "CREATE TABLE IF NOT EXISTS course_coordinators (" +
                "id VARCHAR(20) PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password VARCHAR(100) NOT NULL," +
                "department VARCHAR(50) NOT NULL" +
                ")";
            stmt.execute(createCourseCoordinatorsTable);
            System.out.println("Course Coordinators table created/verified");

            // Create Timetable Coordinators table
            String createTimetableCoordinatorsTable = "CREATE TABLE IF NOT EXISTS timetable_coordinators (" +
                "id VARCHAR(20) PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password VARCHAR(100) NOT NULL," +
                "department VARCHAR(50) NOT NULL" +
                ")";
            stmt.execute(createTimetableCoordinatorsTable);
            System.out.println("Timetable Coordinators table created/verified");

            // Create Courses table
            String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "code VARCHAR(20) UNIQUE NOT NULL," +
                "name VARCHAR(100) NOT NULL," +
                "credits INT NOT NULL CHECK (credits > 0)," +
                "department VARCHAR(50) NOT NULL," +
                "semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8)" +
                ")";
            stmt.execute(createCoursesTable);
            System.out.println("Courses table created/verified");

            // Create Offered Courses table
            String createOfferedCoursesTable = "CREATE TABLE IF NOT EXISTS offered_courses (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "course_id INT NOT NULL," +
                "semester_label VARCHAR(20) NOT NULL," +
                "faculty VARCHAR(100) NOT NULL," +
                "section VARCHAR(10) DEFAULT 'A'," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_offering (course_id, semester_label)" +
                ")";
            stmt.execute(createOfferedCoursesTable);
            System.out.println("Offered Courses table created/verified");

            // Create Prerequisites table
            String createPrerequisitesTable = "CREATE TABLE IF NOT EXISTS prerequisites (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "course_id INT NOT NULL," +
                "prereq_course_id INT NOT NULL," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "FOREIGN KEY (prereq_course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_prereq (course_id, prereq_course_id)" +
                ")";
            stmt.execute(createPrerequisitesTable);
            System.out.println("Prerequisites table created/verified");

            // Create Registrations table
            String createRegistrationsTable = "CREATE TABLE IF NOT EXISTS registrations (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "student_id VARCHAR(20) NOT NULL," +
                "course_id INT NOT NULL," +
                "registration_date DATE NOT NULL," +
                "status ENUM('PENDING', 'APPROVED', 'REGISTERED', 'REJECTED', 'DROPPED') NOT NULL," +
                "semester_label VARCHAR(20) NOT NULL," +
                "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_registration (student_id, course_id, semester_label)" +
                ")";
            stmt.execute(createRegistrationsTable);
            System.out.println("Registrations table created/verified");

            // Create Timetable table
            String createTimetableTable = "CREATE TABLE IF NOT EXISTS timetable (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "course_id INT NOT NULL," +
                "day ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY') NOT NULL," +
                "time_slot VARCHAR(20) NOT NULL," +
                "room VARCHAR(50) NOT NULL," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_schedule (day, time_slot, room)" +
                ")";
            stmt.execute(createTimetableTable);
            System.out.println("Timetable table created/verified");

            // Create Grades table
            String createGradesTable = "CREATE TABLE IF NOT EXISTS grades (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "student_id VARCHAR(20) NOT NULL," +
                "course_id INT NOT NULL," +
                "marks INT NOT NULL CHECK (marks BETWEEN 0 AND 100)," +
                "grade ENUM('A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'F') NOT NULL," +
                "semester_label VARCHAR(20) NOT NULL," +
                "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE," +
                "FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_grade (student_id, course_id)" +
                ")";
            stmt.execute(createGradesTable);
            System.out.println("Grades table created/verified");

            // Create Scheme of Study table
            String createSchemeOfStudyTable = "CREATE TABLE IF NOT EXISTS scheme_of_study (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "code VARCHAR(20) NOT NULL," +
                "title VARCHAR(100) NOT NULL," +
                "pre_req VARCHAR(100)," +
                "semester VARCHAR(20) NOT NULL" +
                ")";
            stmt.execute(createSchemeOfStudyTable);
            System.out.println("Scheme of Study table created/verified");

        } catch (SQLException e) {
            System.out.println("Error creating tables!");
            throw e;
        }
    }
} 