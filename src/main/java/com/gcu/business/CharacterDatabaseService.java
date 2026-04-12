package com.gcu.business;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    private final RaceRepository raceRepository;
    private final ClassRepository classRepository;

    /*
        * Constructor for dependency injection
    */
    public CharacterDatabaseService(
            CharacterRepository characterRepository,
            UserRepository userRepository,
            RaceRepository raceRepository,
            ClassRepository classRepository)
    {
        this.characterRepository = characterRepository;
        this.raceRepository = raceRepository;
        this.classRepository = classRepository;
    }

    /*
        * Retrieves all characters from the database
    */
    public List<CharacterEntity> getAllCharacters()
    {
        return characterRepository.findAll();
    }

    /*
        * Retrieves characters visible to a specific user
    */
    public List<CharacterEntity> getVisibleCharacters(UserEntity user)
    {
        List<CharacterEntity> allCharacters = characterRepository.findAll();

        if (user == null)
        {
            return allCharacters.stream()
                    .filter(character -> character.getVisibility() == 0)
                    .toList();
        }

        return allCharacters.stream()
                .filter(character ->
                        character.getVisibility() == 0 ||
                        (character.getUser() != null &&
                        character.getUser().getUserId().equals(user.getUserId())))
                .toList();
    }


    /*
        * Searches for characters based on a keyword
        * The search checks multiple fields (name, gender, type, race, class)
        * The search is case-insensitive and supports partial matches for text fields
        * Additionally, it supports field-specific searches using the format "field:value" (e.g., "gender:male" or "race:elf")
    */
    public List<CharacterEntity> searchVisibleCharacters(UserEntity user, String keyword)
    {
        if (keyword == null || keyword.trim().isEmpty())
        {
            return getVisibleCharacters(user);
        }

        String searchText = keyword.trim().toLowerCase();

        List<CharacterEntity> resultCharacters;

        // Check for field-specific search (format: field:value)
        if (searchText.contains(":"))
        {
            String[] parts = searchText.split(":", 2);

            String field = parts[0].trim();
            String value = parts[1].trim();

            resultCharacters = characterRepository.findAll().stream()
                    .filter(character ->
                    {
                        return switch (field)
                        {
                            case "gender" ->
                                    character.getCharacterGender() != null &&
                                    character.getCharacterGender().equalsIgnoreCase(value);

                            case "type" ->
                                    character.getCharacterType() != null &&
                                    character.getCharacterType().equalsIgnoreCase(value);

                            case "race" ->
                                    character.getRace() != null &&
                                    character.getRace().getRaceName().equalsIgnoreCase(value);

                            case "class" ->
                                    character.getCharacterClass() != null &&
                                    character.getCharacterClass().getClassName().equalsIgnoreCase(value);

                            case "name" ->
                                    character.getCharacterName() != null &&
                                    character.getCharacterName().toLowerCase().contains(value);

                            default -> false;
                        };
                    })
                    .toList();
        }
        else
        {
            // fallback to normal broad search
            resultCharacters = characterRepository.searchCharacters(searchText);
        }

        // Apply visibility rules (unchanged)
        if (user == null)
        {
            return resultCharacters.stream()
                    .filter(character -> character.getVisibility() == 0)
                    .toList();
        }

        return resultCharacters.stream()
                .filter(character ->
                        character.getVisibility() == 0 ||
                        (character.getUser() != null &&
                        character.getUser().getUserId().equals(user.getUserId())))
                .toList();
    }
   
    /*
        * Sorts a list of characters based on a specified field and order
    */
    public List<CharacterEntity> searchAndSortVisibleCharacters(UserEntity user, String keyword, String sortBy, String ownerPriority)
    {
        List<CharacterEntity> characters = searchVisibleCharacters(user, keyword);

        List<CharacterEntity> sortedCharacters;

        if (sortBy == null || sortBy.trim().isEmpty())
        {
            sortedCharacters = characters;
        }
        else
        {
            sortedCharacters = switch (sortBy)
            {
                case "newest" -> characters.stream()
                        .sorted(Comparator.comparing(CharacterEntity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                        .toList();

                case "oldest" -> characters.stream()
                        .sorted(Comparator.comparing(CharacterEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();

                case "nameAsc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterName() != null ? character.getCharacterName() : "",
                                String.CASE_INSENSITIVE_ORDER))
                        .toList();

                case "nameDesc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterName() != null ? character.getCharacterName() : "",
                                String.CASE_INSENSITIVE_ORDER).reversed())
                        .toList();

                case "levelAsc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterLevel() != null ? character.getCharacterLevel() : 0))
                        .toList();

                case "levelDesc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterLevel() != null ? character.getCharacterLevel() : 0).reversed())
                        .toList();

                case "raceAsc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getRace() != null && character.getRace().getRaceName() != null
                                        ? character.getRace().getRaceName() : "",
                                String.CASE_INSENSITIVE_ORDER))
                        .toList();

                case "raceDesc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getRace() != null && character.getRace().getRaceName() != null
                                        ? character.getRace().getRaceName() : "",
                                String.CASE_INSENSITIVE_ORDER).reversed())
                        .toList();

                case "classAsc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterClass() != null && character.getCharacterClass().getClassName() != null
                                        ? character.getCharacterClass().getClassName() : "",
                                String.CASE_INSENSITIVE_ORDER))
                        .toList();

                case "classDesc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterClass() != null && character.getCharacterClass().getClassName() != null
                                        ? character.getCharacterClass().getClassName() : "",
                                String.CASE_INSENSITIVE_ORDER).reversed())
                        .toList();

                case "typeAsc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterType() != null ? character.getCharacterType() : "",
                                String.CASE_INSENSITIVE_ORDER))
                        .toList();

                case "typeDesc" -> characters.stream()
                        .sorted(Comparator.comparing(
                                (CharacterEntity character) -> character.getCharacterType() != null ? character.getCharacterType() : "",
                                String.CASE_INSENSITIVE_ORDER).reversed())
                        .toList();

                default -> characters;
            };
        }

        if (user == null || ownerPriority == null || ownerPriority.equalsIgnoreCase("ignore"))
        {
            return sortedCharacters;
        }

        List<CharacterEntity> ownedCharacters = sortedCharacters.stream()
                .filter(character -> character.getUser() != null &&
                        character.getUser().getUserId().equals(user.getUserId()))
                .toList();

        List<CharacterEntity> otherCharacters = sortedCharacters.stream()
                .filter(character -> character.getUser() == null ||
                        !character.getUser().getUserId().equals(user.getUserId()))
                .toList();

        List<CharacterEntity> result = new java.util.ArrayList<>();

        if (ownerPriority.equalsIgnoreCase("first"))
        {
            result.addAll(ownedCharacters);
            result.addAll(otherCharacters);
        }
        else if (ownerPriority.equalsIgnoreCase("last"))
        {
            result.addAll(otherCharacters);
            result.addAll(ownedCharacters);
        }
        else
        {
            return sortedCharacters;
        }

        return result;
    }

    /*
        * Finds a character by its ID
    */
    public CharacterEntity findById(int id)
    {
        Optional<CharacterEntity> entity = characterRepository.findById(id);
        return entity.orElse(null);
    }

    /*
        * Adds a new character to the database
    */
    public void addCharacter(CharacterEntity character, UserEntity user)
    {
        RaceEntity race = raceRepository.findById(character.getRace().getRaceId()).orElse(null);
        ClassEntity charClass = classRepository.findById(character.getCharacterClass().getClassId()).orElse(null);

        if (character.getVisibility() == null)
        {
            character.setVisibility(0); // Default to public if not set
        }   

        character.setUser(user);
        character.setRace(race);
        character.setCharacterClass(charClass);

        character.setCreatedAt(LocalDateTime.now());
        character.setUpdatedAt(LocalDateTime.now());

        characterRepository.save(character);
    }

    /*
        * Updates an existing character in the database
    */
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
            entity.setVisibility(character.getVisibility());

            RaceEntity race = raceRepository.findById(character.getRace().getRaceId()).orElse(null);
            ClassEntity charClass = classRepository.findById(character.getCharacterClass().getClassId()).orElse(null);

            entity.setRace(race);
            entity.setCharacterClass(charClass);

            entity.setUpdatedAt(LocalDateTime.now());

            characterRepository.save(entity);
        }
    }

    /*
        * Deletes a character from the database
    */

    public void deleteCharacter(int id)
    {
        characterRepository.deleteById(id);
    }
}