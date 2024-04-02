package com.example.usermanagement.service;

import com.example.usermanagement.dto.Request.UpdatePasswordRequest;
import com.example.usermanagement.dto.Request.UpdateRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import org.springframework.http.ResponseEntity;


public interface UserService {

    ResponseEntity<ResponseObject> getUserList();

    ResponseEntity<ResponseObject> getUserByID(int UserID);

    ResponseEntity<ResponseObject> getUserByEmail(String email);

    ResponseEntity<ResponseObject> getUserByName(String name);

    ResponseEntity<ResponseMessage> updateUser(UpdateRequest User);

    ResponseEntity<ResponseMessage> updatePassword(UpdatePasswordRequest updateRequest);

    ResponseEntity<ResponseMessage> changeRole(int id, String role);

    ResponseEntity<ResponseMessage> changeStatus(int id, String type);

    ResponseEntity<ResponseObject> getUserByRole(String role);

    ResponseEntity<ResponseMessage> importUsers(String filename, String choice);

}
