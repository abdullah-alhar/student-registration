package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Feedback;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Add new feedback
    public void addFeedback(Student student, Course course, int rating, String comment) {
        // Check if student already gave feedback for this course
        List<Feedback> existing = feedbackRepository.findByStudentId(student.getStudentId());
        for (Feedback f : existing) {
            if (f.getCourse().getCourseId().equals(course.getCourseId())) {
                System.out.println("Student already gave feedback for this course");
                return;
            }
        }
        // Check if rating is valid
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5");
            return;
        }
        // Check if comment is not empty
        if (comment == null || comment.trim().isEmpty()) {
            System.out.println("Comment cannot be empty");
            return;
        }
        // Create feedback ID
        String feedbackId = "FBK" + System.currentTimeMillis();
        String date = LocalDate.now().toString();
        Feedback feedback = new Feedback(
                feedbackId,
                student,
                course,
                rating,
                comment,
                date
        );
        feedbackRepository.save(feedback);
        System.out.println("Feedback added successfully");
    }

    // Get all feedbacks
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    // Get feedback by ID
    public Feedback getFeedbackById(String feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            System.out.println("Feedback not found");
        }
        return feedback;
    }

    // Get feedbacks by student ID
    public List<Feedback> getFeedbacksByStudentId(String studentId) {
        return feedbackRepository.findByStudentId(studentId);
    }

    // Get feedbacks by course ID
    public List<Feedback> getFeedbacksByCourseId(String courseId) {
        return feedbackRepository.findByCourseId(courseId);
    }

    // Update feedback before submission
    public void updateFeedback(String feedbackId, int rating, String comment) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            System.out.println("Feedback not found");
            return;
        }
        // Check if rating is valid
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5");
            return;
        }
        // Check if comment is not empty
        if (comment == null || comment.trim().isEmpty()) {
            System.out.println("Comment cannot be empty");
            return;
        }
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedbackRepository.update(feedback);
        System.out.println("Feedback updated successfully");
    }

    // Submit feedback
    public void submitFeedback(String feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            System.out.println("Feedback not found");
            return;
        }
        if (feedback.isSubmitted()) {
            System.out.println("Feedback already submitted");
            return;
        }
        feedback.submitFeedback();
        feedbackRepository.update(feedback);
        System.out.println("Feedback submitted successfully");
    }

    // Delete feedback
    public void deleteFeedback(String feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            System.out.println("Feedback not found");
            return;
        }
        feedbackRepository.delete(feedbackId);
        System.out.println("Feedback deleted successfully");
    }

    // Get average rating for a course
    public double getAverageRating(String courseId) {
        List<Feedback> feedbacks = feedbackRepository.findByCourseId(courseId);
        if (feedbacks.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Feedback feedback : feedbacks) {
            total += feedback.getRating();
        }
        return (double) total / feedbacks.size();
    }

    // Get total feedback count
    public int getTotalFeedbacks() {
        return feedbackRepository.findAll().size();
    }
}