package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Class;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface ClassRepository extends JpaRepository<Class, String> {

    @Query(value = "select * from Class order by modified_date desc", nativeQuery = true)
    List<Class> getAll();

    @Query("SELECT c FROM Class c WHERE c.classId LIKE :classCode")
    Class findByClassCode(String classCode);

    @Query(value = "select c from Class c where c.deactivated = False and c.className LIKE %:name%")
    List<Class> getClassByName(String name);

    @JsonIgnore
    @Query(value = "SELECT DISTINCT c.* FROM class c " +
            "JOIN class_user cu ON cu.class_id = c.class_code " +
            "JOIN users u ON cu.users_id = u.user_id " +
            "WHERE cast(c.start_date as date) >= :startDate " +
            "AND cast(c.start_date as date) <= :endDate " +
            "AND cast(c.time_from as time) >= cast(:timeFrom as time) " +
            "AND cast(c.time_to as time) <= cast(:timeTo as time) " +
            "AND c.status in :status " +
            "AND u.email like %:trainer% " +
            "AND c.location in :location ", nativeQuery = true)
    List<Object> classFilter(List<String> location, LocalDate startDate, LocalDate endDate, Time timeFrom, Time timeTo, List<String> status, String trainer);

}

