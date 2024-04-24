package com.example.myserver.filestorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/files")
public class FileController {

	@Autowired
	private FileStorageService fileStorageService;

	// Only admins can upload files
	@PostMapping("/upload")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			fileStorageService.save(file);
			return ResponseEntity.ok("File uploaded successfully!");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
		}
	}

	// File download with security checks
	@GetMapping("/download/{filename}")
	public ResponseEntity<?> downloadFile(@PathVariable String filename, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Unauthorized access");
		}
		try {
			Resource file = fileStorageService.loadAsResource(filename);
			if (file.exists()) {
				return ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(file);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: File cannot be accessed");
		}
	}
}
