package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gcu.models.CharacterEntity;

public interface CharacterRepository extends JpaRepository<CharacterEntity, Integer>
{

}