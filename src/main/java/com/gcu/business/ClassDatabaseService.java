package com.gcu.business;

import org.springframework.stereotype.Service;
import com.gcu.data.ClassRepository;
import com.gcu.models.ClassEntity;  
import java.util.List;

@Service
public class ClassDatabaseService {

    private final ClassRepository classRepository;

    public ClassDatabaseService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }
}
