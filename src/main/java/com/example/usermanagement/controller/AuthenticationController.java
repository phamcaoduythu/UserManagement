package com.example.usermanagement.controller;

import com.example.usermanagement.dto.Request.CreateRequest;
import com.example.usermanagement.dto.Request.LoginRequest;
import com.example.usermanagement.dto.Request.UpdatePasswordRequest;
import com.example.usermanagement.dto.Request.UpdateRequest;
import com.example.usermanagement.dto.Response.LoginResponse;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.service.AuthenticationService;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/authorize")
public class AuthenticationController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginRequest(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PutMapping("/update")
    @SecurityRequirement(name = "FSA_Phase_1")
    @PreAuthorize("hasAnyRole('USER', 'TRAINER', 'CLASS_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ResponseMessage> updateUserRequest(@RequestBody UpdateRequest updateRequest) {
        return userService.updateUser(updateRequest);
    }

    @PutMapping("/update-password")
    @SecurityRequirement(name = "FSA_Phase_1")
    @PreAuthorize("hasAnyRole('USER', 'TRAINER', 'CLASS_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ResponseMessage> updatePassword(@RequestBody UpdatePasswordRequest updateRequest) {
        return userService.updatePassword(updateRequest);
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasAuthority('user:create')")
    @SecurityRequirement(name = "FSA_Phase_1")
    public ResponseEntity<ResponseObject> createUserRequest(@RequestBody CreateRequest createRequest) {
        return authenticationService.createUser(createRequest);
    }

    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refresh(request, response);
    }

    @GetMapping("/login/get-user")
    @SecurityRequirement(name = "FSA_Phase_1")
    public ResponseEntity<ResponseObject> getLoggedInUser(HttpServletRequest request) {
        return authenticationService.getLoggedInUser(request);
    }

}
