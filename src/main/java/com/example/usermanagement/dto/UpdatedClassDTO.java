package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedClassDTO {
    String attendee;
    private String classCode;
    private String className;
    private String duration;
    private String classTimeFrom;
    private String classTimeTo;
    private String status;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String attendeePlanned;
    private String attendeeAccepted;
    private String attendeeActual;
//    List<String> listDay;
//    List<String> attendeeList;
//    List<String> adminList;
}
