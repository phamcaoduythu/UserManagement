package com.example.usermanagement.repository;

import com.example.usermanagement.entity.SyllabusObjective;
import com.example.usermanagement.entity.composite_key.SyllabusStandardOutputCompositeKey;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SyllabusObjectiveRepository extends JpaRepository<SyllabusObjective, SyllabusStandardOutputCompositeKey> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM syllabus_objective WHERE topic_code = :topicCode", nativeQuery = true)
    void deleteByTopicCode(@Param("topicCode") String topicCode);
}
