package com.filmpin.service;

import com.filmpin.entity.FilmRoll;
import com.filmpin.entity.Photo;
import com.filmpin.repository.FilmRollRepository;
import com.filmpin.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FilmRollService filmRollService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<Photo> getPhotosByRollId(Long rollId) {
        filmRollService.getRollById(rollId); // Check ownership
        List<Photo> photos = photoRepository.findByFilmRollId(rollId);
        photos.sort(java.util.Comparator
                .comparing((Photo p) -> p.getImageUrl() != null)
                .thenComparing(Photo::getPhotoNumber));
        return photos;
    }

    public long getTotalPhotosByUser(String username) {
        return photoRepository.countPhotosByUsername(username);
    }

    public List<Photo> getAllPhotosByUser(String username, String sortDirection) {
        Sort sort;
        if ("asc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Direction.ASC, "timestamp").and(Sort.by(Sort.Direction.ASC, "id"));
        } else {
            sort = Sort.by(Sort.Direction.DESC, "timestamp").and(Sort.by(Sort.Direction.DESC, "id"));
        }
        return photoRepository.findAllByUsername(username, sort);
    }

    public void addPhoto(Long rollId, Double latitude, Double longitude, String aperture, String exposure, String whiteBalance) {
        FilmRoll roll = filmRollService.getRollById(rollId);

        Integer maxNumber = photoRepository.findMaxPhotoNumberByRollId(rollId);
        int nextNumber = (maxNumber == null) ? 1 : maxNumber + 1;

        Photo photo = new Photo();
        photo.setFilmRoll(roll);
        photo.setPhotoNumber(nextNumber);
        photo.setTimestamp(LocalDateTime.now());
        photo.setLatitude(latitude != null ? latitude : 0.0);
        photo.setLongitude(longitude != null ? longitude : 0.0);
        photo.setAperture(aperture);
        photo.setExposure(exposure);
        photo.setWhiteBalance(whiteBalance);

        photoRepository.save(photo);
    }

    public Photo getPhotoById(Long id) {
        Photo photo = photoRepository.findById(id).orElseThrow(() -> new RuntimeException("Photo not found"));
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (!photo.getFilmRoll().getCamera().getUser().getUsername().equals(currentUsername)) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        return photo;
    }

    public void updatePhoto(Long id, Double latitude, Double longitude, LocalDateTime timestamp, String aperture, String exposure, String whiteBalance) {
        Photo photo = getPhotoById(id);
        if (latitude != null) photo.setLatitude(latitude);
        if (longitude != null) photo.setLongitude(longitude);
        if (timestamp != null) photo.setTimestamp(timestamp);
        if (aperture != null) photo.setAperture(aperture);
        if (exposure != null) photo.setExposure(exposure);
        if (whiteBalance != null) photo.setWhiteBalance(whiteBalance);
        photoRepository.save(photo);
    }

    public void uploadImage(Long id, MultipartFile imageFile) throws IOException {
        Photo photo = getPhotoById(id);
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
                photo.setImageUrl("/images/uploads/" + uniqueFileName);
            }
            photoRepository.save(photo);
        }
    }

    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }
}
