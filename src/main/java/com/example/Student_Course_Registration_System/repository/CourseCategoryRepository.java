package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.CourseCategory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseCategoryRepository {

    private static final String FILE_PATH = "src/main/resources/data/courseCategories.txt";

    // Save category to txt file
    public void save(CourseCategory category) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(category.getCategoryId() + "," +
                    category.getCategoryName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving category: " + e.getMessage());
        }
    }

    // Find all categories from txt file
    public List<CourseCategory> findAll() {
        List<CourseCategory> categories = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    CourseCategory category = new CourseCategory(
                            parts[0], // categoryId
                            parts[1]  // categoryName
                    );
                    categories.add(category);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading categories: " + e.getMessage());
        }
        return categories;
    }

    // Find category by ID
    public CourseCategory findById(String categoryId) {
        List<CourseCategory> categories = findAll();
        for (CourseCategory category : categories) {
            if (category.getCategoryId().equals(categoryId)) {
                return category;
            }
        }
        return null;
    }

    // Update category in txt file
    public void update(CourseCategory updatedCategory) {
        List<CourseCategory> categories = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (CourseCategory category : categories) {
                if (category.getCategoryId().equals(updatedCategory.getCategoryId())) {
                    writer.write(updatedCategory.getCategoryId() + "," +
                            updatedCategory.getCategoryName());
                } else {
                    writer.write(category.getCategoryId() + "," +
                            category.getCategoryName());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating category: " + e.getMessage());
        }
    }

    // Delete category from txt file
    public void delete(String categoryId) {
        List<CourseCategory> categories = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (CourseCategory category : categories) {
                if (!category.getCategoryId().equals(categoryId)) {
                    writer.write(category.getCategoryId() + "," +
                            category.getCategoryName());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting category: " + e.getMessage());
        }
    }
}