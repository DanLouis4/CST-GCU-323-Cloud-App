package com.gcu.business;

import org.springframework.stereotype.Service;

import com.gcu.data.UserRepository;
import com.gcu.models.UserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserDatabaseService
{
    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseService.class);


    private final UserRepository userRepository;

    public UserDatabaseService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    // Create user
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

    // Find user by username
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

    // Check if username already exists
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

    // Validate login credentials
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