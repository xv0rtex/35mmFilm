package com.filmpin.service;

import com.filmpin.entity.Camera;
import com.filmpin.entity.User;
import com.filmpin.repository.CameraRepository;
import com.filmpin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CameraService {

    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<Camera> getCamerasByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return cameraRepository.findByUserId(user.getId());
    }

    public Camera getCameraById(Long id) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new RuntimeException("Camera not found"));
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (!camera.getUser().getUsername().equals(currentUsername)) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        return camera;
    }

    public void saveCamera(Camera camera, String username, MultipartFile imageFile) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow();
        camera.setUser(user);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                camera.setImagePath("/images/uploads/" + uniqueFileName);
            }
        }

        cameraRepository.save(camera);
    }

    public void updateCamera(Long id, Camera cameraDetails, MultipartFile imageFile) throws IOException {
        Camera camera = getCameraById(id);
        camera.setName(cameraDetails.getName());
        camera.setPurchaseDate(cameraDetails.getPurchaseDate());
        camera.setType(cameraDetails.getType());
        camera.setDescription(cameraDetails.getDescription());

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                camera.setImagePath("/images/uploads/" + uniqueFileName);
            }
        }
        
        cameraRepository.save(camera);
    }
}
