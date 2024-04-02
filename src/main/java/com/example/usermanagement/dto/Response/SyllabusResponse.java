package com.example.usermanagement.dto.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusResponse {
    String syllabusName = "";
    String syllabusCode = "";
    LocalDate createdOn = null;
    String createdBy = null;
    int duration = 0;
    String syllabusStatus = "";
    List<String> syllabusObjectiveList = new ArrayList<>();
}
