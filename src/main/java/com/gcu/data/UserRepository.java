package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gcu.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>
{
    public UserEntity findByUsername(String username);
}