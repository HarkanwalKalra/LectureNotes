package com.example.android.notes;

public class FileDetails {

    private String fileName;

    private String subjectName;

    private String fileUrl;

    private String fileSize;

    public FileDetails(String name, String course, String fileUrl, String fileSize) {

        if (name.trim().equals("")) {
            name = "No Name";
        }
        if (course.trim().equals("")) {
            course = "No Course";
        }
        this.fileName = name;
        this.subjectName = course;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }

    public FileDetails() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String courseName) {
        this.subjectName = courseName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
