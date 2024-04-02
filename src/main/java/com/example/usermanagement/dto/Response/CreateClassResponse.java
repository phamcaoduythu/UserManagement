package com.example.usermanagement.dto.Response;

import com.example.usermanagement.dto.Request.CreateClassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassResponse {
    public String message;
    public String status;
    CreateClassDTO createClassDTO;
}
