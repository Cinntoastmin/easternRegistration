package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a student schedule and the registration rules for adding and dropping courses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    public static final int MAX_CREDITS = 17;

    private int id;
    private String firstName;
    private String lastName;
    private ArrayList<Course> courses;

    public Student() {
        this.courses = new ArrayList<>();
    }

    /**
     * Creates a student with the given identity.
     * 
     * @param id: a numerical identifier for the student
     * @param firstName the students first name
     * @param lastName the studnets last name
     */
    public Student(int id, String firstName, String lastName) {
        this();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * ID getter
     * 
     * @return student ID
     */
    public int getId() {
        return id;
    }

    /**
     * ID setter
     * 
     * @param id new student ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * first name getter
     * 
     * @return student's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * first name setter
     * 
     * @param firstName new first name for student
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * first name getter
     * 
     * @return student's first name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * last name setter
     * 
     * @param lastName new student last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * getter for student's courses
     * 
     * @return an array list of course records
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }

    /**
     * setter for student's courses
     * 
     * @param courses a list of courses to replace students current courses
     */
    public void setCourses(ArrayList<Course> courses) {
        this.courses = (courses == null) ? new ArrayList<>() : courses;
    }

    /**
     * add a course to student's course list
     * 
     * @param course a course record to be added
     * @return true if add successfull, false otherwise
     */
    public boolean addCourse(Course course) {
        if (course == null) {
            return false;
        }

        if (alreadyEnrolled(course)) {
            return false;
        }

        if (course.isFull()) {
            return false;
        }

        if (wouldExceedMaxCredits(course)) {
            return false;
        }

        if (course.hasTimeConflict(courses)) {
            return false;
        }

        return courses.add(course);
    }

    /**
     * adds a list of courses to the student's course list
     * 
     * @param coursesToAdd list of courses to be added
     * @return true if all courses added successfully
     */
    public boolean addCourses(Course[] coursesToAdd) {
        if (coursesToAdd == null || coursesToAdd.length == 0) {
            return false;
        }

        boolean allAdded = true;
        for (Course course : coursesToAdd) {
            if (!addCourse(course)) {
                allAdded = false;
            }
        }
        return allAdded;
    }

    /**
     * removes the given course  from the student's course list
     * 
     * @param course target course
     * @return true if given course was successfully removed
     */
    public boolean dropCourse(Course course) {
        if (course == null) {
            return false;
        }
        return courses.remove(course);
    }

    /**
     * drops a list of courses
     * 
     * @param coursesToRemove list of courses to be removed
     * @return true if all courses are removed successfully otherwise false
     */
    public boolean dropCourses(Course[] coursesToRemove) {
        if (coursesToRemove == null || coursesToRemove.length == 0) {
            return false;
        }

        boolean allRemoved = true;
        for (Course course : coursesToRemove) {
            if (!dropCourse(course)) {
                allRemoved = false;
            }
        }
        return allRemoved;
    }

    /**
     * clears the course list of the student
     */
    public void dropAllCourses() {
        courses.clear();
    }

    /**
     * displays the courses currently in the student's schedule
     */
    public void displayCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses scheduled.");
            return;
        }

        for (Course course : courses) {
            System.out.println(course);
        }
    }

    /**
     * gets the students current credit count
     * 
     * @return
     */
    public double getTotalCredits() {
        double total = 0.0;
        for (Course course : courses) {
            if (course != null) {
                total += course.getCredits();
            }
        }
        return total;
    }

    /**
     * checks wether a given course is already in the students course list
     * 
     * @param course a target course
     * @return true if already enrolled false otherwise
     */
    private boolean alreadyEnrolled(Course course) {
        return courses.contains(course);
    }

    /**
     * checks wether adding a given course to the students current courseload would exceed max credits
     * 
     * @param course the course in question
     * @return true if course would exceed max credits, false otherwise
     */
    private boolean wouldExceedMaxCredits(Course course) {
        return getTotalCredits() + course.getCredits() > MAX_CREDITS;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", courses=" + courses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return id == student.id
                && Objects.equals(firstName, student.firstName)
                && Objects.equals(lastName, student.lastName)
                && Objects.equals(courses, student.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, courses);
    }
}
