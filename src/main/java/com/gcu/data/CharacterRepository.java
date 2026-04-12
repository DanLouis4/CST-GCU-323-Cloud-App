package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.gcu.models.CharacterEntity;
import java.util.List;


public interface CharacterRepository extends JpaRepository<CharacterEntity, Integer>
{
    // Custom query to search characters based on multiple fields
    // This query uses JPQL to search for characters where any of the specified fields contain the keyword
    // The query performs a case-insensitive search by converting both the field and the keyword to lower case
    // The CONCAT function is used to add wildcards (%) around the keyword for partial matching
    // The LEFT JOINs are used to include related entities (Race and Class) in the search criteria
    @Query("""
        SELECT c
        FROM CharacterEntity c
        LEFT JOIN c.race r
        LEFT JOIN c.characterClass cc
        WHERE
            LOWER(c.characterName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(r.raceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(cc.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(c.characterDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(c.characterGender) = LOWER(:keyword) OR
            LOWER(c.characterType) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)

    // Method signature for the search query, using @Param to bind the keyword parameter
    List<CharacterEntity> searchCharacters(@Param("keyword") String keyword);

}