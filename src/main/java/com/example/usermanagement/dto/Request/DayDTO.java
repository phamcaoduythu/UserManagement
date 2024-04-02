package com.example.usermanagement.dto.Request;

import com.example.usermanagement.dto.UnitDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayDTO {

    int dayNumber;
    List<UnitDTO> unitList;

}
