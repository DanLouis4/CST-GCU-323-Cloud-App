package com.gcu.business;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gcu.data.ClassRepository;
import com.gcu.models.ClassEntity;

/**
 * Service class for handling business logic related to ClassEntity. It interacts with the ClassRepository to perform database operations.
 * This class is annotated with @Service, indicating that it's a service component in the Spring framework. It provides a method to retrieve all classes from the database.
 */
@Service
public class ClassDatabaseService {

    private final ClassRepository classRepository;

    /**
     * Constructor for ClassDatabaseService.
     * @param classRepository the ClassRepository instance to be injected by Spring for database operations related to ClassEntity.
     */
    public ClassDatabaseService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    /**
     * Gets all classes from the database.
     * @return List of ClassEntity objects representing all classes in the database.
     */
    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }
}
