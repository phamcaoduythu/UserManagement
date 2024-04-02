package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Class;
import com.example.usermanagement.entity.ClassLearningDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ClassLearningDayRepository extends JpaRepository<ClassLearningDay, Integer> {
    List<ClassLearningDay> findByClassId_ClassId(String id);

    @Query(value = "SELECT c FROM ClassLearningDay c WHERE c.classId = :classId and c.enrollDate = :enrollDate")
    ClassLearningDay findByClassIdAndAndEnrollDate(Class classId, LocalDate enrollDate);

    @Query(value = "SELECT c FROM ClassLearningDay c WHERE c.enrollDate = :enrollDate")
    List<ClassLearningDay> findByEnrollDate(LocalDate enrollDate);

}
