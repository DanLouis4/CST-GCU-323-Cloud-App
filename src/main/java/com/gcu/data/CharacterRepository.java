package com.gcu.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gcu.models.CharacterEntity;

/**
 * CharacterRepository is a Spring Data JPA repository interface for managing CharacterEntity objects.
 * It extends JpaRepository, providing CRUD operations and custom query methods for character entities.
 */
public interface CharacterRepository extends JpaRepository<CharacterEntity, Integer>
{
    /**
     * Searches for characters using a case-insensitive keyword across multiple fields.
     * The keyword is matched using partial (LIKE) comparisons against:
     * character name, race name, class name, description, type, and username.
     * Gender is matched using an exact comparison.
     *
     * @param keyword the search term entered by the user
     * @return a list of CharacterEntity objects matching the search criteria
     */
    @Query("""
        SELECT c
        FROM CharacterEntity c
        LEFT JOIN c.race r
        LEFT JOIN c.characterClass cc
        LEFT JOIN c.user u
        WHERE
            LOWER(c.characterName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(r.raceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(cc.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(c.characterDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(c.characterGender) = LOWER(:keyword) OR
            LOWER(c.characterType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)

    /**
     * Searches for characters based on a keyword that can match various fields such as character
     * @param keyword
     * @return a list of CharacterEntity objects that match the search criteria
     */
    List<CharacterEntity> searchCharacters(@Param("keyword") String keyword);

}