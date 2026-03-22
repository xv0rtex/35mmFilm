package com.filmpin.service;

import com.filmpin.entity.Camera;
import com.filmpin.entity.FilmRoll;
import com.filmpin.entity.FilmRollStatus;
import com.filmpin.repository.CameraRepository;
import com.filmpin.repository.FilmRollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmRollService {

    @Autowired
    private FilmRollRepository filmRollRepository;
    
    @Autowired
    private CameraService cameraService;

    public List<FilmRoll> getRollsByCameraId(Long cameraId) {
        // Just checking ownership of the camera to be safe
        cameraService.getCameraById(cameraId);
        return filmRollRepository.findByCameraIdOrderByCreatedAtDesc(cameraId);
    }
    
    public List<FilmRoll> getActiveRollsByUser(String username) {
        return filmRollRepository.findByUsernameAndStatus(username, FilmRollStatus.IN_USE);
    }
    
    public FilmRoll getRollById(Long id) {
        FilmRoll roll = filmRollRepository.findById(id).orElseThrow(() -> new RuntimeException("Roll not found"));
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (!roll.getCamera().getUser().getUsername().equals(currentUsername)) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        return roll;
    }

    public void saveRoll(FilmRoll roll, Long cameraId) {
        Camera camera = cameraService.getCameraById(cameraId);
        roll.setCamera(camera);
        filmRollRepository.save(roll);
    }

    public void updateRoll(Long id, FilmRoll rollDetails) {
        FilmRoll roll = getRollById(id);
        roll.setName(rollDetails.getName());
        roll.setBrand(rollDetails.getBrand());
        roll.setIso(rollDetails.getIso());
        roll.setPurchaseDate(rollDetails.getPurchaseDate());
        roll.setExpiryDate(rollDetails.getExpiryDate());
        roll.setDescription(rollDetails.getDescription());
        roll.setFilmPrice(rollDetails.getFilmPrice());
        roll.setDevelopmentPrice(rollDetails.getDevelopmentPrice());
        roll.setStatus(rollDetails.getStatus());
        roll.setPushPull(rollDetails.getPushPull());
        roll.setLoadDate(rollDetails.getLoadDate());
        roll.setFilmType(rollDetails.getFilmType());
        
        filmRollRepository.save(roll);
    }

    public void deleteRoll(Long id) {
        filmRollRepository.deleteById(id);
    }
}
