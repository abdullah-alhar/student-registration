package com.example.Student_Course_Registration_System.model;

public class Course {

    private String courseId;
    private String courseName;
    private int credits;
    private String description;
    private int maxStudents;
    private int enrolledStudents;
    private CourseCategory category;

    // Composition - Course owns CourseCategory
    public Course(String courseId, String courseName, int credits, String description, int maxStudents, CourseCategory category) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.description = description;
        this.maxStudents = maxStudents;
        this.enrolledStudents = 0;
        this.category = category;
    }

    // Getters for show output on html (frondend)
    public String getCourseId() {
        return courseId;
    }
    public String getCourseName() {
        return courseName;
    }
    public int getCredits() {
        return credits;
    }
    public String getDescription() {
        return description;
    }
    public int getMaxStudents() {
        return maxStudents;
    }
    public int getEnrolledStudents() {
        return enrolledStudents;
    }
    public CourseCategory getCategory() {
        return category;
    }

    // changing variables
    public void setDescription(String description) {
        this.description = description;
    }
    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    // check the course has avialble seat or not
    public boolean isAvailable() {
        return enrolledStudents < maxStudents;
    }

    public void addEnrolledStudent() {
        if (isAvailable()) {
            enrolledStudents++;
            System.out.println("Student enrolled successfully");
        } else {
            System.out.println("Course is full");
        }
    }

    public void removeEnrolledStudent() {
        if (enrolledStudents > 0) {
            enrolledStudents--;
        }
    }
}