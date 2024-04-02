package com.example.usermanagement.controller;

import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user")
@PreAuthorize("hasAnyRole('CLASS_ADMIN', 'SUPER_ADMIN', 'TRAINER')")
@SecurityRequirement(name = "FSA_Phase_1")
public class UserController {

    private final UserService userService;

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getUserList() {
        return userService.getUserList();
    }

    @GetMapping("/search/email/{email}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/search/name/{name}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getUserByName(@PathVariable String name) {
        return userService.getUserByName(name);
    }

    @GetMapping("/search/role/{role}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getUserByRole(@PathVariable String role) {
        return userService.getUserByRole(role);
    }

    @GetMapping("/search/id/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getUserByID(@PathVariable("id") int id) {
        return userService.getUserByID(id);
    }

    @PutMapping("/active/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ResponseMessage> activeUser(@PathVariable int id) {
        return userService.changeStatus(id, "active");
    }

    @PutMapping("/inactive/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<ResponseMessage> inactiveUser(@PathVariable int id) {
        return userService.changeStatus(id, "inactive");
    }

    @PutMapping("/role/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ResponseMessage> changeRole(@PathVariable("id") int id, @RequestParam(value = "role") String role) {
        return userService.changeRole(id, role);
    }

    @PutMapping("/import")
    @PreAuthorize("hasAuthority('user:import')")
    public ResponseEntity<ResponseMessage> importUser(@RequestParam String fileName, @RequestParam String choice) {
        return userService.importUsers(fileName, choice);
    }

}
