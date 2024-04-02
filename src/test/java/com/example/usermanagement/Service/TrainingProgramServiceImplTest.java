package com.example.usermanagement.Service;

import com.example.usermanagement.Response.UserDTOImp;
import com.example.usermanagement.dto.Request.CreateTrainingProgramRequest;
import com.example.usermanagement.dto.Request.UpdateTrainingProgramRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.TrainingProgram;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.TrainingProgramRepository;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service_implements.TrainingProgramServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class TrainingProgramServiceImplTest {
    @InjectMocks
    private TrainingProgramServiceImpl trainingProgramService;
    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private UserPermissionRepository userPermissionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private User user;


    private TrainingProgram trainingProgram;

    @BeforeEach
    public void setUp() {
        trainingProgram = TrainingProgram.builder().trainingProgramCode(1).name("Mock Training Program").userID(User.builder().userId(1).build()).startDate(LocalDate.now()).duration(10).status("active").createdBy("Admin").createdDate(LocalDate.now()).build();

        user = User.builder().email("admin@gmail.com").password(passwordEncoder.encode("1")).name("Admin").phone("0977545450").dob(LocalDate.now()).gender("Male").role(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null)).status(true).createdBy("Admin").createdDate(LocalDate.now()).modifiedBy("Admin").modifiedDate(LocalDate.now()).build();
    }

    @Test
    void test_CreateTrainingProgram_Success() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);

        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization").substring(7);
        int userId = 1;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        // Mocking data

        CreateTrainingProgramRequest request = CreateTrainingProgramRequest.builder().name("Training Program 1").userEmail("admin@gmail.com").duration(30).startDate(LocalDate.now().plusDays(5)).build();
        User user = User.builder().name(request.getName()).email(request.getUserEmail()).role(mockUser.getRole()).build();
        String headerMail = "admin@gmail.com";
        when(jwtService.extractUserEmail(token)).thenReturn(headerMail);
        when(userRepository.findByEmail(request.getUserEmail())).thenReturn(Optional.of(user));
        ResponseEntity<ResponseObject> responseEntity = trainingProgramService.createTrainingProgram(request);

        log.info("Create Training Program Successfully");
        // Verifying the response
        Assertions.assertThat(Objects.requireNonNull(responseEntity.getBody()).getMessage()).isEqualTo("New training program has been created successfully!");

    }

    @Test
    void test_CreateTrainingProgram_Exist() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization").substring(7);
        int userId = 1;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        // Mocking data
        CreateTrainingProgramRequest request = CreateTrainingProgramRequest.builder().name("Training Program 1").userEmail("admin@gmail.com").duration(30).startDate(LocalDate.now().plusDays(5)).build();
        String headerMail = "admin@gmail.com";
        when(jwtService.extractUserEmail(token)).thenReturn(headerMail);
        when(userRepository.findByEmail(request.getUserEmail())).thenReturn(Optional.empty());
        ResponseEntity<ResponseObject> responseEntity = trainingProgramService.createTrainingProgram(request);
        // Verifying the response
        Assertions.assertThat(Objects.requireNonNull(responseEntity.getBody()).getMessage()).isEqualTo("This email does not exist!");

    }

    @Test
    void test_UpdateTrainingProgramInvalidId() {
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

        //Mock update training program request
        UpdateTrainingProgramRequest request = new UpdateTrainingProgramRequest();
        request.setDuration(1);
        request.setTopicCode(new String[]{"topic1"});

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.updateTrainingProgram(request, -1);

        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_UpdateTrainingProgramInvalidDuration() {
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

        //Mock update training program request
        UpdateTrainingProgramRequest request = new UpdateTrainingProgramRequest();
        request.setDuration(-1);
        request.setTopicCode(new String[]{"topic1"});

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.updateTrainingProgram(request, 1);

        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_UpdateTrainingProgramNotFound() {
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

        //Mock update training program request
        UpdateTrainingProgramRequest request = new UpdateTrainingProgramRequest();
        request.setDuration(1);
        request.setTopicCode(new String[]{"topic1"});

        //Mock repo behavior
        when(trainingProgramRepository.getTrainingProgramByID(1)).thenReturn(null);

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.updateTrainingProgram(request, 1);

        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_UpdateTrainingProgramFound() {
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

        //Mock update training program request
        UpdateTrainingProgramRequest request = new UpdateTrainingProgramRequest();
        request.setDuration(1);
        request.setTopicCode(new String[]{"topic1"});

        //Mock repo behavior
        when(trainingProgramRepository.getTrainingProgramByID(1)).thenReturn(trainingProgram);

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.updateTrainingProgram(request, 1);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_UpdateTrainingProgramWrongEmail() {
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
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        //Mock update training program request
        UpdateTrainingProgramRequest request = new UpdateTrainingProgramRequest();
        request.setDuration(1);
        request.setTopicCode(new String[]{"topic1"});

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.updateTrainingProgram(request, 1);

        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_UpdateTrainingProgramDatabaseError() {
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
        when(userRepository.findByEmail(userEmail)).thenThrow(new RuntimeException("Database connection error"));

        //Mock update training program request
        UpdateTrainingProgramRequest request = new UpdateTrainingProgramRequest();
        request.setDuration(1);
        request.setTopicCode(new String[]{"topic1"});

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.updateTrainingProgram(request, 1);

        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_DuplicateTrainingProgramWrongEmail() {
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
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.duplicateTrainingProgram("Mock Training Program");

        assertNotNull(responseEntity);
        assertEquals("Failure", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_DuplicateTrainingProgram() {
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

        // Mocking repo behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));

        String[] listNames = new String[]{"Mock Training Program"};
        when(trainingProgramRepository.findByName2("Mock Training Program")).thenReturn(List.of(listNames));

        when(trainingProgramRepository.findByName("Mock Training Program")).thenReturn(Optional.ofNullable(trainingProgram));

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.duplicateTrainingProgram("Mock Training Program");

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ImportTrainingProgramOverride_NotFound() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "training.csv";
        String choice = "Override";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.importTrainingProgram(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ImportTrainingProgramOverride_Found() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "training.csv";
        String choice = "Override";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.ofNullable(trainingProgram));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.importTrainingProgram(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ImportTrainingProgramAllow_NotFound() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "training.csv";
        String choice = "Allow";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.importTrainingProgram(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ImportTrainingProgramAllow_Found() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "training.csv";
        String choice = "Allow";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.ofNullable(trainingProgram));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        String[] listNames = new String[]{"Mock Training Program"};
        when(trainingProgramRepository.findByName2("Mock Training Program")).thenReturn(List.of(listNames));

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.importTrainingProgram(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ImportTrainingProgramSkip_NotFound() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "training.csv";
        String choice = "Skip";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(trainingProgramRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.importTrainingProgram(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ChangeTrainingProgramStatus_Active() {
        // Mocking necessary objects
        String status = "Activate";

        // Mocking repo behavior
        trainingProgram.setStatus("inactive");
        when(trainingProgramRepository.findById(1)).thenReturn(Optional.ofNullable(trainingProgram));


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.changeTrainingProgramStatus(1, status);

        assertNotNull(responseEntity);
        assertEquals("Activate training program successfully", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ChangeTrainingProgramStatus_Deactivate() {
        // Mocking necessary objects
        String status = "De-activate";

        // Mocking repo behavior
        trainingProgram.setStatus("active");
        when(trainingProgramRepository.findById(1)).thenReturn(Optional.ofNullable(trainingProgram));


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.changeTrainingProgramStatus(1, status);

        assertNotNull(responseEntity);
        assertEquals("De-activate training program successfully", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ChangeTrainingProgramStatus_Error() {
        // Mocking necessary objects
        String status = "De-activate";

        // Mocking repo behavior
        trainingProgram.setStatus("inactive");
        when(trainingProgramRepository.findById(1)).thenReturn(Optional.ofNullable(trainingProgram));


        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = trainingProgramService.changeTrainingProgramStatus(1, status);

        assertNotNull(responseEntity);
        assertEquals("De-activate training program failed", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_GetListTrainingProgram_Empty() {
        // Mocking repo behavior
        when(trainingProgramRepository.getAllTrainingProgram()).thenReturn(Collections.emptyList());


        // Call the method under test
        ResponseEntity<ResponseObject> responseEntity = trainingProgramService.getListTrainingProgram();

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_GetTrainingProgramByName_Empty() {
        // Mocking necessary objects
        String name = "name";

        // Mocking repo behavior
        when(trainingProgramRepository.findByNameV2(anyString())).thenReturn(Collections.emptyList());


        // Call the method under test
        ResponseEntity<ResponseObject> responseEntity = trainingProgramService.getTrainingProgramByName(name);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }


}
