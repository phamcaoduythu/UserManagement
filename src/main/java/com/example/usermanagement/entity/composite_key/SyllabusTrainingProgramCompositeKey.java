package com.example.usermanagement.entity.composite_key;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyllabusTrainingProgramCompositeKey implements Serializable {

    @Column(name = "topic_code")
    private String topicCode;

    @Column(name = "training_program_code")
    private int trainingProgramCode;

}