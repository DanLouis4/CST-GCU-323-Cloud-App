package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gcu.models.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer>
{
    ClassEntity findByClassName(String className);
}