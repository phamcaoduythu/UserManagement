package com.example.usermanagement.service_implements;

import com.example.usermanagement.dto.Request.CreateTrainingProgramRequest;
import com.example.usermanagement.dto.Request.UpdateTrainingProgramRequest;
import com.example.usermanagement.dto.Response.ListTrainingProgramResponse;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.TrainingProgram;
import com.example.usermanagement.entity.TrainingProgramSyllabus;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.usermanagement.repository.SyllabusRepository;
import com.example.usermanagement.repository.TrainingProgramRepository;
import com.example.usermanagement.repository.TrainingProgramSyllabusRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service.TrainingProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrainingProgramServiceImpl implements TrainingProgramService {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final SyllabusRepository syllabusRepository;
    private final TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;

    /*
     * author: Ho Van Loc An
     * since: 2/29/2024 8:19 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> createTrainingProgram(CreateTrainingProgramRequest createTrainingProgramRequest) {
        try {
            TrainingProgram trainingProgram = new TrainingProgram();
            String msg;
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String headerEmail = jwtService.extractUserEmail(token);
            var authenticatedUser = userRepository.findByEmail(headerEmail).orElse(null);
            var topicUser = userRepository.findByEmail(createTrainingProgramRequest.getUserEmail()).orElse(null);
            if (trainingProgramRepository.getTrainingProgramByName(createTrainingProgramRequest.getName())
                    .orElse(null) == null) {
                trainingProgram.setName(createTrainingProgramRequest.getName());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("Failure", "This Training Program name is already been used!", null));
            }
            if (createTrainingProgramRequest.getDuration() > 0) {
                trainingProgram.setDuration(createTrainingProgramRequest.getDuration());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("Failure", "The duration cannot be negative!", null));
            }
            if (createTrainingProgramRequest.getStartDate().isAfter(LocalDate.now())) {
                trainingProgram.setStartDate(createTrainingProgramRequest.getStartDate());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("Failure", "Start date must be after the current date!", null));
            }
            if (topicUser != null) {
                trainingProgram.setName(createTrainingProgramRequest.getName());
                trainingProgram.setStatus("active");
                trainingProgram.setDuration(createTrainingProgramRequest.getDuration());
                trainingProgram.setCreatedBy(authenticatedUser.getName());
                trainingProgram.setCreatedDate(LocalDate.now());
                trainingProgram.setModifiedBy(authenticatedUser.getName());
                trainingProgram.setModifiedDate(LocalDate.now());
                trainingProgram.setUserID(topicUser);
                var result = trainingProgramRepository.save(trainingProgram);
                return ResponseEntity.ok(new ResponseObject("Success", "New training program has been created successfully!", result));
            } else {
                msg = "This email does not exist!";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to create Training program. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to create Training program", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> updateTrainingProgram(UpdateTrainingProgramRequest trainingProgram, int id) {
        try {
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String headerEmail = jwtService.extractUserEmail(token);
            var authenticatedUser = userRepository.findByEmail(headerEmail).orElse(null);
            String msg;
            if (authenticatedUser != null) {
                log.info("Getting the training program by ID..");
                if (id <= 0) {
                    msg = "Invalid Id of Training program.";
                    log.error(msg);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseMessage("Failure", msg));
                }
                if (trainingProgram.getDuration() < 0) {
                    msg = "Invalid duration of Training program.";
                    log.error(msg);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseMessage("Failure", msg));
                }
                TrainingProgram tp = trainingProgramRepository.getTrainingProgramByID(id);
                if (tp == null) {
                    msg = "Training program with Id " + id + " is not found!";
                    log.error(msg);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseMessage("Failure", msg));
                } else {
                    tp.setDuration(trainingProgram.getDuration());
                    tp.setModifiedBy(authenticatedUser.getName());
                    tp.setModifiedDate(LocalDate.now());
                    msg = "Training program has been updated successfully!";
                    log.info(msg);
                    return ResponseEntity.ok(new ResponseMessage("Success", msg));
                }
            } else {
                msg = "This email does not exist";
                log.error(msg);
                return ResponseEntity.ok(new ResponseMessage("Failure", msg));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to update Training program. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to update Training program"));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 2/29/2024 8:14 PM
     * description:
     * update:
     */
    public void createTrainingSyllabus(TrainingProgram trainingProgram, String[] syllabus) {
        try {
            for (String element : syllabus) {
                var Item = syllabusRepository.findById(element).orElse(null);
                if (Item != null) {
                    TrainingProgramSyllabus trainingProgramSyllabus =
                            TrainingProgramSyllabus.builder()
                                    .id(
                                            SyllabusTrainingProgramCompositeKey.builder()
                                                    .topicCode(Item.getTopicCode())
                                                    .trainingProgramCode(trainingProgram.getTrainingProgramCode())
                                                    .build())
                                    .topicCode(Item)
                                    .trainingProgramCode(trainingProgram)
                                    .deleted(false)
                                    .build();
                    trainingProgramSyllabusRepository.save(trainingProgramSyllabus);
                }
            }
        } catch (Exception e) {
            log.info("Failed to create TrainingSyllabus func. Error message: " + e);
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> duplicateTrainingProgram(String name) {
        try {
            var originProgram = trainingProgramRepository.findByName(name).orElse(null);
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String headerEmail = jwtService.extractUserEmail(token);
            String headerName = userRepository.getUserNameByEmail(headerEmail);
            var authenticatedUser = userRepository.findByEmail(headerEmail).orElse(null);
            String msg;
            if (originProgram == null) {
                msg = "This Training program does not exist.";
                log.error(msg);
                return ResponseEntity.ok(new ResponseMessage("Failure", msg));
            }
            List<String> listNames = trainingProgramRepository.findByName2(name);
            int count = 1;
            for (String checkName : listNames) {
                if (checkName.matches("(" + name + ")+_\\d")) {
                    count++;
                }
            }
            name = originProgram.getName();
            String trainingProgramName = name + "_" + count;
            TrainingProgram newTrainingProgram = TrainingProgram.builder()
                    .name(trainingProgramName)
                    .duration(originProgram.getDuration())
                    .userID(authenticatedUser)
                    .startDate(LocalDate.now())
                    .createdBy(headerName)
                    .createdDate(LocalDate.now())
                    .modifiedBy(headerName)
                    .modifiedDate(LocalDate.now())
                    .status(originProgram.getStatus())
                    .build();
            trainingProgramRepository.save(newTrainingProgram);
            msg = "Training program has been updated successfully!";
            log.info(msg);
            return ResponseEntity.ok(new ResponseMessage("Success", msg));
        } catch (Exception e) {
            log.error("An error occurred while trying to update Training program. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to update Training program"));
        }

    }

    @Override
    public ResponseEntity<ResponseMessage> importTrainingProgram(String fileName, String choice) {
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String headerEmail = jwtService.extractUserEmail(token);
        String headerName = userRepository.getUserNameByEmail(headerEmail);
        var authenticatedUser = userRepository.findByEmail(headerEmail).orElse(null);

        ArrayList<TrainingProgram> dataList = new ArrayList<>();
        File file = new File(fileName);
        String msg;
        try (Scanner scanner = new Scanner(file)) {
            ArrayList<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] data = line.split(",");
                var existedTrainingProgram = trainingProgramRepository.findByName(data[0]).orElse(null);
                switch (choice) {
                    case "Override":
                        if (existedTrainingProgram == null) {
                            var userImport = userRepository.findById(Integer.parseInt(data[3])).orElse(null);
                            TrainingProgram trainingProgram = TrainingProgram.builder()
                                    .name(data[0])
                                    .userID(userImport)
                                    .startDate(LocalDate.parse(data[4]))
                                    .duration(Integer.parseInt(data[1]))
                                    .status(data[2])
                                    .createdBy(headerName)
                                    .createdDate(LocalDate.now())
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .build();
                            dataList.add(trainingProgram);
                            log.info(trainingProgram);
                        } else {
                            var userImport = userRepository.findById(Integer.parseInt(data[3])).orElse(null);
                            existedTrainingProgram.setStartDate(LocalDate.parse(data[4]));
                            existedTrainingProgram.setUserID(userImport);
                            existedTrainingProgram.setDuration(Integer.parseInt(data[1]));
                            existedTrainingProgram.setStatus(data[2]);
                            existedTrainingProgram.setModifiedBy(headerName);
                            existedTrainingProgram.setModifiedDate(LocalDate.now());
                            trainingProgramRepository.save(existedTrainingProgram);
                            log.info(existedTrainingProgram);
                        }
                        break;
                    case "Allow":
                        if (existedTrainingProgram != null) {
                            var userImport = userRepository.findById(Integer.parseInt(data[3])).orElse(null);
                            List<String> listNames = trainingProgramRepository.findByName2(existedTrainingProgram.getName());
                            int count = 1;
                            for (String checkName : listNames) {
                                if (checkName.matches("(" + existedTrainingProgram.getName() + ")+_\\d")) {
                                    count++;
                                }
                            }
                            String topicNameClone = data[0] + "_" + count;
                            TrainingProgram trainingProgram = TrainingProgram.builder()
                                    .name(topicNameClone)
                                    .userID(userImport)
                                    .startDate(LocalDate.parse(data[4]))
                                    .duration(Integer.parseInt(data[1]))
                                    .status(data[2])
                                    .createdBy(headerName)
                                    .createdDate(LocalDate.now())
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .build();
                            dataList.add(trainingProgram);
                            log.info(trainingProgram);
                        } else {
                            var userImport = userRepository.findById(Integer.parseInt(data[3])).orElse(null);
                            TrainingProgram trainingProgram = TrainingProgram.builder()
                                    .name(data[0])
                                    .userID(userImport)
                                    .startDate(LocalDate.parse(data[4]))
                                    .duration(Integer.parseInt(data[1]))
                                    .status(data[2])
                                    .createdBy(headerName)
                                    .createdDate(LocalDate.now())
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .build();
                            dataList.add(trainingProgram);
                            log.info(trainingProgram);
                        }
                        break;
                    case "Skip":
                        if (existedTrainingProgram == null) {
                            var userImport = userRepository.findById(Integer.parseInt(data[3])).orElse(null);
                            TrainingProgram trainingProgram = TrainingProgram.builder()
                                    .name(data[0])
                                    .userID(userImport)
                                    .startDate(LocalDate.parse(data[4]))
                                    .duration(Integer.parseInt(data[1]))
                                    .status(data[2])
                                    .createdBy(headerName)
                                    .createdDate(LocalDate.now())
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .build();
                            dataList.add(trainingProgram);
                            log.info(trainingProgram);
                        } else {
                            msg = "This Training program is already exist";
                            log.error(msg);
                        }
                        break;
                }
            }
            msg = "Import Training program successfully";
            trainingProgramRepository.saveAll(dataList);
            log.info(msg);
            return ResponseEntity.ok(new ResponseMessage("Success", msg));
        } catch (Exception e) {
            log.error("An error occurred while importing Training program. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to import Training program"));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseMessage> changeTrainingProgramStatus(int trainingProgramCode, String value) {
        if (checkExisted(trainingProgramCode, value)) {
            switch (value) {
                case "Activate":
                    activateProgram(trainingProgramCode);
                    break;
                case "De-activate":
                    deactivateProgram(trainingProgramCode);
                    break;
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            new ResponseMessage(
                                    value + " training program successfully",
                                    "Training program with code "
                                            + trainingProgramCode
                                            + " is now "
                                            + value.toLowerCase()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ResponseMessage(
                                value + " training program failed",
                                "Training program with code "
                                        + trainingProgramCode
                                        + " is not found or already be "
                                        + value.toLowerCase()
                                        + "d"));
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    private void activateProgram(int trainingProgramCode) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProgramCode).orElse(null);
        if (trainingProgram != null) {
            trainingProgram.setStatus("active");
            trainingProgramRepository.save(trainingProgram);
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    private void deactivateProgram(int trainingProgramCode) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProgramCode).orElse(null);
        if (trainingProgram != null) {
            trainingProgram.setStatus("inactive");
            trainingProgramRepository.save(trainingProgram);
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */
    private boolean checkExisted(int trainingProgramCode, String value) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(trainingProgramCode).orElse(null);
        String result = value.equalsIgnoreCase("Activate") ? "active" : "inactive";
        return trainingProgram != null && !trainingProgram.getStatus().equalsIgnoreCase(result);
    }


    /*
     * author: Ho Van Loc An
     * since: 2/29/2024 11:32 PM
     * description: view information Training Program
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> getListTrainingProgram() {
        try {
            List<ListTrainingProgramResponse> list = trainingProgramRepository.getAllTrainingProgram();
            String msg = "list of Training program is loaded successfully!";
            log.info(msg);
            return ResponseEntity.ok(new ResponseObject("Success", msg, list));
        } catch (Exception e) {
            log.error("An error occurred while trying to create list of Training program . Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "Failed to create list of Training program", null));
        }
    }
    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:39 PM
     * description:
     * update:
     */

    // Search name training program--
    @Override
    public ResponseEntity<ResponseObject> getTrainingProgramByName(String name) {
        List<ListTrainingProgramResponse> list = trainingProgramRepository.findByNameV2(name);
        log.info("Fetching Training program by name: {} " + name);
        return ResponseEntity.ok(new ResponseObject("Success", "Fetching training program by name: {} " + name + "successfully", list));
    }


}
