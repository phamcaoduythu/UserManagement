package com.example.usermanagement.Service;

import com.example.usermanagement.dto.ContentDTO;
import com.example.usermanagement.dto.MaterialDTO;
import com.example.usermanagement.dto.Request.CreateSyllabusOutlineRequest;
import com.example.usermanagement.dto.Request.DayDTO;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.SyllabusResponse;
import com.example.usermanagement.dto.UnitDTO;
import com.example.usermanagement.entity.*;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitCompositeKey;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitTrainingContentCompositeKey;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.*;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service_implements.SyllabusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SyllabusManagementTests {

    @Mock
    private SyllabusRepository syllabusRepository;
    @Mock
    private UserPermissionRepository userPermissionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JWTService jwtService;
    @Mock
    private StandardOutputRepository standardOutputRepository;
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private TrainingUnitRepository trainingUnitRepository;
    @Mock
    private TrainingContentRepository trainingContentRepository;
    @Mock
    private TrainingMaterialRepository trainingMaterialRepository;
    @Mock
    private TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;
    @Mock
    private SyllabusObjectiveRepository syllabusObjectiveRepository;
    @InjectMocks
    private SyllabusServiceImpl syllabusService;
    private User user;
    private CreateSyllabusOutlineRequest request;
    private Syllabus syllabus;
    /*
     * author: Ho Van Loc An
     * since: 3/21/2024 4:13 PM
     * description: tham khảo code trên mạng thì nhớ phải tìm hiểu kĩ nha
     * và nhớ chỉnh code lại nha mọi người, đừng để nó quá là hiện hữu
     * ở dưới nó hơi công nghiệp hóa dữ liệu rồi
     * sợ anh check là ra nguồn đó.
     * update:
     */

    @BeforeEach
    public void setUp() {
        // Mock user retrieval from userRepository
        user = User.builder().email("admin@gmail.com").password(passwordEncoder.encode("1")).name("Admin").phone("0977545450").dob(LocalDate.now()).gender("Male").role(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null)).status(true).createdBy("Admin").createdDate(LocalDate.now()).modifiedBy("Admin").modifiedDate(LocalDate.now()).build();

        request = CreateSyllabusOutlineRequest.builder().trainingProgramName(new String[]{"Test1"}).topicName("Topic Name").topicOutline("Topic Outline").version("1.0").technicalGroup("Technical Group").priority("High").courseObjective("Course Objective").publishStatus("inactive").trainingPrinciples("Training Principles").trainingAudience(50).assignmentLab("0%").conceptLecture("0%").guideReview("0%").testQuiz("0%").exam("0%").quiz("0%").assignment("0%").finalValue("0%").finalTheory("0%").finalPractice("0%").gpa("0%").outputCode(new String[]{"H4SD"}).syllabus(Collections.singletonList(DayDTO.builder().dayNumber(1).unitList(Collections.singletonList(UnitDTO.builder().unitName("Unit 1").contentList(Collections.singletonList(ContentDTO.builder().contentName("Content 1").deliveryType("Delivery Type 1").online(true).duration(60).note("Note").standardOutput("H4SD").trainingMaterial(Collections.singletonList(MaterialDTO.builder().material("Material 1").source("trainingAudience").build())).build())).build())).build())).build();

        syllabus = Syllabus.builder().assignment("0%").assignmentLab("0%").conceptLecture("0%").courseObjective("Course Objective").exam("0%").finalPractice("0%").finalTheory("0%").final_("0%").gpa("0%").guideReview("0%").modifiedDate(LocalDate.now()).priority("High").publishStatus("inactive").quiz("0%").technicalGroup("Technical Group").testQuiz("0%").topicName("Topic Name").topicOutline("Topic Outline").trainingAudience(50).trainingPrinciples("Training Principles").version("1.0").build();
    }

    @Test
    void test_returnEmptyList_SearchSyllabus() {
        // Setup
        String searchValue = "non-existent";
        when(syllabusRepository.searchSyllabus(searchValue)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.searchSyllabus(searchValue);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    @Test
    void test_returnSyllabusResponse_SearchSyllabus() {
        // Setup
        String searchValue = "existing";
        List<Syllabus> syllabusList = new ArrayList<>();
        Syllabus testSyllabus = new Syllabus();
        testSyllabus.setTopicName("topicName");
        testSyllabus.setTopicCode("ICD");
        testSyllabus.setPublishStatus("active");
        testSyllabus.setCreatedBy(user);

        syllabusList.add(testSyllabus);
        when(syllabusRepository.searchSyllabus(searchValue)).thenReturn(syllabusList);
        when(syllabusRepository.getTotalDurationOfTrainingContentByTopicCode(anyString())).thenReturn(60);
        when(syllabusRepository.findOutputCodesByTopicCode(anyString())).thenReturn(Collections.singletonList("outputCode"));

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.searchSyllabus(searchValue);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals("topicName", responseEntity.getBody().get(0).getSyllabusName());
        assertEquals("ICD", responseEntity.getBody().get(0).getSyllabusCode());
        assertEquals(60, responseEntity.getBody().get(0).getDuration());
        assertEquals("active", responseEntity.getBody().get(0).getSyllabusStatus());
        assertEquals(Collections.singletonList("outputCode"), responseEntity.getBody().get(0).getSyllabusObjectiveList());
    }

    @Test
    void test_returnExceptionError_SearchSyllabus() {
        // Setup
        String searchValue = "existing";
        when(syllabusRepository.searchSyllabus(searchValue)).thenThrow(new RuntimeException("Test Exception"));

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.searchSyllabus(searchValue);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    @Test
    void test_returnEmptyList_SearchSyllabusByCreatedDate() {
        // Setup
        LocalDate createdDate = LocalDate.now();
        when(syllabusRepository.findByCreatedDate(createdDate)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.searchSyllabusByCreatedDate(createdDate);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    @Test
    void test_returnSyllabusResponse_SearchSyllabusByCreatedDate() {
        // Setup
        LocalDate createdDate = LocalDate.now();
        List<Syllabus> syllabusList = new ArrayList<>();
        Syllabus testSyllabus = new Syllabus();
        testSyllabus.setTopicName("topicName");
        testSyllabus.setTopicCode("ICD");
        testSyllabus.setPublishStatus("active");
        // Setup - Mocking createdBy
        User user = new User();
        user.setName("createdBy");
        testSyllabus.setCreatedBy(user);
        syllabusList.add(testSyllabus);
        when(syllabusRepository.findByCreatedDate(createdDate)).thenReturn(syllabusList);
        when(syllabusRepository.getTotalDurationOfTrainingContentByTopicCode(anyString())).thenReturn(60);
        when(syllabusRepository.findOutputCodesByTopicCode(anyString())).thenReturn(Collections.singletonList("outputCode"));

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.searchSyllabusByCreatedDate(createdDate);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals("topicName", responseEntity.getBody().get(0).getSyllabusName());
        assertEquals("ICD", responseEntity.getBody().get(0).getSyllabusCode());
        assertEquals("createdBy", responseEntity.getBody().get(0).getCreatedBy());
        assertEquals(60, responseEntity.getBody().get(0).getDuration());
        assertEquals("active", responseEntity.getBody().get(0).getSyllabusStatus());
        assertEquals(Collections.singletonList("outputCode"), responseEntity.getBody().get(0).getSyllabusObjectiveList());
    }

    @Test
    void test_returnExceptionError_SearchSyllabusByCreatedDate() {
        // Setup

        LocalDate createdDate = LocalDate.now();
        when(syllabusRepository.findByCreatedDate(createdDate)).thenThrow(new RuntimeException("Test Exception"));

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.searchSyllabusByCreatedDate(createdDate);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    @Test
    void test_returnSuccessResponse_DeleteSyllabus() {
        // Setup
        String topicCode = "existing";
        Syllabus existingSyllabus = new Syllabus();
        when(syllabusRepository.findById(topicCode)).thenReturn(Optional.of(existingSyllabus));

        // Act
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.deleteSyllabus(topicCode);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
        assertEquals("Syllabus with Id: existing has been deleted successfully!", responseEntity.getBody().getMessage());
        assertTrue(existingSyllabus.isDeleted()); // Ensure syllabus is marked as deleted
//        verify(syllabusRepository, times(1)).save(existingSyllabus); // Verify save method called once
    }

    @Test
    void test_returnFailureResponse_whenNoSyllabusFound_DeleteSyllabus() {
        // Setup
        String topicCode = "non-existent";
        when(syllabusRepository.findById(topicCode)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.deleteSyllabus(topicCode);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
        assertEquals("Syllabus with Id: non-existent is not found!", responseEntity.getBody().getMessage());
    }

    @Test
    void test_returnFailureResponse_whenExceptionOccurs_DeleteSyllabus() {
        // Arrange
        String topicCode = "existing";
        when(syllabusRepository.findById(topicCode)).thenReturn(Optional.of(new Syllabus()));
        doThrow(new RuntimeException("Test Exception")).when(syllabusRepository).save(any());

        // Act
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.deleteSyllabus(topicCode);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
        assertEquals("An error occurred while trying to delete Syllabus", responseEntity.getBody().getMessage());
    }

    // view syllabus no list
    @Test
    public void testViewAllSyllabusEmptyList() {
        // Arrange
        when(syllabusRepository.findBySyllabusStatusInOrderByCreatedDateDesc(List.of("active", "draft"))).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.viewAllSyllabus();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // View syllabus error catch
    @Test
    public void testViewAllSyllabusErrorCaught() {
        // Arrange
        when(syllabusRepository.findBySyllabusStatusInOrderByCreatedDateDesc(any())).thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.viewAllSyllabus();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // view syllabus normal list
    @Test
    public void testViewAllSyllabusNormal() {
        // Arrange
        List<Syllabus> syllabusList = new ArrayList<>();
        Syllabus syllabus = new Syllabus();
        syllabus.setTopicName("Test Syllabus");
        syllabus.setTopicCode("TS001");
        syllabus.setPublishStatus("Published");
        syllabus.setCreatedBy(user);
        syllabusList.add(syllabus);

        when(syllabusRepository.findBySyllabusStatusInOrderByCreatedDateDesc(List.of("active", "draft"))).thenReturn(syllabusList);
        when(syllabusRepository.getTotalDurationOfTrainingContentByTopicCode("TS001")).thenReturn(60); // Mocking duration
        when(syllabusRepository.findOutputCodesByTopicCode("TS001")).thenReturn(List.of("SO001", "SO002")); // Mocking list standard output

        // Act
        ResponseEntity<List<SyllabusResponse>> responseEntity = syllabusService.viewAllSyllabus();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size()); // Assuming only one syllabus in the list
        SyllabusResponse syllabusResponse = responseEntity.getBody().get(0);
        // Assert other properties as needed
        assertEquals("Test Syllabus", syllabusResponse.getSyllabusName());
        assertEquals("TS001", syllabusResponse.getSyllabusCode());
        assertEquals("Published", syllabusResponse.getSyllabusStatus());
        assertEquals(60, syllabusResponse.getDuration());
        assertEquals(List.of("SO001", "SO002"), syllabusResponse.getSyllabusObjectiveList());
    }

    // create syllabus with standard output, training program, training outline list
    @Test
    public void testCreateSyllabus() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        // Mocking items existence
        StandardOutput standardOutput = StandardOutput.builder().outputCode("H4SD").outputName("h4sd").description("Output Description").build();
        TrainingProgram trainingProgram = TrainingProgram.builder().trainingProgramCode(1).name("Test1").userID(user).startDate(LocalDate.now()).duration(10).status("Active").createdBy("Admin").createdDate(LocalDate.now()).build();

        // Mocking repository behavior to return existing items
        when(standardOutputRepository.findById("H4SD")).thenReturn(Optional.of(standardOutput));
        when(trainingProgramRepository.getTrainingProgramByName1("Test1")).thenReturn(Optional.of(trainingProgram));

        // Mocking repo save behavior to return a specific object
        Syllabus expectedSyllabus = new Syllabus();
        expectedSyllabus.setTopicCode("ABC123");
        when(syllabusRepository.save(any(Syllabus.class))).thenReturn(expectedSyllabus);

        TrainingUnit expectedTrainingUnit = TrainingUnit.builder().unitName("Unit 1").dayNumber(1).id(SyllabusTrainingUnitCompositeKey.builder().tCode("ABC123").build()).syllabus(expectedSyllabus).build();
        when(trainingUnitRepository.save(any(TrainingUnit.class))).thenReturn(expectedTrainingUnit);

        TrainingContent expectedTrainingContent = TrainingContent.builder().id(SyllabusTrainingUnitTrainingContentCompositeKey.builder().id(SyllabusTrainingUnitCompositeKey.builder().tCode("ABC123").uCode(expectedTrainingUnit.getId().getUCode()).build()).build()).contentName("Content 1").unitCode(expectedTrainingUnit).deliveryType("Delivery Type 1").duration(60).trainingFormat(true).note("Note").outputCode(standardOutput).build();
        when(trainingContentRepository.save(any(TrainingContent.class))).thenReturn(expectedTrainingContent);

        TrainingMaterial expectedTrainingMaterial = TrainingMaterial.builder().material("Material 1").source("trainingAudience").trainingContent(expectedTrainingContent).build();
        when(trainingMaterialRepository.save(any(TrainingMaterial.class))).thenReturn(expectedTrainingMaterial);

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.createSyllabus(request);

        // Verify the behavior and assertions
        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());

    }

    @Test
    public void testCreateSyllabus_ErrorOccurred() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mocking repo save behavior to return a specific object
        Syllabus expectedSyllabus = new Syllabus();
        expectedSyllabus.setTopicCode("ABC123");
        when(syllabusRepository.save(any(Syllabus.class))).thenThrow(new DataAccessException("Simulated database connection error") {
        });

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.createSyllabus(request);

        // Verify the behavior and assertions
        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());

    }

    // update syllabus with standard output, training program not found
    @Test
    public void testUpdateSyllabus() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);
        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mocking repo behavior to return a specific object
        syllabus.setTopicCode("ABC123");
        when(syllabusRepository.findById("ABC123")).thenReturn(Optional.of(syllabus));
        when(syllabusRepository.save(any(Syllabus.class))).thenReturn(syllabus);

        // Mocking other repository delete methods
        doNothing().when(trainingProgramSyllabusRepository).deleteByTopicCode(anyString());
        doNothing().when(syllabusObjectiveRepository).deleteByTopicCode(anyString());
        doNothing().when(trainingUnitRepository).deleteTrainingMaterialByTopicCode(anyString());
        doNothing().when(trainingUnitRepository).deleteTrainingContentByTopicCode(anyString());
        doNothing().when(trainingUnitRepository).deleteTrainingUnitByTopicCode(anyString());

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.updateSyllabus(request, "ABC123");

        // Verify the behavior and assertions
        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());

    }

    @Test
    public void testUpdateSyllabus_NotFound() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);
        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);


        // Arrange
        String topicCode = "ABC123";
        when(syllabusRepository.findById(topicCode)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ResponseMessage> response = syllabusService.updateSyllabus(request, topicCode);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Failure", Objects.requireNonNull(response.getBody()).getStatus());
    }

    @Test
    public void testUpdateSyllabus_ErrorOccurred() {

        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);
        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Arrange
        syllabus.setTopicCode("ABC123");
        when(syllabusRepository.findById("ABC123")).thenReturn(Optional.of(syllabus));
        when(syllabusRepository.save(any(Syllabus.class))).thenThrow(new DataAccessException("Simulated database connection error") {
        });

        // Act
        ResponseEntity<ResponseMessage> response = syllabusService.updateSyllabus(request, "ABC123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Failure", Objects.requireNonNull(response.getBody()).getStatus());
    }

    // get detail syllabus no list
    @Test
    public void testGetDetail() {
        // Mocking repo behavior to return a specific object
        syllabus.setTopicCode("ABC123");
        when(syllabusRepository.findById("ABC123")).thenReturn(Optional.of(syllabus));

        // Call the method under test
        ResponseEntity<ResponseObject> responseEntity = syllabusService.getDetailSyllabus("ABC123");

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
        assertNotNull(responseEntity.getBody().getPayload());
    }

    // duplicate (unknown problem)
    @Test
    public void testDuplicate() {
        // Mocking repo behavior to return a specific object
        syllabus.setTopicCode("ABC123");
        when(syllabusRepository.findById("ABC123")).thenReturn(Optional.of(syllabus));

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.duplicateSyllabus("ABC123");

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    public void testImportOverride() {
        // Prepare test data
        String filename = "Syllabus.csv";
        String choice = "Override";

        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mocking behavior for syllabusRepository
        when(syllabusRepository.findById(anyString())).thenReturn(Optional.empty()); // Mock no existing syllabus initially

        // Mocking behavior for trainingProgramRepository
        when(trainingProgramRepository.getTrainingProgramByName1(anyString())).thenReturn(Optional.empty()); // Mock no existing training program initially


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.importSyllabus(filename, choice);

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    public void testImportAllow() {
        // Prepare test data
        String filename = "Syllabus.csv";
        String choice = "Allow";

        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mocking behavior for syllabusRepository
        when(syllabusRepository.findById(anyString())).thenReturn(Optional.empty()); // Mock no existing syllabus initially

        // Mocking behavior for trainingProgramRepository
        when(trainingProgramRepository.getTrainingProgramByName1(anyString())).thenReturn(Optional.empty()); // Mock no existing training program initially


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.importSyllabus(filename, choice);

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    public void testImportSkip() {
        // Prepare test data
        String filename = "Syllabus.csv";
        String choice = "Skip";

        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mocking behavior for syllabusRepository
        when(syllabusRepository.findById(anyString())).thenReturn(Optional.empty()); // Mock no existing syllabus initially

        // Mocking behavior for trainingProgramRepository
        when(trainingProgramRepository.getTrainingProgramByName1(anyString())).thenReturn(Optional.empty()); // Mock no existing training program initially


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.importSyllabus(filename, choice);

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    public void testImportOverrideWithExistingSyllabus() {
        // Prepare test data
        String filename = "Syllabus.csv";
        String choice = "Override";

        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // Mocking behavior for syllabusRepository
        syllabus.setTopicCode("TOP001"); // Create a mock existing syllabus
        when(syllabusRepository.findById(anyString())).thenReturn(Optional.of(syllabus));

        // Mocking behavior for trainingProgramRepository
        when(trainingProgramRepository.getTrainingProgramByName1(anyString())).thenReturn(Optional.empty()); // Mock no existing training program initially


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = syllabusService.importSyllabus(filename, choice);

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }


}
