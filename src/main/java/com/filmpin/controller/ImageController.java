package com.filmpin.controller;

import com.filmpin.entity.Camera;
import com.filmpin.entity.Photo;
import com.filmpin.repository.CameraRepository;
import com.filmpin.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class ImageController {

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/images/uploads/thumb/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveThumbnail(@PathVariable String filename) {
        String originalImagePath = "/images/uploads/" + filename;
        
        // Check if user is authenticated and owns the image
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required to access this image");
        }
        
        String currentUsername = auth.getName();
        
        boolean isAuthorized = false;

        // Check Photo
        Optional<Photo> photo = photoRepository.findByImageUrl(originalImagePath);
        if (photo.isPresent()) {
            if (photo.get().getFilmRoll().getCamera().getUser().getUsername().equals(currentUsername)) {
                isAuthorized = true;
            }
        }
        
        // Check Camera if not authorized yet
        if (!isAuthorized) {
            Optional<Camera> camera = cameraRepository.findByImagePath(originalImagePath);
            if (camera.isPresent()) {
                if (camera.get().getUser().getUsername().equals(currentUsername)) {
                    isAuthorized = true;
                }
            }
        }

        if (!isAuthorized) {
            throw new AccessDeniedException("Access denied to this image thumbnail");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path originalFile = uploadPath.resolve(filename).normalize();
            Path thumbFile = uploadPath.resolve("thumb_" + filename).normalize();
            
            // Prevent directory traversal
            if (!originalFile.startsWith(uploadPath) || !thumbFile.startsWith(uploadPath)) {
                return ResponseEntity.badRequest().build();
            }

            if (!Files.exists(thumbFile) && Files.exists(originalFile)) {
                // Generate thumbnail on the fly
                net.coobird.thumbnailator.Thumbnails.of(originalFile.toFile())
                    .size(400, 400)
                    .toFile(thumbFile.toFile());
            }

            Resource resource = new UrlResource(thumbFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(thumbFile);
                if (contentType == null) {
                    contentType = "image/jpeg";
                }
                
                return ResponseEntity.ok()
                        .header("Cache-Control", "no-cache, private")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/images/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        String imagePath = "/images/uploads/" + filename;
        
        // Check if user is authenticated and owns the image (either as camera image or photo image)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required to access this image");
        }
        
        String currentUsername = auth.getName();
        
        boolean isAuthorized = false;

        // Check Camera
        Optional<Camera> camera = cameraRepository.findByImagePath(imagePath);
        if (camera.isPresent()) {
            if (camera.get().getUser().getUsername().equals(currentUsername)) {
                isAuthorized = true;
            }
        }
        
        // Check Photo if not authorized yet
        if (!isAuthorized) {
            Optional<Photo> photo = photoRepository.findByImageUrl(imagePath);
            if (photo.isPresent()) {
                if (photo.get().getFilmRoll().getCamera().getUser().getUsername().equals(currentUsername)) {
                    isAuthorized = true;
                }
            }
        }

        if (!isAuthorized) {
            throw new AccessDeniedException("Access denied to this image");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path file = uploadPath.resolve(filename).normalize();
            
            // Prevent directory traversal
            if (!file.startsWith(uploadPath)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .header("Cache-Control", "no-cache, private")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
