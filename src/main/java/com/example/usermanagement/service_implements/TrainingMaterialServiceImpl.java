package com.example.usermanagement.service_implements;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.TrainingMaterial;
import com.example.usermanagement.repository.TrainingMaterialRepository;
import com.example.usermanagement.service.TrainingMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingMaterialServiceImpl implements TrainingMaterialService {

    private final AmazonS3 s3Client;
    private final TrainingMaterialRepository trainingMaterialRepository;
    @Value("${application.bucket.name}")
    private String bucketName;

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    @Override
    public byte[] downloadTrainingMaterials(String fileName) throws IOException, RuntimeException {
        if (isFileExisted(fileName)) {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                return IOUtils.toByteArray(inputStream);
            }
        } else {
            throw new RuntimeException("File not found! Runtime error.");
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> deleteTrainingMaterial(String fileName) {

        if (isFileExisted(fileName)) {
            s3Client.deleteObject(bucketName, fileName);
            return ResponseEntity
                    .ok()
                    .body(ResponseObject.builder()
                            .status("Success")
                            .message("File " + fileName + " removed.")
                            .build());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseObject.builder()
                            .status("Failure")
                            .message("File " + fileName + " is not found.")
                            .build());
        }

    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    public boolean isFileExisted(String fileName) {
        List<TrainingMaterial> list = trainingMaterialRepository.findAll();
        for (TrainingMaterial tm : list) {
            if (tm.getMaterial().equalsIgnoreCase(fileName))
                return true;
        }
        return false;
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> uploadTrainingMaterial(MultipartFile file) throws IOException {
        try {
            File fileObj = convertMultiPartFileToFile(file);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            var result = s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            TrainingMaterial trainingMaterial = TrainingMaterial.builder()
                    .material(fileName)
                    .source("https://fsaphase1.s3.ap-southeast-2.amazonaws.com/" + fileName)
                    .build();
            var savedTrainingMaterial = trainingMaterialRepository.save(trainingMaterial);
            fileObj.delete();
            log.info("Uploading Training material successfully!");
            return ResponseEntity.ok(new ResponseObject("Success", "File" + fileName + "uploaded", savedTrainingMaterial));
        } catch (Exception e) {
            log.info("An error occurred while trying to upload Training material. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to upload Training material", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Failed to convert multipartFile to file. Error message: ", e);
        }
        return convertedFile;
    }

}