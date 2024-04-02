package com.example.usermanagement.service;

import com.example.usermanagement.dto.Request.CreateSyllabusOutlineRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.SyllabusResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface SyllabusService {

    ResponseEntity<List<SyllabusResponse>> viewAllSyllabus();

    ResponseEntity<ResponseMessage> createSyllabus(CreateSyllabusOutlineRequest request);

    ResponseEntity<List<SyllabusResponse>> searchSyllabus(String searchValue);

    ResponseEntity<List<SyllabusResponse>> searchSyllabusByCreatedDate(LocalDate createdDate);

    ResponseEntity<ResponseMessage> updateSyllabus(CreateSyllabusOutlineRequest request, String topicCode);

    ResponseEntity<ResponseMessage> deleteSyllabus(String topicCode);

    ResponseEntity<ResponseMessage> duplicateSyllabus(String syllabusCode);

    ResponseEntity<ResponseObject> getDetailSyllabus(String topicCode);

    ResponseEntity<ResponseMessage> importSyllabus(String filename, String choice);

}
