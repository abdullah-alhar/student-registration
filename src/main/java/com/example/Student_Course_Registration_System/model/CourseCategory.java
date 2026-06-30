package com.example.Student_Course_Registration_System.model;

public class CourseCategory {

    private String categoryId;
    private String categoryName;

    // Composition - CourseCategory is owned by Course
    public CourseCategory(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }

    // Setter only for changeable field
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}