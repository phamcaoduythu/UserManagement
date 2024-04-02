package com.example.usermanagement.repository;

import com.example.usermanagement.entity.TrainingProgramSyllabus;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingProgramCompositeKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingProgramSyllabusRepository extends JpaRepository<TrainingProgramSyllabus, SyllabusTrainingProgramCompositeKey> {
    Optional<TrainingProgramSyllabus> findByIdTopicCodeAndIdTrainingProgramCode(String topicCode, int trainingProgramCode);

    @Query(value = "SELECT tp.name AS TrainingProgramName " +
            "FROM training_programs tp " +
            "JOIN training_program_syllabuses tsm ON tp.training_program_code = tsm.training_programs_code " +
            "WHERE tsm.topic_code = :topicCode", nativeQuery = true)
    List<String> findTrainingProgramNamesByTopicCode(@Param("topicCode") String topicCode);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM training_program_syllabuses WHERE topic_code = :topicCode", nativeQuery = true)
    void deleteByTopicCode(@Param("topicCode") String topicCode);
}
