package com.gcu.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classes")
public class ClassEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "class_name", nullable = false, unique = true)
    private String className;

    @Column(name = "class_type")
    private String classType;

    @Column(name = "class_description")
    private String classDescription;

    @Column(name = "added_on")
    private LocalDateTime addedOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    public ClassEntity()
    {
    }

    public Integer getClassId()
    {
        return classId;
    }

    public void setClassId(Integer classId)
    {
        this.classId = classId;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassType()
    {
        return classType;
    }

    public void setClassType(String classType)
    {
        this.classType = classType;
    }

    public String getClassDescription()
    {
        return classDescription;
    }

    public void setClassDescription(String classDescription)
    {
        this.classDescription = classDescription;
    }

    public LocalDateTime getAddedOn()
    {
        return addedOn;
    }

    public void setAddedOn(LocalDateTime addedOn)
    {
        this.addedOn = addedOn;
    }

    public LocalDateTime getUpdatedOn()
    {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn)
    {
        this.updatedOn = updatedOn;
    }
}