package dao;

import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.CourseModel;
import java.util.Arrays;
import java.util.Collections;

public class TimetableDAO {
    private Connection conn;
    private CourseDAO courseDAO; // To get course code from ID and vice versa

    public TimetableDAO(Connection conn, CourseDAO courseDAO) {
        this.conn = conn;
        this.courseDAO = courseDAO;
    }

    public LinkedHashMap<String, Map<String, List<String>>> getFullTimetable() throws SQLException {
        LinkedHashMap<String, Map<String, List<String>>> timetable = new LinkedHashMap<>();
        // Dynamically fetch unique days, rooms, and time slots from the database
        List<String> days = new ArrayList<>();
        List<String> rooms = new ArrayList<>();
        List<String> timeSlots = new ArrayList<>();
        String dayQuery = "SELECT DISTINCT day FROM timetable ORDER BY FIELD(day, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY')";
        String roomQuery = "SELECT DISTINCT room FROM timetable ORDER BY room";
        String slotQuery = "SELECT DISTINCT time_slot FROM timetable ORDER BY time_slot";
        try (Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery(dayQuery);
            while (rs.next()) days.add(rs.getString(1));
            rs = s.executeQuery(roomQuery);
            while (rs.next()) rooms.add(rs.getString(1));
            rs = s.executeQuery(slotQuery);
            while (rs.next()) timeSlots.add(rs.getString(1));
        }
        // Build empty structure
        for (String day : days) {
            timetable.put(day, new LinkedHashMap<>());
            for (String room : rooms) {
                List<String> slots = new ArrayList<>(Collections.nCopies(timeSlots.size(), ""));
                timetable.get(day).put(room, slots);
            }
        }
        // Fill with courses
        String sql = "SELECT t.day, t.time_slot, t.room, c.code FROM timetable t JOIN courses c ON t.course_id = c.id";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String day = rs.getString("day");
                String timeSlot = rs.getString("time_slot");
                String room = rs.getString("room");
                String courseCode = rs.getString("code");
                int slotIndex = timeSlots.indexOf(timeSlot);
                if (slotIndex != -1 && timetable.containsKey(day) && timetable.get(day).containsKey(room)) {
                    timetable.get(day).get(room).set(slotIndex, courseCode);
                }
            }
        }
        return timetable;
    }

    public void addTimetableEntry(int courseId, String day, String timeSlot, String room) throws SQLException {
        String sql = "INSERT INTO timetable (course_id, day, time_slot, room) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, day);
            ps.setString(3, timeSlot);
            ps.setString(4, room);
            ps.executeUpdate();
        }
    }

    public void updateTimetableEntry(int oldCourseId, String oldDay, String oldTimeSlot, String oldRoom,
                                     int newCourseId, String newDay, String newTimeSlot, String newRoom) throws SQLException {
        String sql = "UPDATE timetable SET course_id=?, day=?, time_slot=?, room=? WHERE course_id=? AND day=? AND time_slot=? AND room=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCourseId);
            ps.setString(2, newDay);
            ps.setString(3, newTimeSlot);
            ps.setString(4, newRoom);
            ps.setInt(5, oldCourseId);
            ps.setString(6, oldDay);
            ps.setString(7, oldTimeSlot);
            ps.setString(8, oldRoom);
            ps.executeUpdate();
        }
    }

    public void deleteTimetableEntry(int courseId, String day, String timeSlot, String room) throws SQLException {
        String sql = "DELETE FROM timetable WHERE course_id=? AND day=? AND time_slot=? AND room=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, day);
            ps.setString(3, timeSlot);
            ps.setString(4, room);
            ps.executeUpdate();
        }
    }

    // Helper to get course ID by code, could be from CourseDAO directly if passed
    private int getCourseIdByCode(String code) throws SQLException {
        return courseDAO.getCourseIdByCode(code);
    }

    public LinkedHashMap<String, Map<String, List<String>>> getFullTimetableBySlots(String[] days, String[] rooms, String[] slots) throws SQLException {
        LinkedHashMap<String, Map<String, List<String>>> timetable = new LinkedHashMap<>();
        // Build empty structure
        for (String day : days) {
            timetable.put(day, new LinkedHashMap<>());
            for (String room : rooms) {
                List<String> slotList = new ArrayList<>(Collections.nCopies(slots.length, ""));
                timetable.get(day).put(room, slotList);
            }
        }
        // Fill with courses
        String sql = "SELECT t.day, t.time_slot, t.room, c.code FROM timetable t JOIN courses c ON t.course_id = c.id";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String day = rs.getString("day");
                String timeSlot = rs.getString("time_slot");
                String room = rs.getString("room");
                String courseCode = rs.getString("code");
                int slotIndex = -1;
                for (int i = 0; i < slots.length; i++) {
                    if (slots[i].equalsIgnoreCase(timeSlot)) {
                        slotIndex = i;
                        break;
                    }
                }
                if (slotIndex != -1 && timetable.containsKey(day) && timetable.get(day).containsKey(room)) {
                    timetable.get(day).get(room).set(slotIndex, courseCode);
                }
            }
        }
        return timetable;
    }
} 