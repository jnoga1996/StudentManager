package com.smanager.services;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.FileHistoryRepository;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class FileUploadHelper {

    private FileHistoryRepository fileHistoryRepository;
    private StorageService storageService;

    @Autowired
    public FileUploadHelper(FileHistoryRepository fileHistoryRepository, StorageService storageService) {
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
    }

    public void saveFileToRepository(JpaRepository repository, MultipartFile file, ISaveable entry) {
        repository.save(entry);
        if (!file.isEmpty()) {
            String path = storageService.store(file, entry.getId());
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(file.getOriginalFilename());
            fileHistory.setModificationDate(new Date(System.currentTimeMillis()));
            fileHistory.setReferencedId(entry.getId());
            String fileType = "";
            if (entry instanceof Assignment && !file.isEmpty()) {
                path = storageService.storeAssignment(file, entry.getId());
                fileType = FileType.ASSIGNMENT.getName();
            } else if (entry instanceof Solution && !file.isEmpty()) {
                path = storageService.storeSolution(file, entry.getId());
                fileType = FileType.SOLUTION.getName();
            }
            fileHistory.setPath(path);
            fileHistory.setFileType(fileType);

            fileHistoryRepository.save(fileHistory);
            entry.setPath(path);
            repository.save(entry);
        } else {
            entry.setPath(null);
            repository.save(entry);
        }
    }

    public void updateFileHistory(ISaveable entry, MultipartFile file) {
        String oldFilePath = entry.getPath();
        if (oldFilePath != null) {
            storageService.delete(entry.getPath());
        }
        String path = "";
        String fileType = "";
        if (!file.isEmpty()) {
            if (entry instanceof Assignment && !file.isEmpty()) {
                path = storageService.storeAssignment(file, entry.getId());
                fileType = FileType.ASSIGNMENT.getName();
            } else if (entry instanceof Solution && !file.isEmpty()) {
                path = storageService.storeSolution(file, entry.getId());
                fileType = FileType.SOLUTION.getName();
            }
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(file.getOriginalFilename());
            fileHistory.setModificationDate(new Date(System.currentTimeMillis()));
            fileHistory.setPath(path);
            fileHistory.setReferencedId(entry.getId());
            fileHistory.setFileType(fileType);
            entry.setPath(path);

            fileHistoryRepository.save(fileHistory);
        }
    }
}
