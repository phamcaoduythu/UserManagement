package com.example.usermanagement.controller;

import com.example.usermanagement.dto.Request.UpdatePermissionRequest;
import com.example.usermanagement.dto.Response.GetUserPermissionsResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.service.UserPermissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-permission")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
@SecurityRequirement(name = "FSA_Phase_1")
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<GetUserPermissionsResponse>> getAllPermission() {
        List<GetUserPermissionsResponse> list = userPermissionService.getUserPermission();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ResponseObject> updatePermission(@RequestBody List<UpdatePermissionRequest> updateRequest) {
        return userPermissionService.updatePermission(updateRequest);
    }

}
