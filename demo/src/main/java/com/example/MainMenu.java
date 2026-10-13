package com.example;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class MainMenu {
    private static final Scanner INPUT = new Scanner(System.in);
    private final EasternSchedule schedule;
    private Student currStudent;

    /**
     * default constructor
     * 
     * @param schedule an Eastern Schedule object
     */
    public MainMenu(EasternSchedule schedule) {
        this.schedule = Objects.requireNonNull(schedule, "schedule");
    }

    /**
     * primary runtime loop
     * 
     * @throws Exception coming from a used method
     * @see displayCoursesBySubject
     */
    public void run() throws Exception {
        System.out.println("Welcome to the Eastern Registration System");
        while (true) {
            System.out.print("Choose 'view' to view courses, 'setup' to add courses, "
                    + "'drop' to drop courses, or 'schedule' to view your current course load: ");

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
            if (selection.equalsIgnoreCase("drop")) {
                dropCourses();
            }
            if (selection.equalsIgnoreCase("schedule")) {
                if (currStudent == null) {
                    System.out.println("Please set up a student before viewing your schedule.");
                } else {
                    currStudent.displayCourses();
                }
            }
        }
    }

    /**
     * setter for current Student
     * @param s
     */
    private void setStudent(Student s){
        this.currStudent = s;
    }

    /**
     * Lets the user view courses by subject, optionally filtered by course number.
     * 
     * @throws Exception from a used method
     * @see #displayCoursesBySubject(String)
     * @see #displayCoursesBySubjectAndNumber(String, String)
     */
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
            return;
        }

        System.out.print("Enter a course number to filter, or press Enter to view all: ");
        if (!INPUT.hasNextLine()) {
            return;
        }

        String courseNumber = INPUT.nextLine().trim();
        if (courseNumber.equalsIgnoreCase("q")) {
            return;
        }
        if (courseNumber.isEmpty()) {
            displayCoursesBySubject(subject);
        } else {
            displayCoursesBySubjectAndNumber(subject, courseNumber);
        }
    }

    /**
     * setups a new student profile for the current session
     * 
     * @return the created student object
     */
    public Student createStudent(){
        System.out.println("It looks like this is the first time you've setup a schedule this session");
        System.out.print("please enter your student ID:");

        if(!INPUT.hasNextLine()){
            System.out.println("please enter an ID");
        }

        String sID = INPUT.nextLine().trim();

        System.out.print("please enter your first name:");

        if(!INPUT.hasNextLine()){
            System.out.println("please enter your first name");
        }

        String sFirstName = INPUT.nextLine().trim();
        
        System.out.print("please enter your last name:");

        if(!INPUT.hasNextLine()){
            System.out.println("please enter a last name");
        }

        String sLastName = INPUT.nextLine().trim();

        return new Student(sID, sFirstName, sLastName);
    }

    /**
     * displays to terminal courses from a given subject code
     * 
     * @param subject the three letter course subject code
     * 
     * @throws Exception handling from used method
     * @see queryCourseBySubject
     */
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

    /**
     * Displays courses matching the given subject and course number.
     *
     * @param subject the subject code to search
     * @param courseNumber the course number to search
     * @throws Exception if the course query fails
     */
    private void displayCoursesBySubjectAndNumber(String subject, String courseNumber) throws Exception {
        ArrayList<Course> courses = schedule.queryCourseBySubject(subject).stream()
                .filter(Objects::nonNull)
                .filter(course -> courseNumber.equalsIgnoreCase(course.courseNumber()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        if (courses.isEmpty()) {
            System.out.println("No courses found for " + subject.toUpperCase(Locale.ROOT)
                    + " " + courseNumber + ".");
            return;
        }

        for (Course course : courses) {
            System.out.println(formatCourse(course));
        }
    }

    /**
     * lets the user attempt to add any number of courses to their schedule
     * 
     * @throws Exception from queryCourseBySubject
     * @see queryCourseBySubject
     */
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

    /**
     * takes the user through dropping courses from their schedule.
     */
    public void dropCourses() {
        if (currStudent == null) {
            System.out.println("Please set up a student before dropping courses.");
            return;
        }

        if (currStudent.getCourses().isEmpty()) {
            System.out.println("There are no courses in your schedule to drop.");
            return;
        }

        System.out.println("Courses currently in your schedule:");
        for (Course course : currStudent.getCourses()) {
            if (course != null) {
                System.out.println(formatCourse(course));
            }
        }

        while (true) {
            System.out.print("Enter a course as Subject-CourseNumber-section "
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
            Course course = currStudent.getCourses().stream()
                    .filter(Objects::nonNull)
                    .filter(scheduledCourse -> subject.equalsIgnoreCase(scheduledCourse.subject())
                            && courseNumber.equalsIgnoreCase(scheduledCourse.courseNumber())
                            && sequenceNumber.equalsIgnoreCase(scheduledCourse.sequenceNumber()))
                    .findFirst()
                    .orElse(null);

            if (course == null) {
                System.out.println("Course " + courseCode + " is not in your schedule.");
            } else if (currStudent.dropCourse(course)) {
                System.out.println("Dropped " + courseCode + " from your schedule.");
            } else {
                System.out.println("Could not drop " + courseCode + " from your schedule.");
            }
        }
    }

    /**
     * wrapper for course to string
     * 
     * @param course a course to be stringified
     * @return a string representation of the given course
     */
    private String formatCourse(Course course) {
        return course.toString();
    }
}
