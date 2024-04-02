package com.example.usermanagement.controller;


import com.example.usermanagement.dto.Request.CreateClassDTO;
import com.example.usermanagement.dto.Request.UpdateCalendarRequest;
import com.example.usermanagement.dto.Request.UpdateClass3Request;
import com.example.usermanagement.dto.Response.CreateClassResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.UpdateClass3Response;
import com.example.usermanagement.dto.Response.UpdateClassResponse;
import com.example.usermanagement.dto.UpdateClassDTO;
import com.example.usermanagement.service_implements.ClassServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','CLASS_ADMIN')")
@SecurityRequirement(name = "FSA_Phase_1")
@RequestMapping(value = "/class")
public class ClassController {

    @Autowired
    ClassServiceImpl classService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('class:create')")
    public ResponseEntity<CreateClassResponse> create(@RequestBody CreateClassDTO createClassDTO) {
        return classService.createClass(createClassDTO);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<ResponseObject> getclass() {
        return classService.getAllClass();
    }

    @GetMapping("search/{id}")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<ResponseObject> getClassByID(@PathVariable String id) {
        return classService.getClassById(id);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/6/2024 9:26 PM
     * @description: deactivate class
     * @update:
     */
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateClass(@PathVariable("id") String classCode) {
        return classService.deactivateClass(classCode);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/6/2024 9:26 PM
     * @description: update class
     * @update:
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('class:update')")
    public ResponseEntity<UpdateClassResponse> updateClass(@RequestBody UpdateClassDTO updateClassRequest, @PathVariable("id") String classCode) {
        return ResponseEntity.ok(classService.updateClass(updateClassRequest, classCode));
    }

    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<ResponseObject> getClassDetail(@PathVariable("id") String classcode) {
        return classService.getClassDetail(classcode);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/8/2024 10:14 PM
     * @description: update class 3
     * @update:
     */
    @PutMapping("/updateClass3")
    public UpdateClass3Response updateClass3(@RequestBody UpdateClass3Request updateClass3Request) {
        return classService.updateClass3(updateClass3Request);
    }

    @PutMapping("/update-calendar")
    @PreAuthorize("hasAuthority('class:update')")
    public ResponseObject updateClassLearningDay(@RequestBody UpdateCalendarRequest updateCalendarRequest) throws ParseException {
        return classService.updateClassLearningDay(updateCalendarRequest);
    }

    @GetMapping("/search/name/{name}")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<ResponseObject> getClassByName(@PathVariable String name) {
        return classService.getClassByName(name);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseObject> filterClass(@RequestParam List<String> locations,
                                                      @RequestParam List<String> classTimes,
                                                      @RequestParam String startDate,
                                                      @RequestParam String endDate,
                                                      @RequestParam List<String> statuses,
                                                      @RequestParam String trainer) {
        return classService.filterClass(locations, classTimes, startDate, endDate, statuses, trainer);
    }

}

