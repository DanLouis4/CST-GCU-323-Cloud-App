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

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserDatabaseService userDatabaseService;

    public UserController(UserDatabaseService userDatabaseService)
    {
        this.userDatabaseService = userDatabaseService;
    }

    @GetMapping("/signin")
    public String showSignInPage()
    {
        logger.info("Entering showSignInPage()");
        logger.info("Exiting showSignInPage() -> signin");
        return "signin";
    }

    @GetMapping("/signup")
    public String showSignUpPage()
    {
        logger.info("Entering showSignUpPage()");
        logger.info("Exiting showSignUpPage() -> signup");
        return "signup";
    }

    @PostMapping("/signin")
    public String processSignIn(@RequestParam String username,
                                @RequestParam String password,
                                Model model,
                                HttpSession session)
    {
        logger.info("Entering processSignIn() for username={}", username);

        UserEntity user = userDatabaseService.validateUser(username, password);

        if (user == null)
        {
            logger.warn("Failed sign-in attempt for username={}", username);
            model.addAttribute("error", "Invalid username or password");
            logger.info("Exiting processSignIn() -> signin");
            return "signin";
        }

        session.setAttribute("user", user);
        logger.info("Successful sign-in for username={}", username);
        logger.info("Exiting processSignIn() -> redirect:/characters");

        return "redirect:/characters";
    }

    @GetMapping("/signout")
    public String signOut(HttpSession session)
    {
        logger.info("Entering signOut()");
        session.invalidate();
        logger.info("Exiting signOut() -> redirect:/");
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String processSignUp(@ModelAttribute UserEntity user, Model model)
    {
        logger.info("Entering processSignUp() for username={}", user.getUsername());

        if (userDatabaseService.usernameExists(user.getUsername()))
        {
            logger.warn("Signup rejected: username already exists -> {}", user.getUsername());
            model.addAttribute("error", "Username already exists");
            logger.info("Exiting processSignUp() -> signup");
            return "signup";
        }

        user.setRole("USER");
        userDatabaseService.addUser(user);

        logger.info("User created successfully for username={}", user.getUsername());
        logger.info("Exiting processSignUp() -> redirect:/signin");

        return "redirect:/signin";
    }
}