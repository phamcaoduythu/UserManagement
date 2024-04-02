package com.example.usermanagement.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarRequest {
    private String classId;
    private LocalDate enrollDate;
    private String timeFrom;
    private String timeTo;
    private String value;
}
