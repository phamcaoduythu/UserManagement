package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusDTO {
    private final int numberOfDay = 1;
    private String topicCode;
    private String topicName;
    private String version;

    private String publishStatus;

    private String createdBy;

    private String createdDate;
}
