package com.example.usermanagement.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.TrainingMaterial;
import com.example.usermanagement.repository.TrainingMaterialRepository;
import com.example.usermanagement.service_implements.TrainingMaterialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingMaterialServiceImplTest {
    @InjectMocks
    private TrainingMaterialServiceImpl materialService;

    @Mock
    private TrainingMaterialRepository materialRepository;

    @Mock
    private AmazonS3 amazonS3;


    @BeforeEach
    public void setUp() {
        materialRepository = mock(TrainingMaterialRepository.class);
        MockitoAnnotations.initMocks(this);
        materialService = new TrainingMaterialServiceImpl(amazonS3, materialRepository);
    }

    @Test
    public void test_deleteTrainingMaterial_FileExists() {
        String fileName = "file";
        List<TrainingMaterial> mockList = new ArrayList<>();
        TrainingMaterial mockTrainingMaterial = new TrainingMaterial();
        mockTrainingMaterial.setMaterial(fileName);
        mockList.add(mockTrainingMaterial);
        when(materialRepository.findAll()).thenReturn(mockList);
        ResponseEntity<ResponseObject> responseEntity = materialService.deleteTrainingMaterial(fileName);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", responseEntity.getBody().getStatus());
        assertEquals("File file removed.", responseEntity.getBody().getMessage());
    }

    @Test
    public void test_deleteTrainingMaterial_FileNotExists() {
        String fileName = "file";
        ResponseEntity<ResponseObject> responseEntity = materialService.deleteTrainingMaterial(fileName);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Failure", responseEntity.getBody().getStatus());
        assertEquals("File file is not found.", responseEntity.getBody().getMessage());
    }

    @Test
    public void test_uploadTrainingMaterial_Success() throws IOException {
        TrainingMaterial trainingMaterial1 = TrainingMaterial.builder()
                .material("file.txt")
                .source("https://usemanagment.com/file.txt")
                .build();
        MultipartFile file = new MockMultipartFile("file", "file.txt", "file/plain", "Hello Everyone!".getBytes());
        File convertedFile = new File("test.txt");
        when(materialRepository.save(any())).thenReturn(trainingMaterial1);

        ResponseEntity<ResponseObject> responseEntity = materialService.uploadTrainingMaterial(file);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", responseEntity.getBody().getStatus());
//        assertEquals("File test.txt uploaded", responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getPayload());

    }

    @Test
    public void test_uploadTrainingMaterial_Failure() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "file.txt", "file/plain", "Hello Everyone!".getBytes());
        when(materialRepository.save(any())).thenThrow(new RuntimeException("Database error"));
        ResponseEntity<ResponseObject> responseEntity = materialService.uploadTrainingMaterial(file);


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Failure", responseEntity.getBody().getStatus());
        assertEquals("Failed to upload Training material", responseEntity.getBody().getMessage());

    }

}
