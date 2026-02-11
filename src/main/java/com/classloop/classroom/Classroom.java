package com.classloop.classroom;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "classes")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String subject;

    @Column(nullable = false)
    private UUID teacherId;

    private Integer period;

    public Classroom() {
    }

    public Classroom(String name, String subject, UUID teacherId, Integer period) {
        this.name = name;
        this.subject = subject;
        this.teacherId = teacherId;
        this.period = period;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public UUID getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(UUID teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
}
