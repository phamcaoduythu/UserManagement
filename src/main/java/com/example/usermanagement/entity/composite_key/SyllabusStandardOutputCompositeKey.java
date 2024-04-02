package com.example.usermanagement.entity.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SyllabusStandardOutputCompositeKey implements Serializable {

    @Column(name = "topic_code")
    private String topicCode;

    @Column(name = "output_code")
    private String outputCode;

}
