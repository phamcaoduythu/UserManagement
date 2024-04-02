package com.example.usermanagement.entity.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyllabusTrainingUnitCompositeKey implements Serializable {

    @Column(name = "unit_code")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int uCode;

    @Column(name = "topic_code")
    private String tCode;

}