package com.example.usermanagement.repository;

import com.example.usermanagement.dto.Response.ListTrainingProgramResponse;
import com.example.usermanagement.entity.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Integer> {

    @Query(value = "SELECT tp.name,tp.created_by as 'createBy',tp.created_date as 'createDate',tp.duration,tp.status FROM training_programs tp", nativeQuery = true)
    List<ListTrainingProgramResponse> getAllTrainingProgram();

    Optional<TrainingProgram> getTrainingProgramByName(String name);

    Optional<TrainingProgram> findByName(String trainingProgramName);

    @Query(value = "SELECT tp.name,tp.created_by as 'createBy',tp.created_date as 'createDate',tp.duration,tp.status FROM training_programs tp WHERE tp.name LIKE %:name%", nativeQuery = true)
    List<ListTrainingProgramResponse> findByNameV2(String name);

    @Query(value = "SELECT tp.name FROM training_programs tp WHERE tp.name LIKE %:name%", nativeQuery = true)
    List<String> findByName2(String name);

    @Query("SELECT tp FROM TrainingProgram tp where tp.trainingProgramCode = :id")
    TrainingProgram getTrainingProgramByID(int id);

    @Query("SELECT tp FROM TrainingProgram tp where tp.name = :name")
    Optional<TrainingProgram> getTrainingProgramByName1(String name);


}
