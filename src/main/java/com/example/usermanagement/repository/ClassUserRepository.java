package com.example.usermanagement.repository;

import com.example.usermanagement.entity.ClassUser;
import com.example.usermanagement.entity.composite_key.ClassUserCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassUserRepository extends JpaRepository<ClassUser, ClassUserCompositeKey> {
    List<ClassUser> findByClassId_ClassId(String classId);

}
