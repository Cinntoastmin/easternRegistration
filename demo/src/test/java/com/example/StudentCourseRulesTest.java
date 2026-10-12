package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;
import java.util.List;
import org.junit.Test;

public class StudentCourseRulesTest {

    @Test
    public void courseCalculatesCreditsFromItsMeetings() {
        Course course = course(meeting(9, 0, 9, 50, 3.0, true, false));

        assertEquals(3.0, course.getCredits(), 0.0);
    }

    @Test
    public void courseDetectsMeetingConflictsOnSharedDays() {
        Course first = course(meeting(9, 0, 9, 50, 3.0, true, false));
        Course overlapping = course(meeting(9, 30, 10, 20, 3.0, true, false));
        Course differentDay = course(meeting(9, 30, 10, 20, 3.0, false, true));

        assertTrue(first.conflictsWith(overlapping));
        assertFalse(first.conflictsWith(differentDay));
    }

    @Test
    public void studentRejectsCourseWithConflictingMeeting() {
        Student student = new Student(1, "Ada", "Lovelace");
        Course first = course(meeting(9, 0, 9, 50, 3.0, true, false));
        Course overlapping = course(meeting(9, 30, 10, 20, 3.0, true, false));

        assertTrue(student.addCourse(first));
        assertFalse(student.addCourse(overlapping));
    }

    @Test
    public void courseIsFullWhenEnrollmentReachesMaximum() {
        Course full = new Course("1", "CSC", "101", List.of(), List.of(), 1, "Test Course", 20, 20);
        Course withSpace = new Course("2", "CSC", "102", List.of(), List.of(), 2, "Test Course", 20, 19);

        assertTrue(full.isFull());
        assertFalse(withSpace.isFull());
    }

    @Test
    public void studentRejectsFullCourse() {
        Student student = new Student(1, "Ada", "Lovelace");
        Course full = new Course("1", "CSC", "101", List.of(), List.of(), 1, "Test Course", 20, 20);

        assertFalse(student.addCourse(full));
    }

    private static Course course(Course.MeetingTime meetingTime) {
        return new Course("1", "CSC", "101",
                List.of(new Course.MeetingFaculty(meetingTime)), List.of(), 1, "Test Course", 20, 19);
    }

    private static Course.MeetingTime meeting(
            int startHour, int startMinute, int endHour, int endMinute,
            double credits, boolean monday, boolean tuesday) {
        return new Course.MeetingTime(
                LocalTime.of(startHour, startMinute),
                LocalTime.of(endHour, endMinute),
                credits,
                monday, tuesday, false, false, false, false, false);
    }
}
