package com.gcu.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.data.UserRepository;
import com.gcu.models.UserEntity;

/**
 * Service class for handling user-related database operations. This class interacts with the UserRepository to perform CRUD operations on user data.
 * It includes methods for adding a new user, finding a user by username, checking if a username already exists, and validating user login credentials.
 * Each method includes logging for better traceability and debugging.
 */
@Service
public class UserDatabaseService
{
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseService.class);

    private final UserRepository userRepository;

    /**
     * Constructor for UserDatabaseService. It takes a UserRepository as a parameter and initializes the userRepository field.
     * @param userRepository The UserRepository instance to be used for database operations related to users. This is typically injected by Spring's dependency injection mechanism.
     */
    public UserDatabaseService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * Adds a new user to the database. It takes a UserEntity object as a parameter and saves it using the userRepository.
     * @param user The UserEntity object representing the user to be added to the database. The object should contain the username and password of the user.
     * @return The saved UserEntity object with the generated ID.
     */
    public UserEntity addUser(UserEntity user)
    {
        logger.info("Entering addUser() for username={}", user.getUsername());

        try
        {
            UserEntity savedUser = userRepository.save(user);
            logger.info("User created successfully for username={}", savedUser.getUsername());
            logger.info("Exiting addUser()");
            return savedUser;
        }
        catch (Exception e)
        {
            logger.error("Error in addUser() for username={}: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Finds a user by their username. It takes a username as a parameter and returns the corresponding UserEntity object if found, or null if not found.
     * @param username The username of the user to find.
     * @return The UserEntity object corresponding to the given username, or null if no user is found.
     */
    public UserEntity findByUsername(String username)
    {
        logger.info("Entering findByUsername() for username={}", username);

        try
        {
            UserEntity user = userRepository.findByUsername(username);

            if (user == null)
            {
                logger.warn("No user found for username={}", username);
            }

            logger.info("Exiting findByUsername()");
            return user;
        }
        catch (Exception e)
        {
            logger.error("Error in findByUsername() for username={}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Checks if a username already exists in the database. It takes a username as a parameter and returns true if the username exists, or false if it does not.
     * @param username The username to check for existence in the database.
     * @return true if the username exists in the database, false otherwise.
     */
    public boolean usernameExists(String username)
    {
        logger.info("Entering usernameExists() for username={}", username);

        try
        {
            boolean exists = userRepository.findByUsername(username) != null;
            logger.info("usernameExists() returned {} for username={}", exists, username);
            logger.info("Exiting usernameExists()");
            return exists;
        }
        catch (Exception e)
        {
            logger.error("Error in usernameExists() for username={}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validates a user's login credentials. It takes a username and password as parameters and returns the corresponding UserEntity object if the credentials are valid, or null if they are invalid.
     * @param username The username of the user to validate.
     * @param password The password of the user to validate.
     * @return The UserEntity object corresponding to the given username if the credentials are valid, or null if the credentials are invalid.
     */
    public UserEntity validateUser(String username, String password)
    {
        logger.info("Entering validateUser() for username={}", username);

        try
        {
            UserEntity user = userRepository.findByUsername(username);

            if (user == null)
            {
                logger.warn("No user found for username={}", username);
                logger.info("Exiting validateUser() => null");
                return null;
            }

            if (user.getPassword().equals(password))
            {
                logger.info("User validated successfully for username={}", username);
                logger.info("Exiting validateUser() => success for user={}", user.getUsername());
                return user;
            }
            else
            {
                logger.warn("Invalid password for username={}", username);
                logger.info("Exiting validateUser() => null");
                return null;
            }
        }
        catch (Exception e)
        {
            logger.error("Error in validateUser() for username={}: {}", username, e.getMessage(), e);
            throw e;
        }
    }
}