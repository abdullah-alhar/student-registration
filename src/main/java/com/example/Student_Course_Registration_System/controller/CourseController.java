package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.CourseCategory;
import com.example.Student_Course_Registration_System.service.CourseService;
import com.example.Student_Course_Registration_System.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseCategoryService categoryService;

    @GetMapping("/courses")
    public String getAllCourses(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "course";
    }

    @PostMapping("/courses/add")
    public String addCourse(
            @RequestParam String courseName,
            @RequestParam int credits,
            @RequestParam String description,
            @RequestParam int maxStudents,
            @RequestParam String categoryId,
            RedirectAttributes redirectAttributes) {

        String courseId = "CRS" + System.currentTimeMillis();
        CourseCategory category = categoryService.getCategoryById(categoryId);
        Course course = new Course(courseId, courseName, credits, description, maxStudents, category);
        String error = courseService.addCourse(course);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Course added successfully");
        }
        return "redirect:/courses";
    }

    @PostMapping("/courses/edit/{courseId}")
    public String updateCourse(
            @PathVariable String courseId,
            @RequestParam String courseName,
            @RequestParam int credits,
            @RequestParam String description,
            @RequestParam int maxStudents,
            @RequestParam String categoryId,
            RedirectAttributes redirectAttributes) {

        CourseCategory category = categoryService.getCategoryById(categoryId);
        Course course = new Course(courseId, courseName, credits, description, maxStudents, category);
        String error = courseService.updateCourse(course);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Course updated successfully");
        }
        return "redirect:/courses";
    }

    @GetMapping("/courses/delete/{courseId}")
    public String deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return "redirect:/courses";
    }

    @GetMapping("/courses/available")
    public String getAvailableCourses(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("courses", courseService.getAvailableCourses());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "course";
    }

    @GetMapping("/courses/search")
    public String searchCourse(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryId,
            Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        if (name != null && !name.isEmpty()) {
            model.addAttribute("courses", courseService.searchByName(name));
        } else if (categoryId != null && !categoryId.isEmpty()) {
            model.addAttribute("courses", courseService.searchByCategory(categoryId));
        } else {
            model.addAttribute("courses", courseService.getAllCourses());
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "course";
    }
}