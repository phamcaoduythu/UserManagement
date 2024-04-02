package com.example.usermanagement.service_implements;

import com.example.usermanagement.dto.Request.UpdatePasswordRequest;
import com.example.usermanagement.dto.Request.UpdateRequest;
import com.example.usermanagement.dto.Response.ListUserResponse;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static com.example.usermanagement.utils.StringHandler.randomStringGenerator;

@Slf4j
@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private List<ListUserResponse> userList;


    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> getUserList() {
        try {
            userList = userRepository.getAllUsersWithRole();
            String msg = "list of User is loaded successfully!";
            log.info(msg);
            return ResponseEntity.ok(new ResponseObject("Success", msg, userList));
        } catch (Exception e) {
            log.error("An error occurred while trying to create list of User. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to create list of User", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> getUserByEmail(String email) {
        try {
            String msg;
            var existedUser = userRepository.findByEmail(email).orElse(null);
            if (existedUser != null) {
                msg = "Get User with email " + email + " successfully";
                log.info(msg);
                return ResponseEntity.ok(new ResponseObject("Success", msg, existedUser));
            } else {
                msg = "User with email: " + email + " is not found";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to get User by email. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to get User by email", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    public ResponseEntity<ResponseObject> getUserByName(String name) {
        try {
            String msg;
            List<User> users = userRepository.findByName(name);
            if (users != null) {
                msg = "Get User with name " + name + " successfully";
                log.info(msg);
                return ResponseEntity.ok(new ResponseObject("Success", msg, users));
            } else {
                msg = "User with email: " + name + " is not found";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to get User by name. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to get User by name", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> getUserByID(int id) {
        try {
            String msg;
            var existedUser = userRepository.findById(id).orElse(null);
            if (existedUser != null) {
                msg = "Get User with Id " + id + " successfully";
                log.info(msg);
                return ResponseEntity.ok(new ResponseObject("Success", msg, existedUser));
            } else {
                msg = "User with Id: " + id + " is not found";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to get User by Id. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to get User by Id", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> getUserByRole(String role) {
        try {
            String msg;
            var permission = userPermissionRepository.findUserPermissionByRole(Role.valueOf(role)).orElse(null);
            if (permission != null) {
                List<User> list = null;
                switch (role) {
                    case "SUPER_ADMIN":
                        list = userRepository.findUsersByRoleAndStatus(userPermissionRepository.findById(1).orElse(null), true);
                        break;
                    case "CLASS_ADMIN":
                        list = userRepository.findUsersByRoleAndStatus(userPermissionRepository.findById(2).orElse(null), true);
                        break;
                    case "TRAINER":
                        list = userRepository.findUsersByRoleAndStatus(userPermissionRepository.findById(3).orElse(null), true);
                        break;
                    case "USER":
                        list = userRepository.findUsersByRoleAndStatus(userPermissionRepository.findById(4).orElse(null), true);
                        break;
                }
                if (!list.isEmpty()) {
                    msg = "List of User is loaded successfully!";
                    log.info(msg);
                    return ResponseEntity.ok(new ResponseObject("Success", msg, list));
                } else {
                    msg = "User with role " + role + " is not found.";
                    log.error(msg);
                    return ResponseEntity.ok(new ResponseObject("Failure", msg, list));
                }
            } else {
                msg = "Role " + role + " does not exist";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to get User bu role. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to get User bu role", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> updateUser(UpdateRequest updateRequest) {
        try {
            String msg;
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String headerEmail = jwtService.extractUserEmail(token);
            var requestUser = userRepository.findByEmail(headerEmail).orElse(null);
            if (requestUser != null) {
                requestUser.setName(updateRequest.getName());
                requestUser.setPhone(updateRequest.getPhone());
                requestUser.setDob(updateRequest.getDob());
                requestUser.setGender(updateRequest.getGender());
                requestUser.setModifiedBy(requestUser.getName());
                requestUser.setModifiedDate(LocalDate.now());
                // Save the updated user
                User updatedUser = userRepository.save(requestUser);
                msg = "User profile of" + updatedUser.getName() + " has been updated successfully!";
                log.info(msg);
                return ResponseEntity.ok(new ResponseMessage("Success", msg));
            } else {
                msg = "User with email " + headerEmail + " does not exist.";
                log.error(msg);
                return ResponseEntity.ok(new ResponseMessage("Failure", msg));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to update User profile. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to update User profile"));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> changeStatus(int id, String type) {
        try {
            var existedUser = userRepository.findUserToChangeStatus(id).orElse(null);
            if (existedUser != null) {
                switch (type) {
                    case "active":
                        return ResponseEntity
                                .ok()
                                .body(activateUser(id));

                    case "inactive":
                        return ResponseEntity
                                .ok()
                                .body(inactivateUser(id));
                }
            }
            String msg = "Failed to " + type.toUpperCase().charAt(0) + type.substring(1) + " User with Id " + id;
            log.error(msg);
            return ResponseEntity.ok(new ResponseMessage("Failure", msg));
        } catch (Exception e) {
            log.error("An error occurred while trying to change User status. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to change User status"));
        }

    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    private ResponseMessage activateUser(int id) {
        userRepository.changeStatus(id, true);
        String msg = "Activate User with Id " + id + " successfully!";
        log.info(msg);
        return new ResponseMessage("Success", msg);
    }

    private ResponseMessage inactivateUser(int id) {
        userRepository.changeStatus(id, false);
        String msg = "Inactivate User with id " + id + " successfully!";
        log.info(msg);
        return new ResponseMessage("Success", msg);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:40 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> updatePassword(UpdatePasswordRequest updateRequest) {
        try {
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String headerEmail = jwtService.extractUserEmail(token);
            var requestUser = userRepository.findByEmail(headerEmail).orElse(null);
            requestUser.setModifiedBy(requestUser.getName());
            requestUser.setModifiedDate(LocalDate.now());
            requestUser.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
            userRepository.save(requestUser);
            log.info("User " + requestUser.getName() + " has updated password successfully!");
            return ResponseEntity.ok(new ResponseMessage("Success", "Update password successfully"));
        } catch (Exception e) {
            log.error("An error occurred while trying to update password. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to update User password"));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:41 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> changeRole(int id, String role) {
        try {
            var existedUser = userRepository.findById(id).orElse(null);
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String headerEmail = jwtService.extractUserEmail(token);
            var permission = userPermissionRepository.findUserPermissionByRole(Role.valueOf(role)).orElse(null);
            if (permission != null) {
                if (existedUser != null) {
                    existedUser.setRole(permission);
                    userRepository.save(existedUser);
                    log.info("User with role of " + headerEmail + "has been successfully!");
                    return ResponseEntity.ok(new ResponseMessage("Success", "Role updated"));
                } else {
                    log.info("User with Id " + id + "does not exist");
                    return ResponseEntity.ok(new ResponseMessage("Failure", "User not exist"));
                }
            } else {
                log.info("User with Role " + role + "does not exist");
                return ResponseEntity.ok(new ResponseMessage("Failure", "Role not exist"));
            }
        } catch (Exception e) {
            log.error("Failed to change  role. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to change  role."));
        }
    }


    @Override
    public ResponseEntity<ResponseMessage> importUsers(String filename, String choice) {
        ArrayList<User> dataList = new ArrayList<>();
        File dataFile = new File(filename);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String headerEmail = jwtService.extractUserEmail(token);
        String headerName = userRepository.getUserNameByEmail(headerEmail);
        String msg;

        try (Scanner fileScanner = new Scanner(dataFile)) {
            ArrayList<String> lines = new ArrayList<>();

            while (fileScanner.hasNextLine()) {
                lines.add(fileScanner.nextLine());
            }

            for (int index = 1; index < lines.size(); index++) {
                String line = lines.get(index);
                String[] data = line.split(",");
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String password = randomStringGenerator(10);
                String encodedPassword = passwordEncoder.encode(password);
                User existedUser = userRepository.findByEmail(data[1]).orElse(null);
                switch (choice) {
                    case "Override":
                        if (existedUser != null) {
                            existedUser.setName(data[0]);
                            existedUser.setEmail(data[1]);
                            existedUser.setPhone("0" + data[2]);
                            existedUser.setDob(LocalDate.parse(data[3]));
                            existedUser.setGender(data[4]);
                            existedUser.setModifiedBy(headerName);
                            existedUser.setModifiedDate(LocalDate.now());
                            existedUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.valueOf(data[5])).orElse(null));
                            userRepository.save(existedUser);
                            log.info(String.valueOf(existedUser));
                        } else {
                            User user = User.builder()
                                    .name(data[0])
                                    .email(data[1])
                                    .password(encodedPassword)
                                    .phone("0" + data[2])
                                    .dob(LocalDate.parse(data[3]))
                                    .gender(data[4])
                                    .status(true)
                                    .createdBy(headerName)
                                    .createdDate(LocalDate.now())
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .role(userPermissionRepository.findUserPermissionByRole(Role.valueOf(data[5])).orElse(null))
                                    .build();
                            dataList.add(user);
                            log.info(String.valueOf(user));
                        }
                        break;
                    case "Skip":
                        if (existedUser == null) {
                            User user = User.builder()
                                    .name(data[0])
                                    .email(data[1])
                                    .password(encodedPassword)
                                    .phone("0" + data[2])
                                    .dob(LocalDate.parse(data[3]))
                                    .gender(data[4])
                                    .status(true)
                                    .createdBy(headerName)
                                    .createdDate(LocalDate.now())
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .role(userPermissionRepository.findUserPermissionByRole(Role.valueOf(data[5])).orElse(null))
                                    .build();
                            dataList.add(user);
                            log.info(String.valueOf(user));
                        } else {
                            msg = "This email is already exist.";
                            log.error(msg);
                        }
                        break;
                }

            }
            msg = "Import User successfully!";
            userRepository.saveAll(dataList);
            log.info(msg);
            return ResponseEntity.ok(new ResponseMessage("Success", msg));
        } catch (Exception e) {
            log.error("An error occurred while trying to import User. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "An error has occurred"));
        }
    }
}
