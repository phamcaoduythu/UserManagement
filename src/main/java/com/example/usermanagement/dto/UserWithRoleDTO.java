package com.example.usermanagement.dto;

import com.example.usermanagement.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithRoleDTO {
    private String name;
    private Role role;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private boolean status;
    private String createdBy;
    private LocalDate createdDate;
    private String modifiedBy;
    private LocalDate modifiedDate;
}
