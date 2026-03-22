package com.filmpin.controller;

import com.filmpin.entity.FilmRoll;
import com.filmpin.service.FilmRollService;
import com.filmpin.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FilmRollController {

    @Autowired
    private FilmRollService filmRollService;

    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private com.filmpin.service.CameraService cameraService;

    @GetMapping("/cameras/{cameraId}/rolls/new")
    public String showAddRollForm(@PathVariable Long cameraId, Model model) {
        cameraService.getCameraById(cameraId); // Validates ownership
        FilmRoll roll = new FilmRoll();
        model.addAttribute("roll", roll);
        model.addAttribute("cameraId", cameraId);
        return "roll-form";
    }

    @PostMapping("/cameras/{cameraId}/rolls")
    public String addRoll(@PathVariable Long cameraId, @ModelAttribute("roll") FilmRoll roll) {
        filmRollService.saveRoll(roll, cameraId);
        return "redirect:/cameras/" + cameraId;
    }

    @GetMapping("/rolls/{id}")
    public String viewRoll(@PathVariable Long id, Model model) {
        FilmRoll roll = filmRollService.getRollById(id);
        model.addAttribute("roll", roll);
        model.addAttribute("photos", photoService.getPhotosByRollId(id));
        return "roll-detail";
    }

    @PostMapping("/rolls/{id}/photos")
    public String addPhoto(@PathVariable Long id, 
                           @RequestParam(required = false) Double latitude, 
                           @RequestParam(required = false) Double longitude,
                           @RequestParam(required = false) String aperture,
                           @RequestParam(required = false) String exposure,
                           @RequestParam(required = false) String whiteBalance) {
        photoService.addPhoto(id, latitude, longitude, aperture, exposure, whiteBalance);
        return "redirect:/rolls/" + id;
    }

    @GetMapping("/rolls/{id}/edit")
    public String showEditRollForm(@PathVariable Long id, Model model) {
        FilmRoll roll = filmRollService.getRollById(id);
        model.addAttribute("roll", roll);
        model.addAttribute("cameraId", roll.getCamera().getId());
        return "roll-form";
    }

    @PostMapping("/rolls/{id}")
    public String updateRoll(@PathVariable Long id, @ModelAttribute("roll") FilmRoll roll) {
        filmRollService.updateRoll(id, roll);
        return "redirect:/rolls/" + id;
    }

    @PostMapping("/rolls/{id}/delete")
    public String deleteRoll(@PathVariable Long id) {
        FilmRoll roll = filmRollService.getRollById(id);
        Long cameraId = roll.getCamera().getId();
        filmRollService.deleteRoll(id);
        return "redirect:/cameras/" + cameraId;
    }
}
