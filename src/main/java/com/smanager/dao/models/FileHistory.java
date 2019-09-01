package com.smanager.dao.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "FILE_HISTORY")
public class FileHistory {
    @Column(name = "file_history_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String path;

    private Date modificationDate;

    private String fileType;

    private Long referencedId;

    public FileHistory() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(Long referencedId) {
        this.referencedId = referencedId;
    }
}
