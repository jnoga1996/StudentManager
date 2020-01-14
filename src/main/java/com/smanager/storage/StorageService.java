package com.smanager.storage;

import com.smanager.dao.models.ISaveable;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    String store(MultipartFile file, Long id);

    String storeAssignment(MultipartFile file, Long id);

    String storeSolution(MultipartFile file, Long id);

    Stream<Path> loadAll();

    Stream<Path> loadAllByType(Class entryClass);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String path);
}
