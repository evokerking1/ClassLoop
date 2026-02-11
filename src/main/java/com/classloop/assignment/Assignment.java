package com.classloop.assignment;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID classId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private ZonedDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType type = AssignmentType.HOMEWORK;

    private Integer estimatedWorkloadMinutes;

    public Assignment() {
    }

    public Assignment(UUID classId, String title, String description, ZonedDateTime dueDate, 
                     AssignmentType type, Integer estimatedWorkloadMinutes) {
        this.classId = classId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.type = type;
        this.estimatedWorkloadMinutes = estimatedWorkloadMinutes;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public AssignmentType getType() {
        return type;
    }

    public void setType(AssignmentType type) {
        this.type = type;
    }

    public Integer getEstimatedWorkloadMinutes() {
        return estimatedWorkloadMinutes;
    }

    public void setEstimatedWorkloadMinutes(Integer estimatedWorkloadMinutes) {
        this.estimatedWorkloadMinutes = estimatedWorkloadMinutes;
    }

    public enum AssignmentType {
        HOMEWORK, QUIZ, TEST, PROJECT
    }
}
