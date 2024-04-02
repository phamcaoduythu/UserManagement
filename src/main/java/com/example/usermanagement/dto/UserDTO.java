package com.example.usermanagement.dto;

import com.example.usermanagement.entity.UserPermission;

import java.time.LocalDate;

public interface UserDTO {

    String getName();

    String getEmail();

    String getPhone();

    LocalDate getDob();

    String getGender();

    UserPermission getRole();

    boolean isStatus();

    String getCreatedBy();

    LocalDate getCreatedDate();

    String getModifiedBy();

    LocalDate getModifiedDate();

}
