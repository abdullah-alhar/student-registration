package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.model.CourseMaterial;
import com.example.Student_Course_Registration_System.repository.CourseMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CourseMaterialService {

    @Autowired
    private CourseMaterialRepository courseMaterialRepository;

    // Add new course material with file upload
    public void addCourseMaterial(CourseMaterial material, MultipartFile file) {
        // Check if material ID already exists
        CourseMaterial existing = courseMaterialRepository.findById(material.getMaterialId());
        if (existing != null) {
            System.out.println("Material ID already exists");
            return;
        }
        // Check if file is not empty
        if (file == null || file.isEmpty()) {
            System.out.println("Please upload a file");
            return;
        }
        // Check file type
        String originalFileName = file.getOriginalFilename();
        if (!isValidFileType(originalFileName)) {
            System.out.println("Invalid file type. Allowed: PDF, PPTX, DOC, DOCX, PNG, JPG");
            return;
        }
        // Save uploaded file
        String savedFileName = courseMaterialRepository.saveUploadedFile(file);
        if (savedFileName == null) {
            System.out.println("Error uploading file");
            return;
        }
        // Save file name to material
        material.saveFile(savedFileName);
        courseMaterialRepository.save(material);
        System.out.println("Course material added successfully");
    }

    // Get all course materials
    public List<CourseMaterial> getAllMaterials() {
        return courseMaterialRepository.findAll();
    }

    // Get material by ID
    public CourseMaterial getMaterialById(String materialId) {
        CourseMaterial material = courseMaterialRepository.findById(materialId);
        if (material == null) {
            System.out.println("Material not found");
        }
        return material;
    }

    // Get all materials by course ID
    public List<CourseMaterial> getMaterialsByCourseId(String courseId) {
        return courseMaterialRepository.findByCourseId(courseId);
    }

    // Update course material
    public void updateCourseMaterial(CourseMaterial material) {
        CourseMaterial existing = courseMaterialRepository.findById(material.getMaterialId());
        if (existing == null) {
            System.out.println("Material not found");
            return;
        }
        courseMaterialRepository.update(material);
        System.out.println("Course material updated successfully");
    }

    // Delete course material
    public void deleteCourseMaterial(String materialId) {
        CourseMaterial existing = courseMaterialRepository.findById(materialId);
        if (existing == null) {
            System.out.println("Material not found");
            return;
        }
        if (existing.getFileName() != null && !existing.getFileName().isEmpty()) {
            courseMaterialRepository.deleteUploadedFile(existing.getFileName());
        }
        courseMaterialRepository.delete(materialId);
        System.out.println("Course material deleted successfully");
    }

    // Validate file type
    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf")  ||
                lower.endsWith(".pptx") ||
                lower.endsWith(".ppt")  ||
                lower.endsWith(".doc")  ||
                lower.endsWith(".docx") ||
                lower.endsWith(".png")  ||
                lower.endsWith(".jpg")  ||
                lower.endsWith(".jpeg");
    }

    // Get total material count
    public int getTotalMaterials() {
        return courseMaterialRepository.findAll().size();
    }
}