package com.example.usermanagement.Service;

import com.example.usermanagement.dto.Request.UpdatePasswordRequest;
import com.example.usermanagement.dto.Request.UpdateRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.UserPermission;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.service_implements.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;

import static com.example.usermanagement.enums.Permission.*;
import static com.example.usermanagement.enums.Role.SUPER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    JWTService jwtService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    UserPermissionRepository userPermissionRepository;
    private User user;
    private UserPermission userPermission;


    //    Set up User builder and UserPermission builder
    @BeforeEach

    public void setUp() {
        user = User.builder().email("admin@gmail.com").password(passwordEncoder.encode("1")).name("Admin").phone("0977545450").dob(LocalDate.now()).gender("Male").role(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN).orElse(null)).status(true).createdBy("Admin").createdDate(LocalDate.now()).modifiedBy("Admin").modifiedDate(LocalDate.now()).build();

        userPermission = UserPermission.builder().role(SUPER_ADMIN).syllabus(List.of(SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT)).trainingProgram(List.of(TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT)).userClass(List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT)).userManagement(List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT)).learningMaterial(List.of()).build();
    }


    //    These Tests are for GetUserList func()
    @Test
    void test_returnSuccessResponse_GetUserList() {

        // Mock behavior of userRepository
        when(userRepository.getAllUsersWithRole()).thenReturn(Collections.emptyList());

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserList();


        // Assert
        assertEquals("Success", response.getBody().getStatus());
        assertEquals("list of User is loaded successfully!", response.getBody().getMessage());
    }

    @Test
    void test_returnFailureResponse_GetUserList() {
        // Mock behavior of userRepository
        when(userRepository.getAllUsersWithRole()).thenThrow(new RuntimeException("Database connection error"));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserList();


        // Assert
        assertEquals("Failure", response.getBody().getStatus());
        assertEquals("Failed to create list of User", response.getBody().getMessage());
    }

    //        These Tests are for GetUserByEmail func()
    @Test
    void test_returnSuccessResponse_getUserByEmail() {
        // Mock behavior of userRepository
        String email = "admin@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByEmail(email);

        // Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_UserNotFound_getUserByEmail() {
        // Mock behavior of userRepository
        String email = "admin@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(null));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByEmail(email);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_getUserByEmail() {
        // Mock behavior of userRepository
        String email = "admin@gmail.com";
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Database connection error"));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByEmail(email);


        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for GetUSerByName func()
    @Test
    void test_returnSuccessResponse_getUserByName() {
        // Mock behavior of userRepository
        String name = "Admin";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findByName(name)).thenReturn(userList);

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByName(name);

        // Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_UserNotFound_getUserByName() {
        // Mock behavior of userRepository
        String name = "Admin";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findByName(name)).thenReturn(null);

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByName(name);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_getUserByName() {
        // Mock behavior of userRepository
        String name = "Admin";
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findByName(name)).thenThrow(new RuntimeException("Database connection error"));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByName(name);


        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for GetUSerByID func()
    @Test
    void test_returnSuccessResponse_getUserByID() {
        //Mock behavior of userRepository
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByID(userId);

        // Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_UserNotFound_getUserByID() {
        // Mock behavior of userRepository
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByID(userId);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_getUserByID() {
        // Mock behavior of userRepository
        int userId = 1;
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database connection error"));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByID(userId);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for GetUSerByRole func() --SUPER ADMIN Role
    @Test
    void test_returnSuccessResponse_getUserByRole() {
        // Mock behavior of userPermissionRepository
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String role = "SUPER_ADMIN";
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.of(userPermission));
        when(userPermissionRepository.findById(1)).thenReturn(Optional.of(userPermission));
        when(userRepository.findUsersByRoleAndStatus(userPermission, true)).thenReturn(userList);

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByRole(role);

        // Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_UserNotFound_getUserByRole() {
        // Mock behavior of userPermissionRepository
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String role = "SUPER_ADMIN";
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.of(userPermission));
        when(userPermissionRepository.findById(1)).thenReturn(Optional.of(userPermission));
        when(userRepository.findUsersByRoleAndStatus(userPermission, true)).thenReturn(Collections.emptyList());

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByRole(role);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_NullPermission_getUserByRole() {
        // Mock behavior of userPermissionRepository
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String role = "SUPER_ADMIN";
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByRole(role);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_getUserByRole() {
        // Mock behavior of userPermissionRepository
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String role = "SUPER_ADMIN";
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenThrow(new RuntimeException("Connect database failed.."));

        // Call the method
        ResponseEntity<ResponseObject> response = userService.getUserByRole(role);

        // Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for UpdateUser func()
    @Test
    void test_returnSuccessResponse_UpdateUser() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        UpdateRequest updateRequest = UpdateRequest.builder().name("John Mama").phone("1234567890").dob(LocalDate.now()).gender("Male").build();

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.updateUser(updateRequest);

        //Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_NullRequest_UpdateUser() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        UpdateRequest updateRequest = UpdateRequest.builder().name("John Mama").phone("1234567890").dob(LocalDate.now()).gender("Male").build();

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.updateUser(updateRequest);

        //Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_UpdateUser() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        UpdateRequest updateRequest = UpdateRequest.builder().name("John Mama").phone("1234567890").dob(LocalDate.now()).gender("Male").build();

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenThrow(new RuntimeException("Connect database failed.."));

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.updateUser(updateRequest);

        //Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for UpdatePassword func()
    @Test
    void test_returnSuccessResponse_UpdatePassword() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .newPassword("123")
                .build();

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);
        // Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        // Mocking password encoding
        when(passwordEncoder.encode(updatePasswordRequest.getNewPassword())).thenReturn(String.valueOf(updatePasswordRequest));
        // Mocking userRepository behavior
        when(userRepository.save(user)).thenReturn(user);

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.updatePassword(updatePasswordRequest);

        //Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_UpdatePassword() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";

        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .newPassword("123")
                .build();

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);
        /// Mocking userRepository behavior
        when(userRepository.findByEmail(userEmail)).thenThrow(new RuntimeException("Connect database failed.."));

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.updatePassword(updatePasswordRequest);

        //Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for ChangeStatus func()
    @Test
    void test_returnSuccessResponse_activateUser_ChangeStatus() {
        // Mock behavior of userRepository
        int id = 1;
        String status = "active";
        when(userRepository.findUserToChangeStatus(id)).thenReturn(Optional.ofNullable(user));

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.changeStatus(id, status);

        //Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnSuccessResponse_deactivateUser_ChangeUser() {
        // Mock behavior of userRepository
        int id = 1;
        String status = "inactive";
        when(userRepository.findUserToChangeStatus(id)).thenReturn(Optional.ofNullable(user));

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.changeStatus(id, status);

        //Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    //    These Tests are for UpdateRoleByID func()
    @Test
    void test_returnSuccessResponse_UpdateRole() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        int id = 1;
        String role = "SUPER_ADMIN";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user));
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.ofNullable(userPermission));


        //Call the method
        ResponseEntity<ResponseMessage> response = userService.changeRole(id, role);

        //Assert
        assertEquals("Success", response.getBody().getStatus());
    }

    @Test
    void test_returnFailureResponse_UserNotFound_UpdateRole() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        int id = 1;
        String role = "SUPER_ADMIN";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.ofNullable(userPermission));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.changeRole(id, role);

        //Assert
        assertEquals("Failure", response.getBody().getStatus());
        assertEquals("User not exist", response.getBody().getMessage());
    }

    @Test
    void test_returnFailureResponse_RoleNotFound_UpdateRole() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        int id = 1;
        String role = "SUPER_ADMIN";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking userRepository behavior
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.changeRole(id, role);

        //Assert
        assertEquals("Failure", response.getBody().getStatus());
        assertEquals("Role not exist", response.getBody().getMessage());
    }

    @Test
    void test_returnFailureResponse_ExceptionThrow_UpdateRole() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        int id = 1;
        String role = "SUPER_ADMIN";

        // Mocking userRepository behavior
        when(userRepository.findById(1)).thenThrow(new RuntimeException("Connect database failed.."));

        //Call the method
        ResponseEntity<ResponseMessage> response = userService.changeRole(id, role);

        //Assert
        assertEquals("Failure", response.getBody().getStatus());
    }

    //    These Tests are for ImportUserByFile.csv func()
    @Test
    void test_ImportUser_Override_NotFound() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "User.csv";
        String choice = "Override";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.getUserNameByEmail(userEmail)).thenReturn(user.getName());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.ofNullable(userPermission));
        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = userService.importUsers(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_returnSuccessResponse_ImportUser_Override() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "User.csv";
        String choice = "Override";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.getUserNameByEmail(userEmail)).thenReturn(user.getName());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.ofNullable(userPermission));
        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = userService.importUsers(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }

    @Test
    void test_ImportUser_Skip_NotFound() {
        // Mocking necessary objects
        String userEmail = "admin@gmail.com";
        String token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk";
        String filename = "User.csv";
        String choice = "Skip";

        // Mocking HttpServletRequest
        MockHttpServletRequest requestObj = new MockHttpServletRequest();
        requestObj.addHeader("Authorization", token);
        ServletRequestAttributes attributes = new ServletRequestAttributes(requestObj);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mocking JWT token extraction
        when(jwtService.extractUserEmail("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3MTE0NDY1OTcsImV4cCI6MTcxMTUzMjk5N30.9wfCCmLQVEB4iTJV7iaZhCqtXXZemRQ9A7M2myOCXngjQgDdM6E3tfSaykBOJ2yk")).thenReturn(userEmail);

        // Mocking repo behavior
        when(userRepository.getUserNameByEmail(userEmail)).thenReturn(user.getName());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userPermissionRepository.findUserPermissionByRole(SUPER_ADMIN)).thenReturn(Optional.ofNullable(userPermission));
        // Call the method under test
        ResponseEntity<ResponseMessage> responseEntity = userService.importUsers(filename, choice);

        assertNotNull(responseEntity);
        assertEquals("Success", Objects.requireNonNull(responseEntity.getBody()).getStatus());
    }



}
