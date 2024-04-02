package com.example.usermanagement.entity.composite_key;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SyllabusTrainingUnitTrainingContentCompositeKey implements Serializable {

    @Embedded
    SyllabusTrainingUnitCompositeKey id;

    @Column(name = "content_code")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int contentCode;

}