package com.example.myserver.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;

public interface FileStorageService {
    void save(MultipartFile file) throws IOException;
    Resource loadAsResource(String filename) throws MalformedURLException;
}
