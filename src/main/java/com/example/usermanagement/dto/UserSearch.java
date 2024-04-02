package com.example.usermanagement.dto;

import java.time.LocalDate;

public interface UserSearch {
    int getUserId();

    String getName();

    String getEmail();

    String getPhone();

    LocalDate getDob();

    String getGender();

    String getRole();

    boolean isStatus();

}
