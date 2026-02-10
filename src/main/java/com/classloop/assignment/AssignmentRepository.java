package com.classloop.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findByClassId(UUID classId);
    List<Assignment> findByClassIdIn(List<UUID> classIds);
}
