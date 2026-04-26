package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.models.RaceEntity;

/**
 * RaceRepository is an interface that extends JpaRepository to provide CRUD operations for RaceEntity objects.
 * It includes a method to find a RaceEntity by its raceName.
 */
public interface RaceRepository extends JpaRepository<RaceEntity, Integer>
{

    /**
     * Finds a RaceEntity by its raceName.
     * @param raceName the name of the race to search for.
     * @return the RaceEntity with the specified raceName, or null if not found.
     */
    RaceEntity findByRaceName(String raceName);
    
}