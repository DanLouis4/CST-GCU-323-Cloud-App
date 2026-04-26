package com.gcu.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gcu.business.UserDatabaseService;
import com.gcu.models.UserEntity;

import jakarta.servlet.http.HttpSession;

/**
 * UserController is responsible for handling user authentication and registration processes in the application.
 * It provides routes for signing in, signing up, and signing out users. The controller interacts with the UserDatabaseService to validate user credentials during sign-in and to create new user accounts during sign-up.
 * The controller also manages user sessions, allowing users to stay logged in across different pages of the application.
 * When a user successfully signs in, their user information is stored in the session, and they are redirected to the character list page.
 * If sign-in fails, an error message is displayed on the sign-in page.
 * During sign-up, the controller checks if the desired username already exists in the database.
 * If it does, an error message is shown on the sign-up page.
 * If the username is available, a new user account is created with a default role of "USER", and the user is redirected to the sign-in page to log in with their new credentials.
 */
@Controller
public class UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserDatabaseService userDatabaseService;

    /**
     * Constructor for UserController that initializes the UserDatabaseService dependency.
     * @param userDatabaseService The UserDatabaseService instance that will be used to interact with the user database for authentication and registration operations.
     */
    public UserController(UserDatabaseService userDatabaseService)
    {
        this.userDatabaseService = userDatabaseService;
    }

    /**
     * Handles GET requests to the "/signin" URL and returns the name of the view to be rendered for the sign-in page.
     * @return The name of the view to be rendered, which is "signin" in this case.
     */
    @GetMapping("/signin")
    public String showSignInPage()
    {
        logger.info("Entering showSignInPage()");
        logger.info("Exiting showSignInPage() -> signin");
        return "signin";
    }

    /**
     * Handles GET requests to the "/signup" URL and returns the name of the view to be rendered for the sign-up page.
     * @return The name of the view to be rendered, which is "signup" in this case.
     */
    @GetMapping("/signup")
    public String showSignUpPage()
    {
        logger.info("Entering showSignUpPage()");
        logger.info("Exiting showSignUpPage() -> signup");
        return "signup";
    }

    /**
     * Handles POST requests to the "/signin" URL, processes the sign-in form data, and manages user authentication.
     * @param username The username entered by the user in the sign-in form.
     * @param password The password entered by the user in the sign-in form.
     * @param model The Model object used to pass data to the view in case of authentication failure.
     * @param session The HttpSession object used to manage the user's session if authentication is successful.
     * @return If authentication is successful, redirects to the character list page. If authentication fails, returns the sign-in view with an error message.
     */
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

    /**
     * Handles GET requests to the "/signout" URL, invalidates the user session, and redirects to the home page.
     * @param session The HttpSession object representing the user's current session, which will be invalidated to sign out the user.
     * @return Redirects to the home page ("/") after signing out the user.
     */
    @GetMapping("/signout")
    public String signOut(HttpSession session)
    {
        logger.info("Entering signOut()");
        session.invalidate();
        logger.info("Exiting signOut() -> redirect:/");
        return "redirect:/";
    }

    /**
     * Handles POST requests to the "/signup" URL, processes the sign-up form data, and manages user registration.
     * @param user The UserEntity object populated with the data from the sign-up form, which contains the username and password entered by the user. 
     * @param model The Model object used to pass data to the view in case of registration failure (e.g., if the username already exists).
     * @return If the username already exists, returns the sign-up view with an error message. If registration is successful, redirects to the sign-in page.
     */
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