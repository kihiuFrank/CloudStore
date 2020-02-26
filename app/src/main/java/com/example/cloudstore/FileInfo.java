package com.example.cloudstore;

import java.io.Serializable;

public class FileInfo implements Serializable {

    private String id;
    private String title;
    private String description;
    private String fileUrl;

    public FileInfo() {

    }


    public FileInfo(String title, String description, String fileUrl) {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setFileUrl(fileUrl);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}