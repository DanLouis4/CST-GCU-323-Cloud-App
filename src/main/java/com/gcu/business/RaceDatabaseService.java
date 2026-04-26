package com.gcu.business;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gcu.data.RaceRepository;
import com.gcu.models.RaceEntity;

/**
 * Service class for handling business logic related to races.
 * It interacts with the RaceRepository to retrieve race data from the database and provides it to the controllers.
 * This class can be extended in the future to include additional business
 */
@Service
public class RaceDatabaseService {

    private final RaceRepository raceRepository;

    /**
     * Constructor for RaceDatabaseService.
     * @param raceRepository The repository used to access race data from the database.
     */
    public RaceDatabaseService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    /**
     * Retrieves all races from the database.
     * @return A list of RaceEntity objects representing all
     */
    public List<RaceEntity> getAllRaces() {
        return raceRepository.findAll();
    }
}