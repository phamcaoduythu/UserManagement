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
public class ClassUserCompositeKey implements Serializable {

    @Column(name = "user_id")
    int userId;

    @Column(name = "class_id")
    String classId;

}

