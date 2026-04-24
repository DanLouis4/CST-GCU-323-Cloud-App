package com.gcu.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "characters")
public class CharacterEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Integer characterId;

    @Column(name = "character_name", nullable = false)
    private String characterName;

    @Column(name = "character_level", nullable = false)
    private Integer characterLevel;

    @Column(name = "character_gender", nullable = false)
    private String characterGender;

    @Column(name = "character_type", nullable = false)
    private String characterType;

    @Column(name = "character_description", columnDefinition = "TEXT")
    private String characterDescription;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "visibility", nullable = false)
    private Integer visibility;

    @Column(name = "flagged", nullable = false)
    private Boolean flagged = false;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "race_id", nullable = false)
    private RaceEntity race;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity characterClass; 

    // Default constructor
    public CharacterEntity()
    {
    }

    /* Getter and Setter methods for the non-relational fields */
    public Integer getCharacterId()
    {
        return characterId;
    }

    public void setCharacterId(Integer characterId)
    {
        this.characterId = characterId;
    }

    public Integer getVisibility()
    {
        return visibility;
    }

    public void setVisibility(Integer visibility)
    {
        this.visibility = visibility;
    }

    public Boolean getFlagged()
    {
        return flagged;
    }

    public void setFlagged(Boolean flagged)
    {
        this.flagged = flagged;
    }

    public String getCharacterName()
    {
        return characterName;
    }

    public void setCharacterName(String characterName)
    {
        this.characterName = characterName;
    }

    public Integer getCharacterLevel()
    {
        return characterLevel;
    }

    public void setCharacterLevel(Integer characterLevel)
    {
        this.characterLevel = characterLevel;
    }

    public String getCharacterGender()
    {
        return characterGender;
    }

    public void setCharacterGender(String characterGender)
    {
        this.characterGender = characterGender;
    }

    public String getCharacterDescription()
    {
        return characterDescription;
    }

    public void setCharacterDescription(String characterDescription)
    {
        this.characterDescription = characterDescription;
    }

    public String getCharacterType()
    {
        return characterType;
    }

    public void setCharacterType(String characterType)
    {
        this.characterType = characterType;
    }

    /* Getter and Setter methods for the relational fields */
    public UserEntity getUser()
    {
        return user;
    }

    public void setUser(UserEntity user)
    {
        this.user = user;
    }
    
    public RaceEntity getRace()
    {
        return race;
    }

    public void setRace(RaceEntity race)
    {
        this.race = race;
    }
    
    public ClassEntity getCharacterClass()
    {
        return characterClass;
    }
    
    public void setCharacterClass(ClassEntity characterClass)
    {
        this.characterClass = characterClass;
    }

    /* Getter and Setter methods for the image URL field */
    public String getImageUrl()
    {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
    
    /* Getter and Setter methods for the created and updated timestamps */
    public LocalDateTime getCreatedAt()
    {
        return createdOn;
    }
    
    public void setCreatedAt(LocalDateTime createdOn)
    {
        this.createdOn = createdOn;
    }
    
    public LocalDateTime getUpdatedAt()
    {
        return updatedOn;
    }
    
    public void setUpdatedAt(LocalDateTime updatedOn)
    {
        this.updatedOn = updatedOn;
    }

}