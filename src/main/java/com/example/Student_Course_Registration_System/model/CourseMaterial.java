package com.example.Student_Course_Registration_System.model;

public class CourseMaterial {

    private String materialId;
    private String title;
    private String fileType;
    private String courseId;
    private String fileName;

    public CourseMaterial(String materialId, String title, String fileType, String courseId) {
        this.materialId = materialId;
        this.title = title;
        this.fileType = fileType;
        this.courseId = courseId;
        this.fileName = "";
    }

    // Getters
    public String getMaterialId() { return materialId; }
    public String getTitle()      { return title; }
    public String getFileType()   { return fileType; }
    public String getCourseId()   { return courseId; }
    public String getFileName()   { return fileName; }

    // Setters
    public void setTitle(String title)     { this.title = title; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    // Controlled method to save file name
    public void saveFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            this.fileName = fileName;
            System.out.println("File saved successfully: " + fileName);
        } else {
            System.out.println("Invalid file name");
        }
    }
}