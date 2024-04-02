package com.example.usermanagement.entity;

import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitCompositeKey;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TrainingUnit")
public class TrainingUnit {

    @OneToMany(mappedBy = "unitCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<TrainingContent> trainingContents = new HashSet<>();

    @EmbeddedId
    SyllabusTrainingUnitCompositeKey id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_code", referencedColumnName = "topic_code")
    @MapsId("topicCode")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Syllabus syllabus;

    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

}

