package com.example.usermanagement.controller;

import com.example.usermanagement.dto.Request.CreateTrainingProgramRequest;
import com.example.usermanagement.dto.Request.UpdateTrainingProgramRequest;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.service.TrainingProgramService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping(value = "/training_program")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ClASS_ADMIN','TRAINER')")
@SecurityRequirement(name = "FSA_Phase_1")
public class TrainingProgramController {

    private final TrainingProgramService trainingProgramService;

    /*
     * author: Ho Van Loc An
     * since: 3/1/2024 6:51 PM
     * description:
     * update:
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('training:create')")
    public ResponseEntity<ResponseObject> createTrainingProgram(
            @RequestBody CreateTrainingProgramRequest trainingProgramRequest) {
        return trainingProgramService.createTrainingProgram(trainingProgramRequest);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/1/2024 6:51 PM
     * description:
     * update:
     */
    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('training:read')")
    public ResponseEntity<ResponseObject> getTrainingProgramList() {
        return trainingProgramService.getListTrainingProgram();
    }

    @GetMapping(value = "/search/{name}")
    public ResponseEntity<ResponseObject> getTrainingProgramByName(@PathVariable String name) {
        return trainingProgramService.getTrainingProgramByName(name);
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('training:update')")
    public ResponseEntity<ResponseMessage> updateTrainingProgram(@PathVariable int id, @RequestBody UpdateTrainingProgramRequest request) {
        return trainingProgramService.updateTrainingProgram(request, id);
    }

    @GetMapping("/activate/{trainingProgramCode}")
    @PreAuthorize("hasAnyAuthority('training:update')")
    public ResponseEntity<ResponseMessage> activateTrainingProgram(
            @PathVariable int trainingProgramCode) {
        return trainingProgramService.changeTrainingProgramStatus(trainingProgramCode, "Activate");
    }

    @GetMapping("/deactivate/{trainingProgramCode}")
    @PreAuthorize("hasAnyAuthority('training:update')")
    public ResponseEntity<ResponseMessage> deactivateTrainingProgram(
            @PathVariable int trainingProgramCode) {
        return trainingProgramService.changeTrainingProgramStatus(trainingProgramCode, "De-activate");
    }

    @GetMapping(value = "/duplicate/{name}")
    @PreAuthorize("hasAuthority('training:read')")
    public ResponseEntity<ResponseMessage> duplicateTrainingProgram(@PathVariable String name) {
        return trainingProgramService.duplicateTrainingProgram(name);
    }

    @PutMapping("/import")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<ResponseMessage> importTrainingProgram(@RequestParam String fileName, @RequestParam String choice) throws FileNotFoundException {
        return trainingProgramService.importTrainingProgram(fileName, choice);
    }

}
