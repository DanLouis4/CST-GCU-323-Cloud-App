package com.gcu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController is responsible for handling requests to the home page of the application.
 * It maps the root URL ("/") to the home view, which is typically the landing page for users when they visit the application.
 * This controller does not require any additional logic or data processing, as it simply returns the name of the view to be rendered.
 * The home view can be a simple HTML page that welcomes users and provides navigation to other parts of the application, such as login, registration and character lists.
 * Additional routes will include About, Contact, and FAQ pages, which will provide users with more information about the application and how to use it.
 */
@Controller
public class HomeController
{

    /**
     * Default constructor for HomeController. Currently, there is no initialization logic needed, but this constructor can be used in the future if any dependencies or services need to be injected into the controller.
     */
    public HomeController()
    {
    }

    /**
     * Handles GET requests to the root URL ("/") and returns the name of the view to be rendered.
     * @return The name of the view to be rendered, which is "home" in this case.
     */
    @GetMapping("/")
    public String home()
    {
        return "home";
    }
}