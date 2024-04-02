package com.example.usermanagement.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassDTO {
    String classCode;
    String nameClass;
    String classTimeFrom;
    String classTimeTo;
    String location;
    String startDate;
    String endDate;
    String createdBy;
    String attendee;
    int attendeePlanned;
    int attendeeAccepted;
    int attendeeActual;
    int trainingProgram;
    List<String> listDay;
    List<String> admin;
    List<String> attendeeList;
    List<TrainerSyllabusRequest> trainer;

}
