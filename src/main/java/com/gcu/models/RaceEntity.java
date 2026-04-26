package com.gcu.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * RaceEntity represents a character race in the application. It contains fields for race attributes such as name and description,
 * as well as metadata fields for timestamps of when the race was added and last updated.
 */
@Entity
@Table(name = "races")
public class RaceEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "race_id")
    private Integer raceId;

    @Column(name = "race_name", nullable = false, unique = true)
    private String raceName;

    @Column(name = "race_description")
    private String raceDescription;

    @Column(name = "added_on")
    private LocalDateTime addedOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    /**
     * Default constructor for RaceEntity. Required by JPA for entity instantiation.
     */
    public RaceEntity()
    {
    }

    /**
     * Gets the unique identifier for the race
     * @return the raceId
     */
    public Integer getRaceId()
    {
        return raceId;
    }

    /**
     * Sets the unique identifier for the race
     * @param raceId the raceId to set
     */
    public void setRaceId(Integer raceId)
    {
        this.raceId = raceId;
    }

    /**
     * Gets the name of the race
     * @return the raceName
     */
    public String getRaceName()
    {
        return raceName;
    }

    /**
     * Sets the name of the race
     * @param raceName the raceName to set
     */
    public void setRaceName(String raceName)
    {
        this.raceName = raceName;
    }

    /**
     * Gets the description of the race
     * @return the raceDescription
     */
    public String getRaceDescription()
    {
        return raceDescription;
    }

    /**
     * Sets the description of the race
     * @param raceDescription   the raceDescription to set
     */
    public void setRaceDescription(String raceDescription)
    {
        this.raceDescription = raceDescription;
    }

    /**
     * Gets the timestamp of when the race was added to the database
     * @return the addedOn timestamp
     */
    public LocalDateTime getAddedOn()
    {
        return addedOn;
    }

    /**
     * Sets the timestamp of when the race was added to the database
     * @param addedOn the addedOn timestamp to set
     */
    public void setAddedOn(LocalDateTime addedOn)
    {
        this.addedOn = addedOn;
    }

    /**
     * Gets the timestamp of when the race was last updated in the database
     * @return the updatedOn timestamp
     */
    public LocalDateTime getUpdatedOn()
    {
        return updatedOn;
    }

    /**
     * Sets the timestamp of when the race was last updated in the database
     * @param updatedOn the updatedOn timestamp to set
     */
    public void setUpdatedOn(LocalDateTime updatedOn)
    {
        this.updatedOn = updatedOn;
    }
}