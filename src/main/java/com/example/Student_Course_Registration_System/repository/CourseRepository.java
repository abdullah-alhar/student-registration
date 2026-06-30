package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.CourseCategory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseRepository {

    private static final String FILE_PATH = "src/main/resources/data/courses.txt";
    private final CourseCategoryRepository categoryRepository = new CourseCategoryRepository();

    // Save course to txt file
    public void save(Course course) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(course.getCourseId() + "," +
                    course.getCourseName() + "," +
                    course.getCredits() + "," +
                    course.getDescription() + "," +
                    course.getMaxStudents() + "," +
                    course.getEnrolledStudents() + "," +
                    course.getCategory().getCategoryId());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving course: " + e.getMessage());
        }
    }

    // Find all courses from txt file
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    CourseCategory category = categoryRepository.findById(parts[6]);
                    Course course = new Course(
                            parts[0], // courseId
                            parts[1], // courseName
                            Integer.parseInt(parts[2]), // credits
                            parts[3], // description
                            Integer.parseInt(parts[4]), // maxStudents
                            category  // category object
                    );
                    courses.add(course);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading courses: " + e.getMessage());
        }
        return courses;
    }

    // Find course by ID
    public Course findById(String courseId) {
        List<Course> courses = findAll();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    // Update course in txt file
    public void update(Course updatedCourse) {
        List<Course> courses = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Course course : courses) {
                if (course.getCourseId().equals(updatedCourse.getCourseId())) {
                    writer.write(updatedCourse.getCourseId() + "," +
                            updatedCourse.getCourseName() + "," +
                            updatedCourse.getCredits() + "," +
                            updatedCourse.getDescription() + "," +
                            updatedCourse.getMaxStudents() + "," +
                            updatedCourse.getEnrolledStudents() + "," +
                            updatedCourse.getCategory().getCategoryId());
                } else {
                    writer.write(course.getCourseId() + "," +
                            course.getCourseName() + "," +
                            course.getCredits() + "," +
                            course.getDescription() + "," +
                            course.getMaxStudents() + "," +
                            course.getEnrolledStudents() + "," +
                            course.getCategory().getCategoryId());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }

    // Delete course from txt file
    public void delete(String courseId) {
        List<Course> courses = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Course course : courses) {
                if (!course.getCourseId().equals(courseId)) {
                    writer.write(course.getCourseId() + "," +
                            course.getCourseName() + "," +
                            course.getCredits() + "," +
                            course.getDescription() + "," +
                            course.getMaxStudents() + "," +
                            course.getEnrolledStudents() + "," +
                            course.getCategory().getCategoryId());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }
}