package com.example;

import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Course(String id, String subject, String courseNumber, List<MeetingFaculty> meetingsFaculty,
                     List<Faculty> faculty) {

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
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MeetingTime(
          @JsonProperty("beginTime") @JsonFormat(pattern = "HHmm") LocalTime startTime,
          @JsonFormat(pattern = "HHmm") LocalTime endTime,
        boolean monday, boolean tuesday, boolean wednesday,
        boolean thursday, boolean friday, boolean saturday, boolean sunday
    ){
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
            String start = startTime.format(timeFormat);
            String end = endTime.format(timeFormat);
            String startPeriod = startTime.format(periodFormat);
            String endPeriod = endTime.format(periodFormat);

            if (!startPeriod.equals(endPeriod)) {
                start += " " + startPeriod;
            }
            end += " " + endPeriod;

            return start + " - " + end + ", " + days;

        }
    }; 

    /**  
     * Returns a string representation of the course     
     * @return the formatted course string
     */
    @Override 
     public String toString() {
        return subject + "-" + courseNumber;
    }
}
    

   

