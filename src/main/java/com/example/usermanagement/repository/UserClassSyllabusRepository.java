package com.example.usermanagement.repository;

import com.example.usermanagement.entity.UserClassSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserClassSyllabusRepository extends JpaRepository<UserClassSyllabus, Integer> {
    List<UserClassSyllabus> findByClassCode_ClassId(String classCode);
}
