package com.example.myserver.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.MalformedURLException;

@Service
public class FileSystemStorageService implements FileStorageService {

    private final Path rootLocation;

    public FileSystemStorageService() {
        // Define the root location path relative to the location of this class file when executed
        this.rootLocation = Paths.get("../FileStorage").toAbsolutePath().normalize();
        System.out.println("Resolved Path: " + this.rootLocation);
        try {
            // Ensure the directory exists
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }
        Path destinationFile = this.rootLocation.resolve(
            Paths.get(file.getOriginalFilename()))
            .normalize();
        if (!destinationFile.getParent().equals(this.rootLocation)) {
            throw new StorageException("Cannot store file outside current directory.");
        }
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Resource loadAsResource(String filename) throws MalformedURLException {
        Path file = rootLocation.resolve(filename).normalize();
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
    }
}
