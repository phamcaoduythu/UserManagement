package com.example.usermanagement.entity.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class TrainingContentLearningObjectiveCompositeKey implements Serializable {

    @Column(name = "content_code")
    private int trainingContentCode;

    @Column(name = "output_code", length = 10)
    private String learningObjectiveCode;

    @Column(name = "topic_code")
    private String topicCode;

}
