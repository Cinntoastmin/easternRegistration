package com.example;

import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Course record to store select course information
 * @param id course id
 * @param subject the course subject
 * @param courseNumber the course number
 * @param meetingsFaculty a list of meetingFaculty objects (see @link com.example.MeetingFaculty)
 * @param faculty a list of faculty members who teach the course
 * @param sequenceNumber the section number of the course
 * @param courseTitle the name of the course
 * @param maximumEnrollment the maximum number of students who may enroll
 * @param enrollment the current number of enrolled students
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Course(
    String id, 
    String subject,
    String courseNumber, 
    List<MeetingFaculty> meetingsFaculty,
    List<Faculty> faculty,
    String sequenceNumber,
    String courseTitle,
    int maximumEnrollment,
    int enrollment) {

    /**
     * 
     * MeetingFaculty record to store select meetingFaculty information
     * @param meetingTime a meetingTime object (see @link com.example.MeetingTime)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MeetingFaculty(MeetingTime meetingTime) {

       /**  
        * Returns a string representation of the meeting faculty objet     
        * @return the formatted meeting faculty string
        */
        @Override 
        public String toString() {
            return meetingTime.toString();
        }
    }

    /**
     * Returns the total credits assigned to this course's meeting sessions.
     */
    public double getCredits() {
        double total = 0.0;
        for (MeetingFaculty meeting : safeMeetings()) {
            if (meeting != null && meeting.meetingTime() != null) {
                total += meeting.meetingTime().creditHourSession();
            }
        }
        if (total <= 0.0 && meetingsFaculty != null && !meetingsFaculty.isEmpty()) {
            return meetingsFaculty.stream()
                .map(MeetingFaculty::meetingTime)
                .filter(Objects::nonNull)
                .mapToDouble(MeetingTime::creditHourSession)
                .sum();
        }
        return total;
    }

    /** Checks whether enrollment has reached this course's maximum capacity. */
    public boolean isFull() {
        return maximumEnrollment > 0 && enrollment >= maximumEnrollment;
    }

    /**
     * Checks whether this course overlaps another course's meeting schedule.
     */
    public boolean conflictsWith(Course other) {
        if (other == null) {
            return false;
        }
        for (MeetingFaculty firstMeeting : safeMeetings()) {
            for (MeetingFaculty secondMeeting : other.safeMeetings()) {
                if (firstMeeting != null && secondMeeting != null
                        && firstMeeting.meetingTime() != null
                        && firstMeeting.meetingTime().conflictsWith(secondMeeting.meetingTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether this course conflicts with any course in the given schedule.
     */
    public boolean hasTimeConflict(Iterable<Course> schedule) {
        if (schedule == null) {
            return false;
        }
        for (Course course : schedule) {
            if (course != null && conflictsWith(course)) {
                return true;
            }
        }
        return false;
    }

    private List<MeetingFaculty> safeMeetings() {
        return meetingsFaculty == null ? List.of() : meetingsFaculty;
    }
       
    /**
     * 
     * MeetingTime record to store select meetingTime information
     * @param monday true if meeting time is on this day; false otherwise
     * @param tuesday true if meeting time is on this day; false otherwise
     * @param wednesday true if meeting time is on this day; false otherwise
     * @param thursday true if meeting time is on this day; false otherwise
     * @param friday true if meeting time is on this day; false otherwise
     * @param saturday true if meeting time is on this day; false otherwise
     * @param sunday true if meeting time is on this day; false otherwise
     * @param startTime the meeting start time
     * @param endTime the meeting end time
     * @param creditHourSession the credit hours associated with the meeting session
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MeetingTime(
        @JsonProperty("beginTime") @JsonFormat(pattern = "HHmm") LocalTime startTime,
        @JsonFormat(pattern = "HHmm") LocalTime endTime,
        double creditHourSession,
        boolean monday, boolean tuesday, boolean wednesday,
        boolean thursday, boolean friday, boolean saturday, boolean sunday
    ){
        /**
         * Checks whether this meeting overlaps another on at least one day.
         */
        public boolean conflictsWith(MeetingTime other) {
            if (other == null || !sharesDayWith(other)
                    || startTime == null || endTime == null
                    || other.startTime == null || other.endTime == null) {
                return false;
            }
            return !startTime.isAfter(other.endTime) && !other.startTime.isAfter(endTime);
        }

        private boolean sharesDayWith(MeetingTime other) {
            return monday && other.monday
                    || tuesday && other.tuesday
                    || wednesday && other.wednesday
                    || thursday && other.thursday
                    || friday && other.friday
                    || saturday && other.saturday
                    || sunday && other.sunday;
        }

        /**  
        * Returns a string representation of the meeting time     
        * @return the formatted meeting time string
        */
        @Override 
        public String toString() {
            StringBuilder days = new StringBuilder();
            if (monday) days.append("M");
            if (tuesday) days.append("T");
            if (wednesday) days.append("W");
            if (thursday) days.append("R");
            if (friday) days.append("F");
            if (saturday) days.append("S");
            if (sunday) days.append("U");

            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm", Locale.US);
            DateTimeFormatter periodFormat = DateTimeFormatter.ofPattern("a", Locale.US);
            if (startTime == null || endTime == null) {
                if (days.length() > 0) {
                    return creditHourSession + " | time unavailable, " + days;
                }
                return creditHourSession + " | time unavailable";
            }

            String start = startTime.format(timeFormat);
            String end = endTime.format(timeFormat);
            String startPeriod = startTime.format(periodFormat);
            String endPeriod = endTime.format(periodFormat);

            if (!startPeriod.equals(endPeriod)) {
                start += " " + startPeriod;
            }
            end += " " + endPeriod;

            String base = creditHourSession + " | " + start + " - " + end;
            if (days.length() > 0) {
                return base + ", " + days;
            }
            return base;

        }
    }; 

    /**  
     * Returns a string representation of the course     
     * @return the formatted course string
     */
    @Override 
     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(subject).append("-").append(courseNumber).append("-").append(sequenceNumber);
        sb.append(" " + courseTitle);
        sb.append(" | ").append(enrollment).append("/").append(maximumEnrollment);

        if (meetingsFaculty != null && !meetingsFaculty.isEmpty()) {
            String meetings = meetingsFaculty.stream()
                .map(MeetingFaculty::toString)
                .collect(Collectors.joining(", "));
            sb.append(" | ").append(meetings);
        }

        if (faculty != null && !faculty.isEmpty()) {
            String facultyNames = faculty.stream()
                .map(Faculty::displayName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.joining(", "));
            if (!facultyNames.isEmpty()) {
                sb.append(" | ").append(facultyNames);
            }
        }

        return sb.toString();
    }
}
    

   
