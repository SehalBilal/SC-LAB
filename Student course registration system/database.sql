-- Drop database if exists to start fresh
DROP DATABASE IF EXISTS student_registration;

-- Create database
CREATE DATABASE student_registration;
USE student_registration;

-- Create tables with improved constraints and data types
CREATE TABLE students (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8)
);

CREATE TABLE course_coordinators (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL
);

CREATE TABLE timetable_coordinators (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL
);

CREATE TABLE courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    credits INT NOT NULL CHECK (credits > 0),
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8)
);

CREATE TABLE offered_courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    semester_label VARCHAR(20) NOT NULL,
    faculty VARCHAR(100) NOT NULL,
    section VARCHAR(10) DEFAULT 'A',
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_offering (course_id, semester_label)
);

CREATE TABLE prerequisites (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    prereq_course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (prereq_course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_prereq (course_id, prereq_course_id)
);

CREATE TABLE registrations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL,
    course_id INT NOT NULL,
    registration_date DATE NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REGISTERED', 'REJECTED', 'DROPPED') NOT NULL,
    semester_label VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (student_id, course_id, semester_label)
);

CREATE TABLE timetable (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    day ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY') NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    room VARCHAR(50) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_schedule (day, time_slot, room)
);

CREATE TABLE grades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL,
    course_id INT NOT NULL,
    marks INT NOT NULL CHECK (marks BETWEEN 0 AND 100),
    grade ENUM('A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'F') NOT NULL,
    semester_label VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_grade (student_id, course_id)
);

CREATE TABLE IF NOT EXISTS scheme_of_study (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    pre_req VARCHAR(100),
    semester VARCHAR(20) NOT NULL
);

-- Insert test data for students
INSERT INTO students (id, name, email, password, department, semester)
VALUES ('04072313001', 'Muhammad Ali', 'ali@example.com', '001', 'Computer Science', 4);

-- Insert test data for coordinators
INSERT INTO course_coordinators (id, name, email, password, department)
VALUES ('admin001', 'Admin User', 'admin@example.com', '001', 'Computer Science');

INSERT INTO timetable_coordinators (id, name, email, password, department)
VALUES ('timetabler001', 'Timetable Admin', 'timetable@example.com', '001', 'Computer Science');

-- Insert all courses
INSERT INTO courses (code, name, credits, department, semester) VALUES
-- BS Semester I
('CS-104 PSP', 'Problem Solving & Programming', 3, 'Computer Science', 1),
('CS-110 ICT', 'Applications of ICT', 3, 'Computer Science', 1),
('PH-111', 'Introductory Mechanics & Waves ', 2, 'Physics', 1),
('PH-193', 'Introductory Mechanics & Waves Lab ', 1, 'Physics', 1),
('EN-101', 'Functional English', 3, 'English', 1),
('PS-151', 'Ideology of Constitution of Pakistan', 2, 'General', 1),
('MA-101', 'Calculus & Analytical Geometry-I', 3, 'Mathematics', 1),

-- BS Semester II
('CS-121 OOP', 'Object Oriented Programming', 4, 'Computer Science', 2),
('MA-102', 'Calculus & Analytical Geometry-II', 3, 'Mathematics', 2),
('IS-101', 'Islamic Studies', 2, 'General', 2),
('PH-112', 'Electricity, Magnetism and Thermal Physics', 2, 'Physics', 2),
('PH-194', 'Electricity, Magnetism and Thermal Physics Lab', 1, 'Physics', 2),
('EN-102', 'English – 2', 3, 'English', 2),
('MA-203 DM', 'Discrete Mathematics', 3, 'Mathematics', 2),

-- BS Semester III
('CS-211 DS', 'Data Structures', 3, 'Computer Science', 3),
('CS-291 DS Lab', 'Data Structures Lab', 1, 'Computer Science', 3),
('EN-201 EN', 'English-3', 3, 'English', 3),
('CS-212 HCI', 'Human Computer Interaction', 3, 'Computer Science', 3),
('CS-103 ICO', 'Introduction to Computer Organization', 3, 'Computer Science', 3),
('PY-101 PY', 'Introduction to Psychology', 3, 'General', 3),

-- BS Semester IV
('CH-100 GC', 'General Chemistry', 2, 'Chemistry', 4),
('CH-190 GC Lab', 'General Chemistry Lab', 1, 'Chemistry', 4),
('MA-205', 'Differential Equations and Linear Algebra', 3, 'Mathematics', 4),
('CS-222 ADSS', 'Analysis and Design for Software Systems', 3, 'Computer Science', 4),
('CS-225 DBS', 'Database Systems', 3, 'Computer Science', 4),
('CS-213 COAL', 'Computer Organization and Assembly Language', 3, 'Computer Science', 4),
('CS-293 COAL Lab', 'Computer Organization and Assembly Language Lab', 1, 'Computer Science', 4);

-- Insert offered courses for each semester
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Dr. Syed M Naqi', 'A' FROM courses c WHERE c.code = 'CS-104 PSP';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Ms. Ifrah Farrukh Khan', 'A' FROM courses c WHERE c.code = 'CS-110 ICT';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Ms. Fizza Mumtaz', 'A' FROM courses c WHERE c.code = 'PH-111';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Ms. Fizza Mumtaz', 'A' FROM courses c WHERE c.code = 'PH-193';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Ms. Shafaq Shakeel', 'A' FROM courses c WHERE c.code = 'EN-101';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Ms. Madeeha Arif', 'A' FROM courses c WHERE c.code = 'PS-151';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester I', 'Dr. Misbah Ejaz', 'A' FROM courses c WHERE c.code = 'MA-101';

-- BS Semester II
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester II', 'Ms. Memoona Afsheen', 'A' FROM courses c WHERE c.code = 'CS-121 OOP';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester II', 'Dr. Farwa Haider', 'A' FROM courses c WHERE c.code = 'MA-102';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester II', 'Mr. Abdul Wahid', 'A' FROM courses c WHERE c.code = 'IS-101';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester II', 'Ms. Fizza Mumtaz', 'A' FROM courses c WHERE c.code IN ('PH-112', 'PH-194');
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester II', 'Ms. Fiza Batool', 'A' FROM courses c WHERE c.code = 'EN-102';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester II', 'Dr. Umer Rashid', 'A' FROM courses c WHERE c.code = 'MA-203 DM';

-- BS Semester III
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester III', 'Dr. Rabeeh Abbasi', 'A' FROM courses c WHERE c.code IN ('CS-211 DS', 'CS-291 DS Lab');
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester III', 'Mr. Ali Naqi', 'A' FROM courses c WHERE c.code = 'EN-201 EN';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester III', 'Dr. Mohammad Shuaib Karim', 'A' FROM courses c WHERE c.code = 'CS-212 HCI';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester III', 'Ms. Memoona Afsheen', 'A' FROM courses c WHERE c.code = 'CS-103 ICO';
INSERT INTO offered_courses (course_id, semester_label, faculty, section)
SELECT c.id, 'BS Semester III', 'Ms. Ayesha Saeed', 'A' FROM courses c WHERE c.code = 'PY-101 PY';

-- Insert grades for Spring 2023
INSERT INTO grades (student_id, course_id, marks, grade, semester_label) VALUES
('04072313001', (SELECT id FROM courses WHERE code = 'CS-104 PSP'), 80, 'A', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-110 ICT'), 80, 'A', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PH-111'), 80, 'A', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PH-193'), 58, 'C', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'EN-101'), 80, 'A', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PS-151'), 80, 'A', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'MA-101'), 80, 'A', 'Spring 2023');

-- Insert registrations for Spring 2023
INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label) VALUES
('04072313001', (SELECT id FROM courses WHERE code = 'CS-104 PSP'), CURDATE(), 'REGISTERED', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-110 ICT'), CURDATE(), 'REGISTERED', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PH-111'), CURDATE(), 'REGISTERED', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PH-193'), CURDATE(), 'REGISTERED', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'EN-101'), CURDATE(), 'REGISTERED', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PS-151'), CURDATE(), 'REGISTERED', 'Spring 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'MA-101'), CURDATE(), 'REGISTERED', 'Spring 2023');

-- Insert registrations for Fall 2023
INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label) VALUES
('04072313001', (SELECT id FROM courses WHERE code = 'CS-121 OOP'), CURDATE(), 'REGISTERED', 'Fall 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'MA-102'), CURDATE(), 'REGISTERED', 'Fall 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'IS-101'), CURDATE(), 'REGISTERED', 'Fall 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PH-112'), CURDATE(), 'REGISTERED', 'Fall 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'PH-194'), CURDATE(), 'REGISTERED', 'Fall 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'EN-102'), CURDATE(), 'REGISTERED', 'Fall 2023'),
('04072313001', (SELECT id FROM courses WHERE code = 'MA-203 DM'), CURDATE(), 'REGISTERED', 'Fall 2023');

-- Insert grades for Spring 2024
INSERT INTO grades (student_id, course_id, marks, grade, semester_label) VALUES
('04072313001', (SELECT id FROM courses WHERE code = 'CS-211 DS'), 77, 'A-', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-291 DS Lab'), 80, 'A', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'EN-201 EN'), 73, 'B+', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-212 HCI'), 45, 'F', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-103 ICO'), 72, 'B+', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'PY-101 PY'), 76, 'A-', 'Spring 2024');

-- Insert registrations for Spring 2024
INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label) VALUES
('04072313001', (SELECT id FROM courses WHERE code = 'CS-211 DS'), CURDATE(), 'REGISTERED', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-291 DS Lab'), CURDATE(), 'REGISTERED', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'EN-201 EN'), CURDATE(), 'REGISTERED', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-212 HCI'), CURDATE(), 'REGISTERED', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'CS-103 ICO'), CURDATE(), 'REGISTERED', 'Spring 2024'),
('04072313001', (SELECT id FROM courses WHERE code = 'PY-101 PY'), CURDATE(), 'REGISTERED', 'Spring 2024');

-- Insert registrations for Fall 2024 (as per your request, including HCI retake)
INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label) VALUES
('04072313001', (SELECT id FROM courses WHERE code = 'CS-212 HCI'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label)
VALUES ('04072313001', (SELECT id FROM courses WHERE code = 'CH-100 GC'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label)
VALUES ('04072313001', (SELECT id FROM courses WHERE code = 'CH-190 GC Lab'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label)
VALUES ('04072313001', (SELECT id FROM courses WHERE code = 'MA-205'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label)
VALUES ('04072313001', (SELECT id FROM courses WHERE code = 'CS-225 DBS'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label)
VALUES ('04072313001', (SELECT id FROM courses WHERE code = 'CS-213 COAL'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

INSERT INTO registrations (student_id, course_id, registration_date, status, semester_label)
VALUES ('04072313001', (SELECT id FROM courses WHERE code = 'CS-293 COAL Lab'), CURDATE(), 'REGISTERED', 'Fall 2024')
ON DUPLICATE KEY UPDATE status = 'REGISTERED', semester_label = 'Fall 2024';

-- Insert data for scheme_of_study
INSERT INTO scheme_of_study (code, title, pre_req, semester) VALUES
('CS-101', 'Introduction to Computing', '', 'Semester I'),
('CS-105', 'Problem Solving and Programming', '', 'Semester I'),
('CS-121', 'Object Oriented Programming', 'CS-105: PSP', 'Semester II'),
('CS-103', 'Introduction to Computer Organization', '', 'Semester II'),
('CS-291', 'Data Structures Lab', '', 'Semester III'),
('CS-211', 'Data Structures', 'CS-121: OOP', 'Semester III'),
('CS-212', 'Human Computer Interaction', '', 'Semester III'),
('CS-213', 'Computer Organization & Assembly Language Programming', 'CS-103: ICO', 'Semester IV'),
('CS-293', 'Computer Organization & Assembly Language Lab', '', 'Semester IV'),
('CS-222', 'Analysis and Design for Software Systems', 'CS-212: HCI', 'Semester IV'),
('CS-225', 'Database Systems', '', 'Semester IV'),
('CS-311', 'Analysis and Design of Algorithms', 'CS-211: DS', 'Semester V'),
('CS-322', 'Software Construction', 'CS-222: ADSS/SS', 'Semester V'),
('CS-414', 'Artificial Intelligence', 'CS-311: A&D of Algor', 'Semester V'),
('CS-312', 'Computer Communication & Networks', 'CS-213: COAL', 'Semester VI'),
('CS-331', 'Theory of Automata', '', 'Semester VI'),
('CS-423', 'Computer Graphics', '', 'Semester VI'),
('CS-324', 'Web Application Development', '', 'Semester VI'),
('CS-332', 'Net Centric Programming', 'CS-322: SC', 'Semester VII'),
('CS-411', 'Compiler Construction', 'CS-331: ToA', 'Semester VII'),
('CS-489', 'Project I', '', 'Semester VII'),
('CS-457', 'Web Application Frameworks', 'CS-324: WAD', 'Semester VII'),
('CS-442', 'Mobile Application Development', '', 'Semester VII'),
('CS-490', 'Project II', '', 'Semester VIII'),
('CS-474', 'Software Testing Techniques', 'CS-322: SC', 'Semester VIII'),
('CS-449', 'ICT & Society', '', 'Semester VIII'),
('CS-413', 'Introduction to Information Security', '', 'Semester VIII'),
('CS-535', 'Cloud DevOps', '', 'Semester VIII');

-- Insert sample data for prerequisites
INSERT INTO prerequisites (course_id, prereq_course_id) VALUES
((SELECT id FROM courses WHERE code = 'CS-121 OOP'), (SELECT id FROM courses WHERE code = 'CS-104 PSP')),
((SELECT id FROM courses WHERE code = 'CS-211 DS'), (SELECT id FROM courses WHERE code = 'CS-121 OOP')),
((SELECT id FROM courses WHERE code = 'CS-291 DS Lab'), (SELECT id FROM courses WHERE code = 'CS-121 OOP')),
((SELECT id FROM courses WHERE code = 'CS-213 COAL'), (SELECT id FROM courses WHERE code = 'CS-103 ICO')),
((SELECT id FROM courses WHERE code = 'CS-293 COAL Lab'), (SELECT id FROM courses WHERE code = 'CS-103 ICO')),
((SELECT id FROM courses WHERE code = 'CS-222 ADSS'), (SELECT id FROM courses WHERE code = 'CS-212 HCI')),
((SELECT id FROM courses WHERE code = 'CS-311 ADA'), (SELECT id FROM courses WHERE code = 'CS-211 DS')),
((SELECT id FROM courses WHERE code = 'CS-414 AI'), (SELECT id FROM courses WHERE code = 'CS-311 ADA')),
((SELECT id FROM courses WHERE code = 'CS-322 SC'), (SELECT id FROM courses WHERE code = 'CS-222 ADSS')),
((SELECT id FROM courses WHERE code = 'CS-312 CCN'), (SELECT id FROM courses WHERE code = 'CS-213 COAL')),
((SELECT id FROM courses WHERE code = 'CS-331 TOA'), (SELECT id FROM courses WHERE code = 'CS-211 DS')),
((SELECT id FROM courses WHERE code = 'CS-332 NCP'), (SELECT id FROM courses WHERE code = 'CS-322 SC')),
((SELECT id FROM courses WHERE code = 'CS-411 CC'), (SELECT id FROM courses WHERE code = 'CS-331 TOA')),
((SELECT id FROM courses WHERE code = 'CS-457 WAF'), (SELECT id FROM courses WHERE code = 'CS-324 WAD')),
((SELECT id FROM courses WHERE code = 'CS-474 STT'), (SELECT id FROM courses WHERE code = 'CS-322 SC')); 