package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.CourseMaterial;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseMaterialRepository {

    private static final String FILE_PATH = "src/main/resources/data/courseMaterials.txt";
    private static final String UPLOAD_PATH = "uploads/materials/";

    // Save course material to txt file
    public void save(CourseMaterial material) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(material.getMaterialId() + "," +
                    material.getTitle() + "," +
                    material.getFileType() + "," +
                    material.getCourseId() + "," +
                    material.getFileName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving course material: " + e.getMessage());
        }
    }

    // Save uploaded file to uploads/materials/ folder
    public String saveUploadedFile(org.springframework.web.multipart.MultipartFile file) {
        try {
            // Create uploads/materials/ folder if not exists
            File uploadDir = new File(UPLOAD_PATH).getAbsoluteFile();
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file to folder
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir, fileName);
            file.transferTo(destination);
            return fileName;

        } catch (IOException e) {
            System.out.println("Error uploading file: " + e.getMessage());
            return null;
        }
    }

    // Find all course materials from txt file
    public List<CourseMaterial> findAll() {
        List<CourseMaterial> materials = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    CourseMaterial material = new CourseMaterial(
                            parts[0], // materialId
                            parts[1], // title
                            parts[2], // fileType
                            parts[3]  // courseId
                    );
                    material.saveFile(parts[4]); // fileName
                    materials.add(material);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading course materials: " + e.getMessage());
        }
        return materials;
    }

    // Find material by ID
    public CourseMaterial findById(String materialId) {
        List<CourseMaterial> materials = findAll();
        for (CourseMaterial material : materials) {
            if (material.getMaterialId().equals(materialId)) {
                return material;
            }
        }
        return null;
    }

    // Find all materials by course ID
    public List<CourseMaterial> findByCourseId(String courseId) {
        List<CourseMaterial> materials = findAll();
        List<CourseMaterial> courseMaterials = new ArrayList<>();
        for (CourseMaterial material : materials) {
            if (material.getCourseId().equals(courseId)) {
                courseMaterials.add(material);
            }
        }
        return courseMaterials;
    }

    // Update material in txt file
    public void update(CourseMaterial updatedMaterial) {
        List<CourseMaterial> materials = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (CourseMaterial material : materials) {
                if (material.getMaterialId().equals(updatedMaterial.getMaterialId())) {
                    writer.write(updatedMaterial.getMaterialId() + "," +
                            updatedMaterial.getTitle() + "," +
                            updatedMaterial.getFileType() + "," +
                            updatedMaterial.getCourseId() + "," +
                            updatedMaterial.getFileName());
                } else {
                    writer.write(material.getMaterialId() + "," +
                            material.getTitle() + "," +
                            material.getFileType() + "," +
                            material.getCourseId() + "," +
                            material.getFileName());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating course material: " + e.getMessage());
        }
    }

    // Delete material from txt file
    public void delete(String materialId) {
        List<CourseMaterial> materials = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (CourseMaterial material : materials) {
                if (!material.getMaterialId().equals(materialId)) {
                    writer.write(material.getMaterialId() + "," +
                            material.getTitle() + "," +
                            material.getFileType() + "," +
                            material.getCourseId() + "," +
                            material.getFileName());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting course material: " + e.getMessage());
        }
    }

    // Delete uploaded file from folder
    public void deleteUploadedFile(String fileName) {
        File file = new File(UPLOAD_PATH + fileName);
        if (file.exists()) {
            file.delete();
            System.out.println("File deleted successfully: " + fileName);
        }
    }
}