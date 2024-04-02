package com.example.usermanagement.dto.Response;

import com.example.usermanagement.dto.SyllabusDTO;
import com.example.usermanagement.dto.TrainerDTO;
import com.example.usermanagement.dto.UserDTOClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailResponse {
    List<String> classLearningDays = new ArrayList<>();
    //    TrainingProgramDTO trainingProgram;
    String trainingProgram;
    List<SyllabusDTO> syllabusList = new ArrayList<>();
    //private String message = "";
    private String classCode;
    private String nameClass;
    private String duration;
    private String status;
    private String classTimeFrom;
    private String classTimeTo;
    private String location;
    private String startDate;
    private String endDate;
    private String createdBy;
    private String review;
    private String approve;
    private String attendee;
    private String attendeePlanned;
    private String attendeeAccepted;
    private String attendeeActual;
    private String createdDate;
    private String modifiedBy = null;
    private String modifiedDate = null;
    private boolean deactivated = false;
    private List<TrainerDTO> trainer = new ArrayList<>();
    private List<UserDTOClass> admin = new ArrayList<>();
    private List<UserDTOClass> attendeeList = new ArrayList<>();

}
