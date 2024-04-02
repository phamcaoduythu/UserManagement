package com.example.usermanagement.dto.Response;

import com.example.usermanagement.enums.Role;

import java.time.LocalDate;


public interface ListUserResponse {
    int getUserId();

    String getName();

    String getEmail();

    String getPhone();

    LocalDate getDob();

    String getGender();

    Role getRole();

    boolean isStatus();

    String getCreatedBy();

    String getModifiedBy();
}
