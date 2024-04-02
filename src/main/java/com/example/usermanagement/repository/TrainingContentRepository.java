package com.example.usermanagement.repository;

import com.example.usermanagement.entity.TrainingContent;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitTrainingContentCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingContentRepository extends JpaRepository<TrainingContent, SyllabusTrainingUnitTrainingContentCompositeKey> {
}
