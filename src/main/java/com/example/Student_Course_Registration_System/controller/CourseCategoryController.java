package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.CourseCategory;
import com.example.Student_Course_Registration_System.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService categoryService;

    @GetMapping("/categories")
    public String getAllCategories(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category";
    }

    @PostMapping("/categories/add")
    public String addCategory(
            @RequestParam String categoryName,
            RedirectAttributes redirectAttributes) {
        String categoryId = "CAT" + System.currentTimeMillis();
        CourseCategory category = new CourseCategory(categoryId, categoryName);
        String error = categoryService.addCategory(category);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Category added successfully");
        }
        return "redirect:/categories";
    }

    @PostMapping("/categories/edit/{categoryId}")
    public String updateCategory(
            @PathVariable String categoryId,
            @RequestParam String categoryName,
            RedirectAttributes redirectAttributes) {
        CourseCategory category = new CourseCategory(categoryId, categoryName);
        String error = categoryService.updateCategory(category);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{categoryId}")
    public String deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return "redirect:/categories";
    }

    @GetMapping("/categories/search")
    public String searchCategory(@RequestParam String name, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("categories", categoryService.searchByName(name));
        return "category";
    }
}