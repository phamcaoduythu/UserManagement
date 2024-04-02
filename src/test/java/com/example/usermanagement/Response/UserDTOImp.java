package com.example.usermanagement.Response;

import com.example.usermanagement.dto.UserDTO;
import com.example.usermanagement.entity.UserPermission;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTOImp implements UserDTO {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String createdBy;
    private String modifiedBy;
    private UserPermission role;
    private boolean status;


    private LocalDate createdDate;
    private LocalDate modifiedDate;

}

