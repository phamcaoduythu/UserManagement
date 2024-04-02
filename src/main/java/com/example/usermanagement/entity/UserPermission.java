package com.example.usermanagement.entity;

import com.example.usermanagement.enums.Permission;
import com.example.usermanagement.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_permission")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int permissionId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "syllabus", nullable = false, length = 1000)
    @Enumerated(EnumType.STRING)
    private List<Permission> syllabus;

    @Column(name = "training_program", nullable = false, length = 1000)
    @Enumerated(EnumType.STRING)
    private List<Permission> trainingProgram;

    @Column(name = "user_class", nullable = false, length = 1000)
    @Enumerated(EnumType.STRING)
    private List<Permission> userClass;

    @Column(name = "learning_material", nullable = false, length = 1000)
    @Enumerated(EnumType.STRING)
    private List<Permission> learningMaterial;

    @Column(name = "user_management", nullable = false, length = 1000)
    @Enumerated(EnumType.STRING)
    private List<Permission> userManagement;

    @OneToMany(mappedBy = "role")
    @JsonManagedReference
    private Set<User> users = new HashSet<>();

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        syllabus.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        trainingProgram.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        userClass.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        learningMaterial.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        userManagement.forEach(permission -> {
            authList.add(new SimpleGrantedAuthority(permission.getPermission()));
        });
        authList.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authList;
    }

}
