package com.gcu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import com.gcu.models.UserEntity;

import jakarta.servlet.http.HttpSession;

import com.gcu.business.UserDatabaseService;

@Controller
public class UserController
{
    private final UserDatabaseService userDatabaseService;

    public UserController(UserDatabaseService userDatabaseService)
    {
        this.userDatabaseService = userDatabaseService;
    }

    @GetMapping("/signin")
    public String showSignInPage()
    {
        return "signin";
    }

    @GetMapping("/signup")
    public String showSignUpPage()
    {
        return "signup";
    }

    @PostMapping("/signin")
    public String processSignIn(@RequestParam String username,
                                @RequestParam String password,
                                Model model,
                                HttpSession session)
    {
        UserEntity user = userDatabaseService.validateUser(username, password);

        if (user == null)
        {
            model.addAttribute("error", "Invalid username or password");
            return "signin";
        }

        session.setAttribute("user", user);

        return "redirect:/characters";
    } 
    
    @GetMapping("/signout")
    public String signOut(HttpSession session)
    {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String processSignUp(@ModelAttribute UserEntity user, Model model)
{
    // Check if username already exists
    if (userDatabaseService.usernameExists(user.getUsername()))
    {
        model.addAttribute("error", "Username already exists");
        return "signup";
    }

    // Set default role
    user.setRole("USER");

    // Save user
    userDatabaseService.addUser(user);

    // Redirect to sign in page
    return "redirect:/signin";
}
}