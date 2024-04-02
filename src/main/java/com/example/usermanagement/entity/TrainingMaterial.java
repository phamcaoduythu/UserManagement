package com.example.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_material")
public class TrainingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private int id;

    @Column(name = "material", nullable = false)
    private String material;

    @Column(name = "source", nullable = false, length = 10000)
    private String source;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "unit_code", referencedColumnName = "unit_code"),
            @JoinColumn(name = "content_code", referencedColumnName = "content_code"),
            @JoinColumn(name = "topic_code", referencedColumnName = "topic_code")
    })
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private TrainingContent trainingContent;
}

