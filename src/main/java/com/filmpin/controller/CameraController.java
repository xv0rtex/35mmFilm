package com.filmpin.controller;

import com.filmpin.entity.Camera;
import com.filmpin.service.CameraService;
import com.filmpin.service.FilmRollService;
import com.filmpin.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class CameraController {

    @Autowired
    private CameraService cameraService;
    
    @Autowired
    private FilmRollService filmRollService; // Will be implemented next

    @Autowired
    private PhotoService photoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("cameras", cameraService.getCamerasByUser(username));
        model.addAttribute("activeRolls", filmRollService.getActiveRollsByUser(username));
        model.addAttribute("username", username);

        long totalPhotos = photoService.getTotalPhotosByUser(username);
        model.addAttribute("totalPhotos", totalPhotos);

        double lengthCm = totalPhotos * 3.8;
        if (lengthCm >= 100) {
            model.addAttribute("filmLength", String.format("%.2f m", lengthCm / 100.0));
        } else {
            model.addAttribute("filmLength", String.format("%.1f cm", lengthCm));
        }

        return "dashboard";
    }

    @GetMapping("/gallery")
    public String gallery(@RequestParam(value = "sort", required = false, defaultValue = "desc") String sort, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        model.addAttribute("photos", photoService.getAllPhotosByUser(username, sort));
        model.addAttribute("username", username);
        model.addAttribute("currentSort", sort);
        
        return "gallery";
    }

    @GetMapping("/cameras/new")
    public String showAddCameraForm(Model model) {
        model.addAttribute("camera", new Camera());
        return "camera-form";
    }

    @PostMapping("/cameras")
    public String addCamera(@ModelAttribute("camera") Camera camera,
                            @RequestParam("image") MultipartFile image,
                            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            cameraService.saveCamera(camera, auth.getName(), image);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image");
            return "camera-form";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/cameras/{id}")
    public String viewCamera(@PathVariable Long id, Model model) {
        Camera camera = cameraService.getCameraById(id);
        model.addAttribute("camera", camera);
        
        // Add rolls if analog
        if ("ANALOG".equals(camera.getType().name())) {
             model.addAttribute("rolls", filmRollService.getRollsByCameraId(id));
        }
        
        return "camera-detail";
    }

    @GetMapping("/cameras/{id}/edit")
    public String showEditCameraForm(@PathVariable Long id, Model model) {
        Camera camera = cameraService.getCameraById(id);
        model.addAttribute("camera", camera);
        return "camera-form";
    }

    @PostMapping("/cameras/{id}")
    public String updateCamera(@PathVariable Long id, 
                               @ModelAttribute("camera") Camera camera,
                               @RequestParam(value = "image", required = false) MultipartFile image,
                               Model model) {
        try {
            cameraService.updateCamera(id, camera, image);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image");
            return "camera-form";
        }
        return "redirect:/cameras/" + id;
    }
}
