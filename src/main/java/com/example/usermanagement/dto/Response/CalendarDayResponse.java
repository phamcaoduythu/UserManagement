package com.example.usermanagement.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDayResponse {
    public LocalDate enrollDate;
    private String classCode;
    private Time timeFrom;
    private Time timeTo;
    private String userType;

}
