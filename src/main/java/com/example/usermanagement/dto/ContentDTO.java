package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {
    String contentName;
    String deliveryType;
    String standardOutput;
    String note;
    int duration;
    Boolean online;
    List<MaterialDTO> trainingMaterial;
}
