package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Registration;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.service.CourseService;
import com.example.Student_Course_Registration_System.service.RegistrationService;
import com.example.Student_Course_Registration_System.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/registrations")
    public String getAllRegistrations(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));

        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");

        //if student has login
        if ("STUDENT".equals(role)) {
            model.addAttribute("registrations", registrationService.getRegistrationsByStudentId(userId));
        } else { //admin has login
            model.addAttribute("registrations", registrationService.getAllRegistrations());
        }//call all student name in text file for admin view
        model.addAttribute("students", studentService.getAllStudents());
        //find the course are availabe
        model.addAttribute("courses", courseService.getAvailableCourses());
        return "registration";
    }

    @GetMapping("/registrations/register")
    public String registerForCourse(@RequestParam String courseId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        Student student = studentService.getStudentById(userId);
        Course course = courseService.getCourseById(courseId);
        registrationService.addRegistration(student, course);
        return "redirect:/registrations";//return registiatiko page
    }

    @PostMapping("/registrations/add")
    public String addRegistration(@RequestParam String studentId, @RequestParam String courseId) {
        Student student = studentService.getStudentById(studentId);//check stdnt available
        Course course = courseService.getCourseById(courseId);
        registrationService.addRegistration(student, course);
        return "redirect:/registrations";
    }

    @GetMapping("/registrations/pending")
    public String getPendingRegistrations(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("registrations", registrationService.getPendingRegistrations());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAvailableCourses());
        return "registration";
    }

    @GetMapping("/registrations/student/{studentId}")
    public String getRegistrationsByStudent(@PathVariable String studentId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("registrations", registrationService.getRegistrationsByStudentId(studentId));
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAvailableCourses());
        return "registration";
    }

    @GetMapping("/registrations/approve/{registrationId}")
    public String approveRegistration(@PathVariable String registrationId) {
        registrationService.approveRegistration(registrationId);
        return "redirect:/registrations";
    }

    @GetMapping("/registrations/reject/{registrationId}")
    public String rejectRegistration(@PathVariable String registrationId) {
        registrationService.rejectRegistration(registrationId);
        return "redirect:/registrations";
    }

    @GetMapping("/registrations/cancel/{registrationId}")
    public String cancelRegistration(@PathVariable String registrationId) {
        registrationService.cancelRegistration(registrationId);
        return "redirect:/registrations";
    }

    @GetMapping("/registrations/delete/{registrationId}")
    public String deleteRegistration(@PathVariable String registrationId) {
        registrationService.deleteRegistration(registrationId);
        return "redirect:/registrations";
    }
}