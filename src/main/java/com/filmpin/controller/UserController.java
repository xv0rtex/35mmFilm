package com.filmpin.controller;

import com.filmpin.entity.User;
import com.filmpin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import com.filmpin.service.CustomUserDetailsService;

@Controller
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("username") String newUsername,
                                @RequestParam("email") String newEmail,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User user = userService.getUserByUsername(currentUsername);

        try {
            if (!newUsername.equals(currentUsername) && userService.usernameExists(newUsername)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/profile";
            }
            if (!newEmail.equals(user.getEmail()) && userService.emailExists(newEmail)) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/profile";
            }

            user.setUsername(newUsername);
            user.setEmail(newEmail);
            userService.updateUser(user);
            
            // Actualizar el contexto de seguridad con el nuevo nombre de usuario
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(newUsername);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating profile");
        }
        
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String updatePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Passwords do not match");
            return "redirect:/profile";
        }

        userService.updatePassword(user, newPassword);
        redirectAttributes.addFlashAttribute("passwordSuccess", "Password updated successfully");
        
        return "redirect:/profile";
    }
}
