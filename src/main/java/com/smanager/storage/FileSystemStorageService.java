package com.smanager.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.ISaveable;
import com.smanager.dao.models.Solution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public String storeSolution(MultipartFile file, Long id) {
        return storeFile("solution", file, id);
    }

    @Override
    public void store(MultipartFile file) {
        storeFile("assignment", file, Long.valueOf(-1));
    }

    @Override
    public String store(MultipartFile file, Long id) {
        return storeFile("assignment", file, id);
    }

    private String storeFile(String prefix, MultipartFile file, Long id) {
        String path = (id > 0) ?
                (prefix + "." + id + "." + file.getOriginalFilename())
                : prefix + "." + file.getOriginalFilename();
        String filename = StringUtils.cleanPath(path);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
                return path;
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Stream<Path> loadAllByType(Class entryClass) {
            String prefix = "";
            if (entryClass == Assignment.class) {
                prefix = "assignment";
            } else if (entryClass == Solution.class) {
                prefix = "solution";
            }
            final String className = prefix;
            Stream<Path> files = loadAll();
            return files.filter(file -> file.getFileName().toString().contains(className));
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void delete(String path) {
        try {
            Path filename = rootLocation.resolve(path);
            FileSystemUtils.deleteRecursively(filename);
        } catch (IOException ex) {
            System.out.println("File deletion failed!");
        }
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

}
