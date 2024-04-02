package com.example.usermanagement.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserPermissionsResponse {

    private String roleName;
    private String syllabus;
    private String training;
    private String userclass;
    private String learningMaterial;
    private String userManagement;
}
