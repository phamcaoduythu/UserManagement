package com.example.usermanagement.dto.Response;

import java.time.LocalDate;

public interface ListTrainingProgramResponse {
    String getName();


    LocalDate getCreateDate();

    String getCreateBy();

    int getDuration();

    String getStatus();


}
