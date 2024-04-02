package com.example.usermanagement.controller;

import com.example.usermanagement.dto.Request.CreateSyllabusOutlineRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.SyllabusResponse;
import com.example.usermanagement.service.SyllabusService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/syllabus")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ClASS_ADMIN','TRAINER')")
@SecurityRequirement(name = "FSA_Phase_1")
public class SyllabusController {

    private final SyllabusService syllabusService;

    @GetMapping("/view")
    @PreAuthorize("hasAuthority('syllabus:read')")
    public ResponseEntity<List<SyllabusResponse>> viewAll() {
        return syllabusService.viewAllSyllabus();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('syllabus:create')")
    public ResponseEntity<ResponseMessage> create(@RequestBody CreateSyllabusOutlineRequest request) {
        return syllabusService.createSyllabus(request);
    }

    @PutMapping("/update/{topicCode}")
    @PreAuthorize("hasAuthority('syllabus:update')")
    public ResponseEntity<ResponseMessage> update(@RequestBody CreateSyllabusOutlineRequest request, @PathVariable String topicCode) {
        return syllabusService.updateSyllabus(request, topicCode);
    }

    @DeleteMapping("/delete/{topicCode}")
    @PreAuthorize("hasAuthority('syllabus:delete')")
    public ResponseEntity<ResponseMessage> delete(@PathVariable String topicCode) {
        return syllabusService.deleteSyllabus(topicCode);
    }

    @PutMapping("/duplicate/{topicCode}")
    @PreAuthorize("hasAuthority('syllabus:create')")
    public ResponseEntity<ResponseMessage> duplicateSyllabus(@PathVariable String topicCode) {
        return syllabusService.duplicateSyllabus(topicCode);
    }

    @GetMapping("/detail/{topicCode}")
    @PreAuthorize("hasAuthority('syllabus:read')")
    public ResponseEntity<ResponseObject> getDetail(@PathVariable String topicCode) {
        return syllabusService.getDetailSyllabus(topicCode);
    }

    @GetMapping("/search/{searchValue}")
    @PreAuthorize("hasAuthority('syllabus:read')")
    public ResponseEntity<List<SyllabusResponse>> search(@PathVariable String searchValue) {
        return syllabusService.searchSyllabus(searchValue);
    }

    @GetMapping("/searchByDate/{createdDate}")
    @PreAuthorize("hasAuthority('syllabus:read')")
    public ResponseEntity<List<SyllabusResponse>> searchByDate(@PathVariable LocalDate createdDate) {
        return syllabusService.searchSyllabusByCreatedDate(createdDate);
    }

    @PutMapping("/import")
    @PreAuthorize("hasAuthority('syllabus:create')")
    public ResponseEntity<ResponseMessage> importSyllabuses(@RequestParam String filename, @RequestParam String choice) {
        return syllabusService.importSyllabus(filename, choice);
    }

}
