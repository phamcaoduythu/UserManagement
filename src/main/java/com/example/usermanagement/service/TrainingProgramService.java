package com.example.usermanagement.service;

import com.example.usermanagement.dto.Request.CreateTrainingProgramRequest;
import com.example.usermanagement.dto.Request.UpdateTrainingProgramRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;

public interface TrainingProgramService {

    ResponseEntity<ResponseMessage> updateTrainingProgram(UpdateTrainingProgramRequest trainingProgram, int id);

    ResponseEntity<ResponseObject> getTrainingProgramByName(String name);

    ResponseEntity<ResponseObject> createTrainingProgram(CreateTrainingProgramRequest trainingProgramRequest);

    ResponseEntity<ResponseObject> getListTrainingProgram();

    ResponseEntity<ResponseMessage> changeTrainingProgramStatus(int trainingProgramCode, String value);

    ResponseEntity<ResponseMessage> duplicateTrainingProgram(String name);

    ResponseEntity<ResponseMessage> importTrainingProgram(String file, String choice) throws FileNotFoundException;

}
