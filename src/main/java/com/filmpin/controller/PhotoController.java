package com.filmpin.controller;

import com.filmpin.entity.Photo;
import com.filmpin.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping("/{id}/edit")
    public String showEditPhotoForm(@PathVariable Long id, Model model) {
        Photo photo = photoService.getPhotoById(id);
        model.addAttribute("photo", photo);
        return "photo-edit";
    }

    @PostMapping("/{id}")
    public String updatePhoto(@PathVariable Long id,
                              @RequestParam("latitude") Double latitude,
                              @RequestParam("longitude") Double longitude,
                              @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
                              @RequestParam(value = "aperture", required = false) String aperture,
                              @RequestParam(value = "exposure", required = false) String exposure,
                              @RequestParam(value = "whiteBalance", required = false) String whiteBalance) {
        photoService.updatePhoto(id, latitude, longitude, timestamp, aperture, exposure, whiteBalance);
        Photo photo = photoService.getPhotoById(id);
        return "redirect:/rolls/" + photo.getFilmRoll().getId();
    }

    @PostMapping("/{id}/delete")
    public String deletePhoto(@PathVariable Long id) {
        Photo photo = photoService.getPhotoById(id);
        Long rollId = photo.getFilmRoll().getId();
        photoService.deletePhoto(id);
        return "redirect:/rolls/" + rollId;
    }

    @PostMapping("/{id}/upload")
    public String uploadPhoto(@PathVariable Long id, @RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes) {
        Photo photo = photoService.getPhotoById(id);
        Long rollId = photo.getFilmRoll().getId();
        try {
            photoService.uploadImage(id, image);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image");
        }
        return "redirect:/rolls/" + rollId + "?mode=upload";
    }
}
