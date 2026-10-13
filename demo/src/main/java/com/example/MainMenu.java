package com.example;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class MainMenu {
    private static final Scanner INPUT = new Scanner(System.in);
    private final EasternSchedule schedule;

    public MainMenu(EasternSchedule schedule) {
        this.schedule = Objects.requireNonNull(schedule, "schedule");
    }

    public void run() throws Exception {
        while (true) {
            System.out.print("Enter a subject code to view courses (or Q to quit): ");
            if (!INPUT.hasNextLine()) {
                return;
            }

            String subject = INPUT.nextLine().trim();
            if (subject.equalsIgnoreCase("q")) {
                return;
            }
            if (subject.isEmpty()) {
                System.out.println("Please enter a subject code.");
                continue;
            }

            displayCoursesBySubject(subject);
        }
    }

    private void displayCoursesBySubject(String subject) throws Exception {
        ArrayList<Course> courses = schedule.queryCourseBySubject(subject);
        if (courses.isEmpty()) {
            System.out.println("No courses found for " + subject.toUpperCase(Locale.ROOT) + ".");
            return;
        }

        for (Course course : courses) {
            System.out.println(formatCourse(course));
        }
    }

    private String formatCourse(Course course) {
        return course.toString();
        /* 
        StringBuilder formatted = new StringBuilder()
            .append(course.subject())
            .append(' ')
            .append(course.courseNumber())
            .append('-')
            .append(String.format(Locale.ROOT, "%02d", course.sequenceNumber()))
            .append(", ");

        List<Course.MeetingFaculty> meetings = course.meetingsFaculty();
        if (meetings == null || meetings.isEmpty()) {
            formatted.append("credits unavailable, ")
                .append(course.courseTitle())
                .append(", meeting information unavailable");
            return formatted.toString();
        }

        Course.MeetingTime firstMeeting = meetings.get(0) == null
            ? null
            : meetings.get(0).meetingTime();
        if (firstMeeting == null) {
            formatted.append("credits unavailable");
        } else {
            DecimalFormat creditFormat = new DecimalFormat(
                "0.##",
                DecimalFormatSymbols.getInstance(Locale.US)
            );
            formatted.append(creditFormat.format(firstMeeting.creditHourSession()))
                .append(firstMeeting.creditHourSession() == 1.0 ? " credit, " : " credits, ");
        }
        formatted.append(course.courseTitle()).append(", ");

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
        for (int index = 0; index < meetings.size(); index++) {
            if (index > 0) {
                formatted.append("; ");
            }

            Course.MeetingFaculty meeting = meetings.get(index);
            Course.MeetingTime time = meeting == null ? null : meeting.meetingTime();
            if (time == null) {
                formatted.append("meeting information unavailable");
                continue;
            }

            StringBuilder days = new StringBuilder();
            if (time.monday()) days.append('M');
            if (time.tuesday()) days.append('T');
            if (time.wednesday()) days.append('W');
            if (time.thursday()) days.append('R');
            if (time.friday()) days.append('F');
            if (time.saturday()) days.append('S');
            if (time.sunday()) days.append('U');

            formatted.append(days.isEmpty() ? "days unavailable" : days)
                .append(", ");
            if (time.startTime() == null || time.endTime() == null) {
                formatted.append("time unavailable");
            } else {
                formatted.append(time.startTime().format(timeFormat))
                    .append(" - ")
                    .append(time.endTime().format(timeFormat));
            }
        }

        return formatted.toString();*/
    }
}
