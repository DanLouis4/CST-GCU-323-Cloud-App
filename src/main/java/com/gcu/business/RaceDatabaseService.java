package com.gcu.business;

import org.springframework.stereotype.Service;
import com.gcu.data.RaceRepository;
import com.gcu.models.RaceEntity;
import java.util.List;

@Service
public class RaceDatabaseService {

    private final RaceRepository raceRepository;

    public RaceDatabaseService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public List<RaceEntity> getAllRaces() {
        return raceRepository.findAll();
    }
}