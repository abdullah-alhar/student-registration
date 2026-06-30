package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Feedback;
import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FeedbackRepository {

    private static final String FILE_PATH = "src/main/resources/data/feedbacks.txt";
    private final StudentRepository studentRepository = new StudentRepository();
    private final CourseRepository courseRepository = new CourseRepository();

    // Save feedback to txt file
    public void save(Feedback feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(feedback.getFeedbackId() + "," +
                    feedback.getStudent().getStudentId() + "," +
                    feedback.getCourse().getCourseId() + "," +
                    feedback.getRating() + "," +
                    feedback.getComment() + "," +
                    feedback.getDate() + "," +
                    feedback.isSubmitted());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving feedback: " + e.getMessage());
        }
    }

    // Find all feedbacks from txt file
    public List<Feedback> findAll() {
        List<Feedback> feedbacks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Student student = studentRepository.findById(parts[1]);
                    Course course = courseRepository.findById(parts[2]);
                    // Skip feedbacks whose student or course has been deleted
                    if (student == null || course == null) {
                        continue;
                    }
                    Feedback feedback = new Feedback(
                            parts[0],                        // feedbackId
                            student,                         // student object
                            course,                          // course object
                            Integer.parseInt(parts[3]),      // rating
                            parts[4],                        // comment
                            parts[5]                         // date
                    );
                    // Restore submitted status
                    if (Boolean.parseBoolean(parts[6])) {
                        feedback.submitFeedback();
                    }
                    feedbacks.add(feedback);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedbacks: " + e.getMessage());
        }
        return feedbacks;
    }

    // Find feedback by ID
    public Feedback findById(String feedbackId) {
        List<Feedback> feedbacks = findAll();
        for (Feedback feedback : feedbacks) {
            if (feedback.getFeedbackId().equals(feedbackId)) {
                return feedback;
            }
        }
        return null;
    }

    // Find all feedbacks by student ID
    public List<Feedback> findByStudentId(String studentId) {
        List<Feedback> feedbacks = findAll();
        List<Feedback> studentFeedbacks = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            if (feedback.getStudent().getStudentId().equals(studentId)) {
                studentFeedbacks.add(feedback);
            }
        }
        return studentFeedbacks;
    }

    // Find all feedbacks by course ID
    public List<Feedback> findByCourseId(String courseId) {
        List<Feedback> feedbacks = findAll();
        List<Feedback> courseFeedbacks = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            if (feedback.getCourse().getCourseId().equals(courseId)) {
                courseFeedbacks.add(feedback);
            }
        }
        return courseFeedbacks;
    }

    // Update feedback in txt file
    public void update(Feedback updatedFeedback) {
        List<Feedback> feedbacks = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Feedback feedback : feedbacks) {
                if (feedback.getFeedbackId().equals(updatedFeedback.getFeedbackId())) {
                    writer.write(updatedFeedback.getFeedbackId() + "," +
                            updatedFeedback.getStudent().getStudentId() + "," +
                            updatedFeedback.getCourse().getCourseId() + "," +
                            updatedFeedback.getRating() + "," +
                            updatedFeedback.getComment() + "," +
                            updatedFeedback.getDate() + "," +
                            updatedFeedback.isSubmitted());
                } else {
                    writer.write(feedback.getFeedbackId() + "," +
                            feedback.getStudent().getStudentId() + "," +
                            feedback.getCourse().getCourseId() + "," +
                            feedback.getRating() + "," +
                            feedback.getComment() + "," +
                            feedback.getDate() + "," +
                            feedback.isSubmitted());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating feedback: " + e.getMessage());
        }
    }

    // Delete feedback from txt file
    public void delete(String feedbackId) {
        List<Feedback> feedbacks = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Feedback feedback : feedbacks) {
                if (!feedback.getFeedbackId().equals(feedbackId)) {
                    writer.write(feedback.getFeedbackId() + "," +
                            feedback.getStudent().getStudentId() + "," +
                            feedback.getCourse().getCourseId() + "," +
                            feedback.getRating() + "," +
                            feedback.getComment() + "," +
                            feedback.getDate() + "," +
                            feedback.isSubmitted());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting feedback: " + e.getMessage());
        }
    }
}