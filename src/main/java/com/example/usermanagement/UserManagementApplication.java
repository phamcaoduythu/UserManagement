package com.example.usermanagement;


import com.example.usermanagement.entity.StandardOutput;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.UserPermission;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.StandardOutputRepository;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.repository.UserRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.usermanagement.enums.Permission.*;

@SpringBootApplication
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/")
@SecurityScheme(name = "FSA_Phase_1", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class UserManagementApplication {

    private final UserRepository userRepository;

    private final UserPermissionRepository userPermissionRepository;

    private final PasswordEncoder passwordEncoder;

    private final StandardOutputRepository standardOutputRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                if (standardOutputRepository.findAll().size() == 0) {
                    List<StandardOutput> standardOutputList = new ArrayList<>();
                    String[] objectiveCode = {"h4sd", "h6sd", "k6sd", "hk416", "mp5k", "mac10", "m4a1"};
                    for (int i = 0; i < objectiveCode.length; i++) {
                        StandardOutput standardOutput =
                                StandardOutput.builder()
                                        .outputCode(objectiveCode[i].toUpperCase())
                                        .outputName(objectiveCode[i])
                                        .description("description")
                                        .build();

                        standardOutputList.add(standardOutput);
                    }
                    standardOutputRepository.saveAll(standardOutputList);
                }

                if (userPermissionRepository.findAll().size() == 0) {
                    List<UserPermission> permissionList = new ArrayList<>();
                    permissionList.add(UserPermission.builder()
                            .role(Role.SUPER_ADMIN)
                            .syllabus(
                                    List.of(
                                            SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                            .trainingProgram(
                                    List.of(
                                            TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                            .userClass(
                                    List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                            .userManagement(
                                    List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
                            .learningMaterial(List.of())
                            .build());
                    permissionList.add(UserPermission.builder()
                            .role(Role.CLASS_ADMIN)
                            .syllabus(
                                    List.of(
                                            SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                            .trainingProgram(
                                    List.of(
                                            TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                            .userClass(
                                    List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                            .userManagement(
                                    List.of(USER_VIEW))
                            .learningMaterial(List.of())
                            .build());
                    permissionList.add(UserPermission.builder()
                            .role(Role.TRAINER)
                            .syllabus(List.of(SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                            .trainingProgram(List.of(TRAINING_VIEW))
                            .userClass(List.of(CLASS_VIEW))
                            .userManagement(List.of(USER_VIEW))
                            .learningMaterial(List.of())
                            .build());
                    permissionList.add(UserPermission.builder()
                            .role(Role.USER)
                            .syllabus(List.of(SYLLABUS_VIEW))
                            .trainingProgram(List.of())
                            .userClass(List.of())
                            .userManagement(List.of())
                            .learningMaterial(List.of())
                            .build());
                    userPermissionRepository.saveAll(permissionList);
                }

                if (userRepository.findAll().isEmpty()) {
                    List<User> userList = new ArrayList<>();
                    userList.add(User.builder()
                            .email("admin@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Admin")
                            .phone("0977545450")
                            .dob(LocalDate.now())
                            .gender("Male")
                            .role(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Admin")
                            .createdDate(LocalDate.now())
                            .modifiedBy("Admin")
                            .modifiedDate(LocalDate.now())
                            .build());
                    userList.add(User.builder()
                            .email("an@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Admin")
                            .phone("0977545450")
                            .dob(LocalDate.now())
                            .gender("Male")
                            .role(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Admin")
                            .createdDate(LocalDate.now())
                            .modifiedBy("Admin")
                            .modifiedDate(LocalDate.now())
                            .build());
                    userRepository.saveAll(userList);
                }
            }
        };
    }

}