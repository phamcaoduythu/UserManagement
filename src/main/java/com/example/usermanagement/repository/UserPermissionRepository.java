package com.example.usermanagement.repository;

import com.example.usermanagement.entity.UserPermission;
import com.example.usermanagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {
    Optional<UserPermission> findUserPermissionByRole(Role role);
}
