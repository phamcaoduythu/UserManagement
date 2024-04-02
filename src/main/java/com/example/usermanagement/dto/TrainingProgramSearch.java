package com.example.usermanagement.dto;

import java.time.LocalDate;

public interface TrainingProgramSearch {
    String getTrainingProgramCode();

    String getName();

    LocalDate getStartDate();

    String getStatus();

    String getUser();

    String getCreatedBy();

    LocalDate getCreatedDate();

    int getDuration();

    String getModifiedBy();

    LocalDate getModifiedDate();

}
