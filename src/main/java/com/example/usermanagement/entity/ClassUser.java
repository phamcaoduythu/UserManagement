package com.example.usermanagement.entity;


import com.example.usermanagement.entity.composite_key.ClassUserCompositeKey;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "ClassUser")
public class ClassUser {

    @EmbeddedId
    ClassUserCompositeKey id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Class classId;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "users_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private User userID;

    @Column(nullable = false)
    private String userType;

}
