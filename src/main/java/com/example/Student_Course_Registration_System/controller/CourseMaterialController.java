package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.CourseMaterial;
import com.example.Student_Course_Registration_System.model.Registration;
import com.example.Student_Course_Registration_System.service.CourseMaterialService;
import com.example.Student_Course_Registration_System.service.CourseService;
import com.example.Student_Course_Registration_System.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.example.Student_Course_Registration_System.enums.RegistrationStatus;

@Controller
public class CourseMaterialController {

    @Autowired
    private CourseMaterialService courseMaterialService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RegistrationService registrationService;

    private static final String MATERIAL_UPLOAD_PATH = "uploads/materials/";

    // View materials — students only see materials for their registered courses
    @GetMapping("/materials")
    public String getAllMaterials(Model model, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");
        model.addAttribute("userRole", role);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", userId);

        if ("STUDENT".equals(role)) {
            // Only show materials for courses the student is registered in AND approved
            List<Registration> registrations = registrationService.getRegistrationsByStudentId(userId);
            Set<String> registeredCourseIds = new HashSet<>();
            for (Registration reg : registrations) {
                if (reg.getStatus() == RegistrationStatus.APPROVED) {
                    registeredCourseIds.add(reg.getCourse().getCourseId());
                }
            }
            List<CourseMaterial> allMaterials = courseMaterialService.getAllMaterials();
            List<CourseMaterial> filteredMaterials = new ArrayList<>();
            for (CourseMaterial mat : allMaterials) {
                if (registeredCourseIds.contains(mat.getCourseId())) {
                    filteredMaterials.add(mat);
                }
            }
            model.addAttribute("materials", filteredMaterials);
        } else {
            model.addAttribute("materials", courseMaterialService.getAllMaterials());
        }

        model.addAttribute("courses", courseService.getAllCourses());
        return "material";
    }

    // View materials by course (used by students)
    @GetMapping("/materials/course/{courseId}")
    public String getMaterialsByCourse(@PathVariable String courseId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("materials", courseMaterialService.getMaterialsByCourseId(courseId));
        model.addAttribute("courses", courseService.getAllCourses());
        return "material";
    }

    // Add new material — materialId and fileType are auto-generated server-side
    @PostMapping("/materials/add")
    public String addMaterial(
            @RequestParam String title,
            @RequestParam String courseId,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        String role = (String) session.getAttribute("userRole");
        if (!"LECTURER".equals(role) && !"ADMIN".equals(role)) {
            return "redirect:/materials";
        }

        // Auto-generate unique materialId
        String materialId = "MAT" + System.currentTimeMillis();

        // Auto-detect fileType from uploaded file extension
        String originalFileName = file.getOriginalFilename();
        String fileType = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileType = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        }

        CourseMaterial material = new CourseMaterial(materialId, title, fileType, courseId);
        courseMaterialService.addCourseMaterial(material, file);
        return "redirect:/materials";
    }

    // Update material title
    @PostMapping("/materials/edit/{materialId}")
    public String updateMaterial(
            @PathVariable String materialId,
            @RequestParam String title,
            @RequestParam String courseId,
            HttpSession session) {

        String role = (String) session.getAttribute("userRole");
        if (!"LECTURER".equals(role) && !"ADMIN".equals(role)) {
            return "redirect:/materials";
        }

        CourseMaterial material = courseMaterialService.getMaterialById(materialId);
        if (material != null) {
            material.setTitle(title);
            courseMaterialService.updateCourseMaterial(material);
        }
        return "redirect:/materials";
    }

    // Delete material
    @GetMapping("/materials/delete/{materialId}")
    public String deleteMaterial(@PathVariable String materialId, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"LECTURER".equals(role) && !"ADMIN".equals(role)) {
            return "redirect:/materials";
        }
        courseMaterialService.deleteCourseMaterial(materialId);
        return "redirect:/materials";
    }

    // Download/view material file
    @GetMapping("/materials/download/{materialId}")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable String materialId,
                                                     HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (role == null) {
            return ResponseEntity.status(403).build();
        }

        CourseMaterial material = courseMaterialService.getMaterialById(materialId);
        if (material == null || material.getFileName() == null
                || material.getFileName().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(MATERIAL_UPLOAD_PATH).toAbsolutePath()
                    .resolve(material.getFileName());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                String name = material.getFileName().toLowerCase();
                if (name.endsWith(".pdf"))       contentType = "application/pdf";
                else if (name.endsWith(".pptx")) contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                else if (name.endsWith(".ppt"))  contentType = "application/vnd.ms-powerpoint";
                else if (name.endsWith(".docx")) contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                else if (name.endsWith(".doc"))  contentType = "application/msword";
                else if (name.endsWith(".png"))  contentType = "image/png";
                else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) contentType = "image/jpeg";
                else                             contentType = "application/octet-stream";
            }

            boolean openInBrowser = contentType.equals("application/pdf")
                    || contentType.startsWith("image/");
            String disposition = openInBrowser ? "inline" : "attachment";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename=\"" + material.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}