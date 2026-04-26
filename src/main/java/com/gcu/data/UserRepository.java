package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.models.UserEntity;

/**
 * UserRepository is an interface that extends JpaRepository to provide CRUD operations for UserEntity objects.
 * It includes a custom method to find a user by their username.
 */
public interface UserRepository extends JpaRepository<UserEntity, Integer>
{
    /**
     * Finds a UserEntity by its username.
     * @param username the username of the user to find.
     * @return UserEntity object that matches the given username, or null if no match is found.
     */
    public UserEntity findByUsername(String username);
}