package com.example.usermanagement.dto.Response;

import com.example.usermanagement.entity.TrainingProgramSyllabus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClass3Response {
    private String status;
    private TrainingProgramSyllabus updatedClass3;
}
