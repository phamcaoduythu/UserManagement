package com.example.usermanagement.controller;


import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.service.TrainingMaterialService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/training-material")
@RequiredArgsConstructor
@SecurityRequirement(name = "FSA_Phase_1")
public class TrainingMaterialController {

    private final TrainingMaterialService trainingMaterialService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseObject> uploadTrainingMaterial(
            @RequestBody MultipartFile file) throws IOException {
        return trainingMaterialService.uploadTrainingMaterial(file);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadTrainingMaterial(@PathVariable String fileName) {
        try {
            byte[] data = trainingMaterialService.downloadTrainingMaterials(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity.ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header(
                            "Content-disposition",
                            "attachment; filename=\"" + fileName + "\""
                    )
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fail to download file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<ResponseObject> deleteTrainingMaterial(@PathVariable String fileName
    ) {
        return trainingMaterialService.deleteTrainingMaterial(fileName);
    }

}
