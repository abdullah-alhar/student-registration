package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Feedback;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.service.CourseService;
import com.example.Student_Course_Registration_System.service.FeedbackService;
import com.example.Student_Course_Registration_System.service.StudentService;
import com.example.Student_Course_Registration_System.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/feedbacks")
    public String getAllFeedbacks(Model model, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");
        model.addAttribute("userRole", role);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", userId);
        
        if ("STUDENT".equals(role)) {
            model.addAttribute("feedbacks", feedbackService.getFeedbacksByStudentId(userId));
        } else if ("LECTURER".equals(role)) {
            // Get courses taught by lecturer via schedules
            List<com.example.Student_Course_Registration_System.model.Schedule> schedules = scheduleService.getSchedulesByLecturerId(userId);
            List<Feedback> lecturerFeedbacks = new java.util.ArrayList<>();
            java.util.Set<String> processedCourses = new java.util.HashSet<>();
            for (com.example.Student_Course_Registration_System.model.Schedule s : schedules) {
                String cId = s.getCourse().getCourseId();
                if (!processedCourses.contains(cId)) {
                    processedCourses.add(cId);
                    lecturerFeedbacks.addAll(feedbackService.getFeedbacksByCourseId(cId));
                }
            }
            model.addAttribute("feedbacks", lecturerFeedbacks);
        } else {
            model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        }
        
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        return "feedback";
    }

    @PostMapping("/feedbacks/add")
    public String addFeedback(
            @RequestParam String studentId,
            @RequestParam String courseId,
            @RequestParam int rating,
            @RequestParam String comment,
            @RequestParam String date) {
        Student student = studentService.getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);
        feedbackService.addFeedback(student, course, rating, comment);
        return "redirect:/feedbacks";
    }

    @PostMapping("/feedbacks/edit/{feedbackId}")
    public String updateFeedback(
            @PathVariable String feedbackId,
            @RequestParam int rating,
            @RequestParam String comment) {
        feedbackService.updateFeedback(feedbackId, rating, comment);
        return "redirect:/feedbacks";
    }

    @GetMapping("/feedbacks/submit/{feedbackId}")
    public String submitFeedback(@PathVariable String feedbackId) {
        feedbackService.submitFeedback(feedbackId);
        return "redirect:/feedbacks";
    }

    @GetMapping("/feedbacks/delete/{feedbackId}")
    public String deleteFeedback(@PathVariable String feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return "redirect:/feedbacks";
    }

    @GetMapping("/feedbacks/student/{studentId}")
    public String getFeedbacksByStudent(@PathVariable String studentId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByStudentId(studentId));
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        return "feedback";
    }

    @GetMapping("/feedbacks/course/{courseId}")
    public String getFeedbacksByCourse(@PathVariable String courseId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByCourseId(courseId));
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("averageRating", feedbackService.getAverageRating(courseId));
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        return "feedback";
    }
}