package com.example.usermanagement.dto.Response;

import com.example.usermanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String status;
    private String message;
    private String token;
    private User userInfo;
}