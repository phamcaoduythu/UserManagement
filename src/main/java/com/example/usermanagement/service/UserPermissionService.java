package com.example.usermanagement.service;

import com.example.usermanagement.dto.Request.UpdatePermissionRequest;
import com.example.usermanagement.dto.Response.GetUserPermissionsResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserPermissionService {

    List<GetUserPermissionsResponse> getUserPermission();

    ResponseEntity<ResponseObject> updatePermission(List<UpdatePermissionRequest> updateRequest);

}