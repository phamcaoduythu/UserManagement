package com.example.usermanagement.service;

import com.example.usermanagement.dto.Response.ResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TrainingMaterialService {

    ResponseEntity<ResponseObject> uploadTrainingMaterial(MultipartFile file) throws IOException;

    byte[] downloadTrainingMaterials(String fileName) throws IOException, RuntimeException;

    ResponseEntity<ResponseObject> deleteTrainingMaterial(String fileName);

}
