package com.example.usermanagement.service;

import com.example.usermanagement.dto.Request.CreateClassDTO;
import com.example.usermanagement.dto.Request.UpdateCalendarRequest;
import com.example.usermanagement.dto.Request.UpdateClass3Request;
import com.example.usermanagement.dto.Response.CreateClassResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.UpdateClass3Response;
import com.example.usermanagement.dto.Response.UpdateClassResponse;
import com.example.usermanagement.dto.UpdateClassDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClassService {

    ResponseEntity<CreateClassResponse> createClass(CreateClassDTO request);

    ResponseEntity<ResponseObject> getAllClass();

    ResponseEntity<ResponseObject> getClassById(String classCode);

    ResponseEntity<String> deactivateClass(String classCode);

    UpdateClassResponse updateClass(UpdateClassDTO updateClassRequest, String classCode);

    UpdateClass3Response updateClass3(UpdateClass3Request updateClass3Request);

    ResponseObject updateClassLearningDay(UpdateCalendarRequest updateCalendarRequest);

    ResponseEntity<ResponseObject> getClassByName(String name);

    ResponseEntity<ResponseObject> filterClass(List<String> location, List<String> classTime, String startDate, String endDate, List<String> status, String trainer);

}

