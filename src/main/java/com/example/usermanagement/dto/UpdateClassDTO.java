package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/*
 * author: Le Ngoc Tam Nhu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassDTO {
    private String className;
    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private String attendeeActual;
    private String attendee;
    private String attendeePlanned;
    private String attendeeAccepted;
    private String classTimeFrom;
    private String classTimeTo;
    private String location;
    private String status;
    private String trainingProgram;
    private List<String> listDay;
    private List<String> attendeeList;
    private List<String> admin;
    private List<TrainerSyllabusDTO> trainer;
    private String moderEmail;
}

