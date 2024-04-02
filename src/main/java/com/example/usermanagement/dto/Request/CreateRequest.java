package com.example.usermanagement.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRequest {
    private String name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String role;
    private boolean status;
}
