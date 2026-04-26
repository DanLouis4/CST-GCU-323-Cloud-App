package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.models.ClassEntity;

/**
 * ClassRepository is an interface that extends JpaRepository to provide CRUD operations for ClassEntity objects.
 * It includes a method to find a ClassEntity by its className.
 */
public interface ClassRepository extends JpaRepository<ClassEntity, Integer>
{
    /**
     * Finds a ClassEntity by its className.
     * @param className the name of the class to search for.
     * @return the ClassEntity with the specified className, or null if not found.
     */
    ClassEntity findByClassName(String className);
}