package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gcu.models.RaceEntity;

public interface RaceRepository extends JpaRepository<RaceEntity, Integer>
{

    RaceEntity findByRaceName(String raceName);
    
}