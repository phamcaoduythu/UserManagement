package com.example.usermanagement.entity.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClassSyllabusCompositeKey {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "topic_code")
    private String topicCode;

    @Column(name = "class_code")
    private String classCode;

}
