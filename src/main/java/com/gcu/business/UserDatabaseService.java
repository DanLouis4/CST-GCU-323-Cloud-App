package com.gcu.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.UserRepository;
import com.gcu.models.UserEntity;

@Service
public class UserDatabaseService
{
    @Autowired
    private UserRepository userRepository;

    // Create user
    public UserEntity addUser(UserEntity user)
    {
        return userRepository.save(user);
    }

    // Find user by username
    public UserEntity findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    // Check if username already exists
    public boolean usernameExists(String username)
    {
        return userRepository.findByUsername(username) != null;
    }

    // Validate login credentials
    public UserEntity validateUser(String username, String password)
    {
        UserEntity user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password))
        {
            return user;
        }

        return null;
    }
}