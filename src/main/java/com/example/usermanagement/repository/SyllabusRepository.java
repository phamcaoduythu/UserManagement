package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SyllabusRepository extends JpaRepository<Syllabus, String> {
    @Query(value = "SELECT sy.topic_name FROM syllabus sy WHERE sy.topic_name LIKE %:name%", nativeQuery = true)
    List<String> findByName2(String name);

    // Search Syllabus by topicName, topicCode, createdBy, output Code
    @Query("SELECT s FROM Syllabus s " +
            "JOIN s.syllabusObjectives so " +
            "WHERE LOWER(s.topicName) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
            "OR LOWER(s.topicCode) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
            "OR LOWER(s.createdBy.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
            "OR LOWER(so.outputCode) LIKE LOWER(CONCAT('%', :searchValue, '%'))")
    List<Syllabus> searchSyllabus(String searchValue);

    // Search Syllabus by createdDate (*Using DatePicker*)
    @Query("SELECT s FROM Syllabus s WHERE s.createdDate = :createdDate")
    List<Syllabus> findByCreatedDate(LocalDate createdDate);

    // Calculate the sum duration of all training content for each syllabus
    @Query(value = "SELECT COALESCE(SUM(tc.duration), 0) " +
            "FROM training_content tc " +
            "WHERE tc.topic_code = :topicCode", nativeQuery = true)
    int getTotalDurationOfTrainingContentByTopicCode(@Param("topicCode") String topicCode);

    // Get the list of StandardOutput(output code only) for each syllabus
    @Query(value = "SELECT so.output_code " +
            "FROM syllabus s " +
            "JOIN syllabus_objective so ON s.topic_code = so.topic_code " +
            "WHERE s.topic_code = :topicCode", nativeQuery = true)
    List<String> findOutputCodesByTopicCode(@Param("topicCode") String topicCode);

    //Get the list of syllabus with status "active, draft" and sort by day created desc
    @Query("SELECT s FROM Syllabus s WHERE s.publishStatus IN :statuses AND s.deleted = false ORDER BY s.createdDate DESC")
    List<Syllabus> findBySyllabusStatusInOrderByCreatedDateDesc(List<String> statuses);

}
