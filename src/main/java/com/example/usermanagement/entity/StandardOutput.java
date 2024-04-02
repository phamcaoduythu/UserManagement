package com.example.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "StandardOutput")
public class StandardOutput {

    @OneToMany(mappedBy = "outputCode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private final Set<SyllabusObjective> syllabusObjectives = new HashSet<>();
    @OneToMany(mappedBy = "outputCode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private final Set<TrainingContent> trainingContents = new HashSet<>();

    @Id
    @Column(name = "output_code", length = 10)
    private String outputCode;

    @Column(name = "output_name", length = 300)
    private String outputName;

    @Column(name = "output_description", length = 15000)
    private String description;

}

