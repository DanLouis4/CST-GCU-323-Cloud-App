package com.gcu.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gcu.data.CharacterRepository;
import com.gcu.data.ClassRepository;
import com.gcu.data.RaceRepository;
import com.gcu.data.UserRepository;
import com.gcu.models.CharacterEntity;
import com.gcu.models.ClassEntity;
import com.gcu.models.RaceEntity;
import com.gcu.models.UserEntity;

@Service
public class CharacterDatabaseService
{
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final ClassRepository classRepository;

    public CharacterDatabaseService(
            CharacterRepository characterRepository,
            UserRepository userRepository,
            RaceRepository raceRepository,
            ClassRepository classRepository)
    {
        this.characterRepository = characterRepository;
        this.userRepository = userRepository;
        this.raceRepository = raceRepository;
        this.classRepository = classRepository;
    }

    public List<CharacterEntity> getAllCharacters()
    {
        return characterRepository.findAll();
    }

    public CharacterEntity findById(int id)
    {
        Optional<CharacterEntity> entity = characterRepository.findById(id);
        return entity.orElse(null);
    }

    public void addCharacter(CharacterEntity character)
    {
        UserEntity user = userRepository.findById(1).orElse(null);
        RaceEntity race = raceRepository.findById(character.getRace().getRaceId()).orElse(null);
        ClassEntity charClass = classRepository.findById(character.getCharacterClass().getClassId()).orElse(null);

        character.setUser(user);
        character.setRace(race);
        character.setCharacterClass(charClass);

        character.setCreatedAt(LocalDateTime.now());
        character.setUpdatedAt(LocalDateTime.now());

        characterRepository.save(character);
    }

    public void updateCharacter(CharacterEntity character)
    {
        Optional<CharacterEntity> optionalEntity = characterRepository.findById(character.getCharacterId());

        if (optionalEntity.isPresent())
        {
            CharacterEntity entity = optionalEntity.get();

            entity.setCharacterName(character.getCharacterName());
            entity.setCharacterLevel(character.getCharacterLevel());
            entity.setCharacterGender(character.getCharacterGender());
            entity.setCharacterType(character.getCharacterType());
            entity.setCharacterDescription(character.getCharacterDescription());
            entity.setImageUrl(character.getImageUrl());

            RaceEntity race = raceRepository.findById(character.getRace().getRaceId()).orElse(null);
            ClassEntity charClass = classRepository.findById(character.getCharacterClass().getClassId()).orElse(null);

            entity.setRace(race);
            entity.setCharacterClass(charClass);

            entity.setUpdatedAt(LocalDateTime.now());

            characterRepository.save(entity);
        }
    }

    public void deleteCharacter(int id)
    {
        characterRepository.deleteById(id);
    }
}