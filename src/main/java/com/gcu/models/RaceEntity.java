package com.gcu.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public RaceEntity()
    {
    }

    public Integer getRaceId()
    {
        return raceId;
    }

    public void setRaceId(Integer raceId)
    {
        this.raceId = raceId;
    }

    public String getRaceName()
    {
        return raceName;
    }

    public void setRaceName(String raceName)
    {
        this.raceName = raceName;
    }

    public String getRaceDescription()
    {
        return raceDescription;
    }

    public void setRaceDescription(String raceDescription)
    {
        this.raceDescription = raceDescription;
    }

    public LocalDateTime getAddedOn()
    {
        return addedOn;
    }

    public void setAddedOn(LocalDateTime addedOn)
    {
        this.addedOn = addedOn;
    }

    public LocalDateTime getUpdatedOn()
    {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn)
    {
        this.updatedOn = updatedOn;
    }
}