package com.example.usermanagement.dto.Response;


import com.example.usermanagement.dto.UpdatedClassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * author: Le Ngoc Tam Nhu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassResponse {
    private int status;
    private String message = "";
    private UpdatedClassDTO updatedClass;

}

