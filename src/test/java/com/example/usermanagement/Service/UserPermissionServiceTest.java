package com.example.usermanagement.Service;

import com.example.usermanagement.dto.Request.UpdatePermissionRequest;
import com.example.usermanagement.dto.Response.GetUserPermissionsResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.UserPermission;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.service_implements.UserPermissionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.usermanagement.enums.Permission.*;
import static com.example.usermanagement.enums.Role.SUPER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserPermissionServiceTest {
    @Mock
    private UserPermissionRepository userPermissionRepository;

    @InjectMocks
    private UserPermissionServiceImpl userPermissionService;

    // only permit to everything
    private UserPermission userPermission;

    // only permit to nothing
    private UserPermission userPermission1;

    // only permit to view
    private UserPermission userPermission2;

    // only permit to view, create
    private UserPermission userPermission3;


    @BeforeEach
    public void setUp() {
        userPermission = UserPermission.builder()
                .role(SUPER_ADMIN)
                .syllabus(List.of(SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                .trainingProgram(List.of(TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                .userClass(List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                .userManagement(List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
                .learningMaterial(List.of(MATERIAL_CREATE, MATERIAL_VIEW, MATERIAL_MODIFY, MATERIAL_DELETE, MATERIAL_IMPORT)).build();

        userPermission1 = UserPermission.builder()
                .role(SUPER_ADMIN)
                .syllabus(new ArrayList<>())
                .trainingProgram(new ArrayList<>())
                .userClass(new ArrayList<>())
                .userManagement(new ArrayList<>())
                .learningMaterial(new ArrayList<>()).build();

        userPermission2 = UserPermission.builder()
                .role(SUPER_ADMIN)
                .syllabus(List.of(SYLLABUS_VIEW))
                .trainingProgram(List.of(TRAINING_VIEW))
                .userClass(List.of(CLASS_VIEW))
                .userManagement(List.of(USER_VIEW))
                .learningMaterial(List.of(MATERIAL_VIEW)).build();

        userPermission3 = UserPermission.builder()
                .role(SUPER_ADMIN)
                .syllabus(List.of(SYLLABUS_CREATE, SYLLABUS_VIEW))
                .trainingProgram(List.of(TRAINING_CREATE, TRAINING_VIEW))
                .userClass(List.of(CLASS_CREATE, CLASS_VIEW))
                .userManagement(List.of(USER_CREATE, USER_VIEW))
                .learningMaterial(List.of(MATERIAL_CREATE, MATERIAL_VIEW)).build();
    }

    @Test
    void testGetUserPermission() {
        // Mocking
        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission);

        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);

        // Calling the method
        List<GetUserPermissionsResponse> responses = userPermissionService.getUserPermission();

        // Verifying
        assertEquals(1, responses.size());
    }

    @Test
    void testGetUserPermission1() {
        // Mocking
        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission1);

        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);

        // Calling the method
        List<GetUserPermissionsResponse> responses = userPermissionService.getUserPermission();

        // Verifying
        assertEquals(1, responses.size());
    }

    @Test
    void testGetUserPermission2() {
        // Mocking
        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission2);

        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);

        // Calling the method
        List<GetUserPermissionsResponse> responses = userPermissionService.getUserPermission();

        // Verifying
        assertEquals(1, responses.size());
    }

    @Test
    void testGetUserPermission3() {
        // Mocking
        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission3);

        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);

        // Calling the method
        List<GetUserPermissionsResponse> responses = userPermissionService.getUserPermission();

        // Verifying
        assertEquals(1, responses.size());
    }

    @Test
    void testUpdateUserPermission() {
        // Mocking
        UpdatePermissionRequest request = UpdatePermissionRequest.builder()
                .roleName("SUPER_ADMIN")
                .syllabus("FULL_ACCESS")
                .training("ACCESS_DENIED")
                .userclass("FULL_ACCESS")
                .learningMaterial("ACCESS_DENIED")
                .userManagement("FULL_ACCESS")
                .build();
        List<UpdatePermissionRequest> updateRequests = new ArrayList<>();
        updateRequests.add(request);

        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission);
        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);
        when(userPermissionRepository.saveAll(mockUserPermissions)).thenReturn(mockUserPermissions);


        // Calling the method
        ResponseEntity<ResponseObject> response = userPermissionService.updatePermission(updateRequests);

        // Verifying
        assertEquals("Success", Objects.requireNonNull(response.getBody()).getStatus());
    }

    @Test
    void testUpdateUserPermission1() {
        // Mocking
        UpdatePermissionRequest request = UpdatePermissionRequest.builder()
                .roleName("SUPER_ADMIN")
                .syllabus("FULL_ACCESS")
                .training("ACCESS_DENIED")
                .userclass("FULL_ACCESS")
                .learningMaterial("ACCESS_DENIED")
                .userManagement("FULL_ACCESS")
                .build();
        List<UpdatePermissionRequest> updateRequests = new ArrayList<>();
        updateRequests.add(request);

        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission1);
        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);
        when(userPermissionRepository.saveAll(mockUserPermissions)).thenReturn(mockUserPermissions);


        // Calling the method
        ResponseEntity<ResponseObject> response = userPermissionService.updatePermission(updateRequests);

        // Verifying
        assertEquals("Success", Objects.requireNonNull(response.getBody()).getStatus());
    }

    @Test
    void testUpdateUserPermission2() {
        // Mocking
        UpdatePermissionRequest request = UpdatePermissionRequest.builder()
                .roleName("SUPER_ADMIN")
                .syllabus("FULL_ACCESS")
                .training("ACCESS_DENIED")
                .userclass("FULL_ACCESS")
                .learningMaterial("ACCESS_DENIED")
                .userManagement("FULL_ACCESS")
                .build();
        List<UpdatePermissionRequest> updateRequests = new ArrayList<>();
        updateRequests.add(request);

        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission2);
        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);
        when(userPermissionRepository.saveAll(mockUserPermissions)).thenReturn(mockUserPermissions);


        // Calling the method
        ResponseEntity<ResponseObject> response = userPermissionService.updatePermission(updateRequests);

        // Verifying
        assertEquals("Success", Objects.requireNonNull(response.getBody()).getStatus());
    }

    @Test
    void testUpdateUserPermission3() {
        // Mocking
        UpdatePermissionRequest request = UpdatePermissionRequest.builder()
                .roleName("SUPER_ADMIN")
                .syllabus("FULL_ACCESS")
                .training("ACCESS_DENIED")
                .userclass("FULL_ACCESS")
                .learningMaterial("ACCESS_DENIED")
                .userManagement("FULL_ACCESS")
                .build();
        List<UpdatePermissionRequest> updateRequests = new ArrayList<>();
        updateRequests.add(request);

        List<UserPermission> mockUserPermissions = new ArrayList<>();
        mockUserPermissions.add(userPermission3);
        when(userPermissionRepository.findAll()).thenReturn(mockUserPermissions);
        when(userPermissionRepository.saveAll(mockUserPermissions)).thenReturn(mockUserPermissions);


        // Calling the method
        ResponseEntity<ResponseObject> response = userPermissionService.updatePermission(updateRequests);

        // Verifying
        assertEquals("Success", Objects.requireNonNull(response.getBody()).getStatus());
    }
}
