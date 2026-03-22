package com.filmpin.controller;

import com.filmpin.entity.User;
import com.filmpin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.usernameExists(user.getUsername())) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        userService.registerUser(user);
        return "redirect:/login?success";
    }
}
