package com.example.usermanagement.repository;

import com.example.usermanagement.entity.TrainingUnit;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitCompositeKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainingUnitRepository extends JpaRepository<TrainingUnit, SyllabusTrainingUnitCompositeKey> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM training_material " +
            "WHERE unit_code IN (" +
            "    SELECT unit_code FROM training_unit WHERE topic_code = :topicCode" +
            ") " +
            "AND topic_code = :topicCode", nativeQuery = true)
    void deleteTrainingMaterialByTopicCode(@Param("topicCode") String topicCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM training_content " +
            "WHERE unit_code IN (" +
            "    SELECT unit_code FROM training_unit WHERE topic_code = :topicCode" +
            ") " +
            "AND topic_code = :topicCode", nativeQuery = true)
    void deleteTrainingContentByTopicCode(@Param("topicCode") String topicCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM training_unit WHERE topic_code = :topicCode", nativeQuery = true)
    void deleteTrainingUnitByTopicCode(@Param("topicCode") String topicCode);
}
