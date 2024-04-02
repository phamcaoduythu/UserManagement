package com.example.usermanagement.repository;

import com.example.usermanagement.dto.Response.ListUserResponse;
import com.example.usermanagement.dto.UserDTO;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u.name FROM User u WHERE u.email = :email AND u.status = true")
    String getUserNameByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userId = :id AND u.status = true")
    Optional<User> findById(int id);

    @Query("SELECT u FROM User u WHERE u.userId = :id")
    Optional<User> findUserToChangeStatus(int id);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = true")
    Optional<User> findByEmail(String email);

    Optional<UserDTO> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.status = true")
    List<User> findByName(String name);

    List<User> findUsersByRoleAndStatus(UserPermission role, boolean status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.status = :newStatus WHERE u.userId = :id")
    void changeStatus(int id, boolean newStatus);

    @Query(
            value =
                    "SELECT u.user_id as 'userId', u.name, u.email, u.phone, u.dob, u.gender, u.created_by as 'createdBy', u.modified_by as 'modifiedBy', u.is_active as 'status', p.role\n"
                            + "FROM users as u INNER JOIN user_permission as p on u.role = p.permission_id",
            nativeQuery = true)
    List<ListUserResponse> getAllUsersWithRole();

}
