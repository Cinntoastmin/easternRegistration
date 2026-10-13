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
    private Student currStudent;

    public MainMenu(EasternSchedule schedule) {
        this.schedule = Objects.requireNonNull(schedule, "schedule");
    }

    public void run() throws Exception {
        System.out.println("Welcome to the Eastern Registration System");
        while (true) {
            System.out.print("would you like to 'view' courses (type 'view'), or 'setup' your schedule? (type setup)? ");

            if (!INPUT.hasNextLine()){
                return;
            }

            String selection = INPUT.nextLine().trim();
            if (selection.equalsIgnoreCase("view")){
                viewCourses();
            }
            if (selection.equalsIgnoreCase("setup")){
                if(currStudent == null){
                    setStudent(createStudent());
                }
                addCourses();
            }
        }
    }

    
    private void setStudent(Student s){
        this.currStudent = s;
    }

    public void viewCourses() throws Exception{
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
            
        }

        displayCoursesBySubject(subject);
    }

    public Student createStudent(){
        System.out.println("It looks like this is the first time you've setup a schedule this session");
        System.out.print("please enter your student ID:");

        if(!INPUT.hasNextLine()){
            System.out.println("please enter an ID");
        }

        String sID = INPUT.nextLine().trim();

        System.out.println();
        System.out.print("please enter your first name:");

        if(!INPUT.hasNextLine()){
            System.out.println("please enter your first name");
        }

        String sFirstName = INPUT.nextLine().trim();
        
        System.out.println();
        System.out.print("please enter your last name:");

        if(!INPUT.hasNextLine()){
            System.out.println("please enter a last name");
        }

        String sLastName = INPUT.nextLine().trim();

        return new Student(sID, sFirstName, sLastName);
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

    public void addCourses() throws Exception {
        if (currStudent == null) {
            System.out.println("Please set up a student before adding courses.");
            return;
        }

        while (true) {
            System.out.print("Enter a course as Subject-CourseNumber-section"
                    + "(for example CSC-450-01), or Q to finish: ");
            if (!INPUT.hasNextLine()) {
                return;
            }

            String courseCode = INPUT.nextLine().trim();
            if (courseCode.equalsIgnoreCase("q")) {
                return;
            }
            String[] parts = courseCode.split("-", -1);
            if (parts.length != 3
                    || parts[0].isBlank()
                    || parts[1].isBlank()
                    || parts[2].isBlank()) {
                System.out.println("Please use the format subject-courseNumber-sequenceNumber, "
                        + "for example CSC-450-01.");
                continue;
            }

            String subject = parts[0].trim();
            String courseNumber = parts[1].trim();
            String sequenceNumber = parts[2].trim();
            ArrayList<Course> availableCourses = schedule.queryCourseBySubject(subject);
            Course course = availableCourses.stream()
                    .filter(Objects::nonNull)
                    .filter(availableCourse -> subject.equalsIgnoreCase(availableCourse.subject())
                            && courseNumber.equalsIgnoreCase(availableCourse.courseNumber())
                            && sequenceNumber.equalsIgnoreCase(availableCourse.sequenceNumber()))
                    .findFirst()
                    .orElse(null);

            if (course == null) {
                System.out.println("No course found for " + courseCode + ".");
            } else if (currStudent.addCourse(course)) {
                System.out.println("Added " + course + " to your schedule.");
            } else {
                System.out.println("Could not add " + courseCode
                        + ". It may already be scheduled, full, exceed the credit limit, "
                        + "or conflict with another course.");
            }
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
