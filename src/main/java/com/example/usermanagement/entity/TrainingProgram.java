package com.example.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "TrainingPrograms")
public class TrainingProgram {

    @OneToMany(mappedBy = "trainingProgramCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<TrainingProgramSyllabus> trainingProgramSyllabus = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_program_code")
    private int trainingProgramCode;

    @Column(nullable = false, name = "name")
    private String name;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "userId", referencedColumnName = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User userID;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "duration")
    private int duration;

    @Column(nullable = false, name = "status")
    private String status;

    @Column(nullable = false, name = "created_by")
    private String createdBy;

    @Column(nullable = false, name = "created_date")
    private LocalDate createdDate;

    @Column(name = "modified_by")
    private String modifiedBy = "";

    @Column(name = "modified_date")
    private LocalDate modifiedDate = null;

}

