package model;

public class Conflict {
    private String day;
    private String room;
    private int timeSlot; // 0-based index for the time slot
    private String[] conflictingCourses;

    public Conflict(String day, String room, int timeSlot, String[] conflictingCourses) {
        this.day = day;
        this.room = room;
        this.timeSlot = timeSlot;
        this.conflictingCourses = conflictingCourses;
    }

    public String getDay() { return day; }
    public String getRoom() { return room; }
    public int getTimeSlot() { return timeSlot; }
    public String[] getConflictingCourses() { return conflictingCourses; }
} 