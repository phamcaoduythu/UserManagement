package com.example.usermanagement.service;

import com.example.usermanagement.dto.Request.CreateRequest;
import com.example.usermanagement.dto.Request.LoginRequest;
import com.example.usermanagement.dto.Response.LoginResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AuthenticationService {

    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    ResponseEntity<ResponseObject> createUser(CreateRequest userRequest) throws RuntimeException;

    void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException;

    ResponseEntity<ResponseObject> getLoggedInUser(HttpServletRequest request);

}
