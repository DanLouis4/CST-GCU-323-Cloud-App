package com.gcu.business;

// Services
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.data.CharacterRepository;
import com.gcu.data.ClassRepository;
import com.gcu.data.RaceRepository;
import com.gcu.models.CharacterEntity;
import com.gcu.models.ClassEntity;
import com.gcu.models.RaceEntity;
import com.gcu.models.UserEntity;

/**
 * CharacterDatabaseService is a Spring service class that provides business logic for managing character entities in the application.
 * It interacts with the CharacterRepository, RaceRepository, and ClassRepository to perform CRUD operations, search, and filtering of characters based on user roles and visibility rules.
 * The service includes methods for retrieving all characters, retrieving visible characters for a
 * specific user, searching characters with keyword and sorting options, adding new characters,
 * updating existing characters, deleting characters, and flagging/unflagging characters for review.
 */
@Service
public class CharacterDatabaseService
{

    private static final Logger logger = LoggerFactory.getLogger(CharacterDatabaseService.class);

    private final CharacterRepository characterRepository;
    private final RaceRepository raceRepository;
    private final ClassRepository classRepository;

    /**
     * Constructor for CharacterDatabaseService, which initializes the repositories used for character management.
     * @param characterRepository the repository for performing CRUD operations on CharacterEntity objects
     * @param raceRepository the repository for retrieving RaceEntity objects when adding or updating characters
     * @param classRepository the repository for retrieving ClassEntity objects when adding or updating characters
     */
    public CharacterDatabaseService(CharacterRepository characterRepository, RaceRepository raceRepository, ClassRepository classRepository) {
        this.characterRepository = characterRepository;
        this.raceRepository = raceRepository;
        this.classRepository = classRepository;
    }

    /**
     * Retrieves all characters from the database without applying any visibility or flagging filters. This method is intended for internal use and administrative purposes.
     * @return list of all CharacterEntity objects in the database
     */
    public List<CharacterEntity> getAllCharacters() {
        logger.info("Entering getAllCharacters()");

        try {
            List<CharacterEntity> characters = characterRepository.findAll();

            logger.info("Exiting getAllCharacters() => {} characters found", characters.size());
            return characters;
        } catch (Exception e) {
            logger.error("Error in getAllCharacters(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves characters from the database based on the visibility and flagging rules for the given user. Admin users see all characters,
     * registered users see their own characters plus public unflagged ones, and guests see only public unflagged characters.
     * @param user the currently authenticated (null if guest)
     * @return list of CharacterEntity objects visible to the given user based on their role and the characters' visibility and flagging status
     */
    public List<CharacterEntity> getVisibleCharacters(UserEntity user) {
        logger.info("Entering getVisibleCharacters() for user={}", user != null ? user.getUsername() : "null");

        try {
            List<CharacterEntity> allCharacters = characterRepository.findAll();

            List<CharacterEntity> visibleCharacters;
            if (isAdmin(user)) {
                logger.info("Admin user detected. Returning all characters.");

                visibleCharacters = allCharacters;

            } else if (user == null) {
                logger.info("Filtering public, unflagged characters only for guest user");

                visibleCharacters = allCharacters.stream()
                        .filter(character -> character.getVisibility() == 0 
                                && !Boolean.TRUE.equals(character.getFlagged()))
                        .toList();
            } else {
                logger.info(
                        "Filtering characters for registered user: {} (Id: userId={})",
                        user.getUsername(),
                        user.getUserId());

                visibleCharacters = allCharacters.stream()
                        .filter(character -> (character.getVisibility() == 0 && !Boolean.TRUE.equals(character.getFlagged()))   
                                || (character.getUser() != null
                                        && character.getUser().getUserId().equals(user.getUserId())))
                        .toList();
            }

            logger.info("Exiting getVisibleCharacters() => {} characters found", visibleCharacters.size());
            return visibleCharacters;
        } catch (Exception e) {
            logger.error("Error in getVisibleCharacters(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Searches for characters based on a keyword and user visibility.
     * 
     * Supports:
     * - General keyword search across multiple fields (name, race, class, description, type, username)
     * - Field-specific search using "field:value" syntax (e.g., "user:john", "gender:male")
     * 
     * Field-specific searches use exact matching for controlled fields (e.g., gender, user),
     * while general searches use partial matching.
     * 
     * Results are filtered based on user role:
     * - Admin: sees all characters
     * - Registered user: sees own characters + public unflagged characters
     * - Guest: sees only public unflagged characters
     *
     * @param user the currently authenticated user (null if guest)
     * @param keyword the search term or field-specific query
     * @return list of visible CharacterEntity objects matching the search criteria
     */
    public List<CharacterEntity> searchVisibleCharacters(UserEntity user, String keyword) {

        logger.info(
                "Entering searchVisibleCharacters() for user={} with keyword={}",
                user != null ? user.getUsername() : "guest",
                keyword);

        /* Searches for characters based on a keyword and user visibility */
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                logger.info("No keyword provided, delegating to getVisibleCharacters()");

                List<CharacterEntity> result = getVisibleCharacters(user);

                logger.info("Exiting searchVisibleCharacters() with {} records", result.size());
                return result;
            }

            String searchText = keyword.trim().toLowerCase();

            List<CharacterEntity> resultCharacters;

            // Check for field-specific search syntax (e.g., "user:John", "gender:Male")
            if (searchText.contains(":")) {
                String[] parts = searchText.split(":", 2);
                String field = parts[0].trim();
                String value = parts[1].trim();

                logger.info("Field-specific search detected: {}={}", field, value);

                // Perform field-specific filtering
                resultCharacters = characterRepository.findAll().stream()
                        .filter(character -> {
                            return switch (field) {
                                case "user" ->
                                    character.getUser() != null
                                            && character.getUser().getUsername() != null
                                            && character.getUser().getUsername().equalsIgnoreCase(value);
                                
                                case "gender" ->
                                    character.getCharacterGender() != null
                                            && character.getCharacterGender().equalsIgnoreCase(value);

                                case "type" ->
                                    character.getCharacterType() != null
                                            && character.getCharacterType().equalsIgnoreCase(value);

                                case "race" ->
                                    character.getRace() != null
                                            && character.getRace().getRaceName().equalsIgnoreCase(value);

                                case "class" ->
                                    character.getCharacterClass() != null
                                            && character
                                                    .getCharacterClass()
                                                    .getClassName()
                                                    .equalsIgnoreCase(value);

                                case "name" ->
                                    character.getCharacterName() != null
                                            && character
                                                    .getCharacterName()
                                                    .toLowerCase()
                                                    .contains(value);

                                default -> false;
                            };
                        })
                        .toList();

                logger.info("Field search returned {} records", resultCharacters.size());
            } else {
                logger.info("Performing general search with keyword={}", searchText);

                resultCharacters = characterRepository.searchCharacters(searchText);

                logger.info("General search returned {} records", resultCharacters.size());
            }

            List<CharacterEntity> visibleResults;

            if (isAdmin(user)) {
                visibleResults = resultCharacters;
            }
            else if (user == null) {
                visibleResults = resultCharacters.stream()
                        .filter(character -> character.getVisibility() == 0 
                                && !Boolean.TRUE.equals(character.getFlagged()))
                        .toList();
            }
            else {
                visibleResults = resultCharacters.stream()
                        .filter(character ->
                                (character.getVisibility() == 0 
                                        && !Boolean.TRUE.equals(character.getFlagged()))
                                || (character.getUser() != null
                                        && character.getUser().getUserId().equals(user.getUserId())))
                        .toList();
            }

            logger.info("Exiting searchVisibleCharacters() with {} visible records", visibleResults.size());
            return visibleResults;
        } catch (Exception e) {
            logger.error("Error in searchVisibleCharacters(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Searches for characters based on a keyword and user visibility, then sorts the results based on the specified criteria (e.g., newest, oldest, name ascending/descending, level ascending
     * @param user the currently authenticated user (null if guest)
     * @param keyword the search term entered by the user
     * @param sortBy the sorting criteria (e.g., nameAsc, levelDesc)
     * @param ownerPriority determines whether user-owned characters appear first or last
     * @return list of CharacterEntity objects that match the search criteria, are visible to the given user based on their role and the characters' visibility and flagging status,and are sorted according to the specified criteria and owner priority
     */
    public List<CharacterEntity> searchAndSortVisibleCharacters(
            UserEntity user, String keyword, String sortBy, String ownerPriority) {
        logger.info(
                "Entering searchAndSortVisibleCharacters() for user={} keyword={} sortBy={} ownerPriority={}",
                user != null ? user.getUsername() : "guest",
                keyword,
                sortBy,
                ownerPriority);

        try {
            List<CharacterEntity> characters = searchVisibleCharacters(user, keyword);
            logger.info("Search returned {} records", characters.size());

            List<CharacterEntity> sortedCharacters;

            if (sortBy == null || sortBy.trim().isEmpty()) {

                logger.info("No sorting applied");
                sortedCharacters = characters;

            } else {

                logger.info("Applying sort: {}", sortBy);

                switch (sortBy) {
                    case "newest":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                        CharacterEntity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                                .toList();
                        break;

                    case "oldest":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                        CharacterEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                                .toList();
                        break;

                    case "nameAsc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                        (CharacterEntity c) -> c.getCharacterName() != null ? c.getCharacterName() : "",
                                        String.CASE_INSENSITIVE_ORDER))
                                .toList();
                        break;

                    case "nameDesc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                                (CharacterEntity c) ->
                                                        c.getCharacterName() != null ? c.getCharacterName() : "",
                                                String.CASE_INSENSITIVE_ORDER)
                                        .reversed())
                                .toList();
                        break;

                    case "levelAsc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing((CharacterEntity c) ->
                                        c.getCharacterLevel() != null ? c.getCharacterLevel() : 0))
                                .toList();
                        break;

                    case "levelDesc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing((CharacterEntity c) ->
                                                c.getCharacterLevel() != null ? c.getCharacterLevel() : 0)
                                        .reversed())
                                .toList();
                        break;

                    case "raceAsc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                        (CharacterEntity c) -> c.getRace() != null
                                                        && c.getRace().getRaceName() != null
                                                ? c.getRace().getRaceName()
                                                : "",
                                        String.CASE_INSENSITIVE_ORDER))
                                .toList();
                        break;

                    case "raceDesc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                                (CharacterEntity c) -> c.getRace() != null
                                                                && c.getRace().getRaceName() != null
                                                        ? c.getRace().getRaceName()
                                                        : "",
                                                String.CASE_INSENSITIVE_ORDER)
                                        .reversed())
                                .toList();
                        break;

                    case "classAsc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                        (CharacterEntity c) -> c.getCharacterClass() != null
                                                        && c.getCharacterClass().getClassName() != null
                                                ? c.getCharacterClass().getClassName()
                                                : "",
                                        String.CASE_INSENSITIVE_ORDER))
                                .toList();
                        break;

                    case "classDesc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                                (CharacterEntity c) -> c.getCharacterClass() != null
                                                                && c.getCharacterClass()
                                                                                .getClassName()
                                                                        != null
                                                        ? c.getCharacterClass().getClassName()
                                                        : "",
                                                String.CASE_INSENSITIVE_ORDER)
                                        .reversed())
                                .toList();
                        break;

                    case "typeAsc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                        (CharacterEntity c) -> c.getCharacterType() != null ? c.getCharacterType() : "",
                                        String.CASE_INSENSITIVE_ORDER))
                                .toList();
                        break;

                    case "typeDesc":
                        sortedCharacters = characters.stream()
                                .sorted(Comparator.comparing(
                                                (CharacterEntity c) ->
                                                        c.getCharacterType() != null ? c.getCharacterType() : "",
                                                String.CASE_INSENSITIVE_ORDER)
                                        .reversed())
                                .toList();
                        break;

                    default:
                        sortedCharacters = characters;
                        break;
                }
            }

            if (user == null || ownerPriority == null || ownerPriority.equalsIgnoreCase("ignore")) {
                logger.info("No owner priority applied");
                logger.info("Exiting searchAndSortVisibleCharacters() with {} records", sortedCharacters.size());
                return sortedCharacters;
            }

            logger.info("Applying owner priority: {}", ownerPriority);

            List<CharacterEntity> ownedCharacters = sortedCharacters.stream()
                    .filter(c -> c.getUser() != null && c.getUser().getUserId().equals(user.getUserId()))
                    .toList();

            List<CharacterEntity> otherCharacters = sortedCharacters.stream()
                    .filter(c -> c.getUser() == null || !c.getUser().getUserId().equals(user.getUserId()))
                    .toList();

            List<CharacterEntity> result = new java.util.ArrayList<>();

            if (ownerPriority.equalsIgnoreCase("first")) {

                result.addAll(ownedCharacters);
                result.addAll(otherCharacters);

            } else if (ownerPriority.equalsIgnoreCase("last")) {

                result.addAll(otherCharacters);
                result.addAll(ownedCharacters);

            } else {

                logger.info("Owner priority not recognized, returning sorted results");
                logger.info("Exiting searchAndSortVisibleCharacters() with {} records", sortedCharacters.size());
                return sortedCharacters;
            }

            logger.info("Exiting searchAndSortVisibleCharacters() with {} records", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error in searchAndSortVisibleCharacters(): {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Finds a character by its ID without applying any visibility or flagging filters. This method is intended for internal use and administrative purposes.
     * @param id the ID of the character to find
     * @return CharacterEntity object with the specified ID, or null if not found
     */
    public CharacterEntity findById(int id) {
        logger.info("Entering findById() with id={}", id);

        try {
            Optional<CharacterEntity> entity = characterRepository.findById(id);

            if (entity.isPresent()) {
                logger.info("Character found for id={}", id);
                logger.info("Exiting findById()");
                return entity.get();
            } else {
                logger.warn("No character found for id={}", id);
                logger.info("Exiting findById() -> null");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error in findById() for id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Adds a new character to the database, associating it with the specified user and
     * @param character the CharacterEntity object to be added to the database, which should contain all necessary
     * @param user the currently authenticated user (null if guest)
     */
    public void addCharacter(CharacterEntity character, UserEntity user) {
        logger.info(
                "Entering addCharacter() for name={} user={}",
                character.getCharacterName(),
                user != null ? user.getUsername() : "null");

        try {
            RaceEntity race =
                    raceRepository.findById(character.getRace().getRaceId()).orElse(null);
            ClassEntity charClass = classRepository
                    .findById(character.getCharacterClass().getClassId())
                    .orElse(null);

            if (character.getVisibility() == null) {
                character.setVisibility(0);
                logger.info("Visibility not set, defaulting to public (0)");
            }

            character.setUser(user);
            character.setRace(race);
            character.setCharacterClass(charClass);

            character.setCreatedAt(LocalDateTime.now());
            character.setUpdatedAt(LocalDateTime.now());

            characterRepository.save(character);

            logger.info(
                    "Character created successfully: name={} for user={}",
                    character.getCharacterName(),
                    user != null ? user.getUsername() : "null");

            logger.info("Exiting addCharacter()");
        } catch (Exception e) {
            logger.error("Error in addCharacter() for name={}: {}", character.getCharacterName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates an existing character in the database, allowing changes to all fields including visibility and flagging status.
     * The method first retrieves the existing character by ID, then updates its fields with the new values provided in the input character object,
     * and finally saves the updated character back to the database.
     * @param character the CharacterEntity object containing the updated character information, which must include the characterId of the character to be updated.
     */
    public void updateCharacter(CharacterEntity character) {
        logger.info("Entering updateCharacter() for id={}", character.getCharacterId());

        try {
            Optional<CharacterEntity> optionalEntity = characterRepository.findById(character.getCharacterId());

            if (optionalEntity.isPresent()) {
                CharacterEntity entity = optionalEntity.get();

                entity.setCharacterName(character.getCharacterName());
                entity.setCharacterLevel(character.getCharacterLevel());
                entity.setCharacterGender(character.getCharacterGender());
                entity.setCharacterType(character.getCharacterType());
                entity.setCharacterDescription(character.getCharacterDescription());
                entity.setImageUrl(character.getImageUrl());
                entity.setVisibility(character.getVisibility());

                RaceEntity race =
                        raceRepository.findById(character.getRace().getRaceId()).orElse(null);
                ClassEntity charClass = classRepository
                        .findById(character.getCharacterClass().getClassId())
                        .orElse(null);

                entity.setRace(race);
                entity.setCharacterClass(charClass);

                entity.setUpdatedAt(LocalDateTime.now());

                characterRepository.save(entity);

                logger.info("Character updated successfully for id={}", character.getCharacterId());
            } else {
                logger.warn("Attempted update on non-existent character id={}", character.getCharacterId());
            }

            logger.info("Exiting updateCharacter()");
        } catch (Exception e) {
            logger.error("Error in updateCharacter() for id={}: {}", character.getCharacterId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deletes a character from the database by its ID. The method first checks if a character with the specified ID exists, and if it does, it deletes the character.
     * If no character is found with the given ID, it logs a warning and does not perform any deletion.
     * @param id the ID of the character to be deleted from the database
     */
    public void deleteCharacter(int id) {
        logger.info("Entering deleteCharacter() with id={}", id);

        try {
            characterRepository.deleteById(id);

            logger.info("Character deleted successfully for id={}", id);
            logger.info("Exiting deleteCharacter()");
        } catch (Exception e) {
            logger.error("Error in deleteCharacter() for id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Flags a character by its ID, marking it as inappropriate or needing review.
     * The method first retrieves the character by ID, checks if it exists and is not already flagged,
     * then sets the flagged status to true and updates the updatedAt timestamp before saving the changes to the database.
     * If the character does not exist or is already flagged, it logs a warning and does not perform any update.
     * @param id the ID of the character to be flagged in the database
     */
    public void flagCharacter(int id)
    {
        CharacterEntity character = findById(id);

        if (character != null && !Boolean.TRUE.equals(character.getFlagged()))
        {
            character.setFlagged(true);
            character.setUpdatedAt(LocalDateTime.now());

            characterRepository.save(character);
        }
    }

    /**
     * Unflags a character by its ID, removing the flagged status. The method first retrieves the character by ID,
     * checks if it exists and is currently flagged, then sets the flagged status to false and updates the updatedAt timestamp before saving the changes to the database.
     * If the character does not exist or is not currently flagged, it logs a warning and does not perform any update.
     * @param id the ID of the character to be unflagged in the database
     */
    public void unflagCharacter(int id)
    {
        CharacterEntity character = findById(id);

        if (character != null && Boolean.TRUE.equals(character.getFlagged()))
        {
            character.setFlagged(false);
            character.setUpdatedAt(LocalDateTime.now());

            characterRepository.save(character);
        }
    }

    /* HELPER METHODS */
    
    /**
     * Checks if the given user has an admin role. A user is considered an admin if their role is not null and equals "ADMIN" (case-insensitive).
     * This method is used to determine if a user should have access to all characters regardless of visibility and flagging status.
     * @param user  the currently authenticated user (null if guest)
     * @return true if the user is an admin, false otherwise
     */
    private boolean isAdmin(UserEntity user)
    {
        return user != null
                && user.getRole() != null
                && user.getRole().equalsIgnoreCase("ADMIN");
    }
}
