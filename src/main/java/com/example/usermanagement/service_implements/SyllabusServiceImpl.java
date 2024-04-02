package com.example.usermanagement.service_implements;

import com.example.usermanagement.dto.ContentDTO;
import com.example.usermanagement.dto.MaterialDTO;
import com.example.usermanagement.dto.Request.CreateSyllabusOutlineRequest;
import com.example.usermanagement.dto.Request.DayDTO;
import com.example.usermanagement.dto.Response.ResponseMessage;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.SyllabusResponse;
import com.example.usermanagement.dto.UnitDTO;
import com.example.usermanagement.entity.*;
import com.example.usermanagement.entity.composite_key.SyllabusStandardOutputCompositeKey;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitCompositeKey;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingUnitTrainingContentCompositeKey;
import com.example.usermanagement.repository.*;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service.SyllabusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SyllabusServiceImpl implements SyllabusService {

    @Autowired
    SyllabusRepository syllabusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTService jwtService;
    @Autowired
    TrainingMaterialRepository trainingMaterialRepository;
    @Autowired
    TrainingUnitRepository trainingUnitRepository;
    @Autowired
    TrainingContentRepository trainingContentRepository;
    @Autowired
    StandardOutputRepository standardOutputRepository;
    @Autowired
    SyllabusObjectiveRepository syllabusObjectiveRepository;
    @Autowired
    TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;
    @Autowired
    TrainingProgramRepository trainingProgramRepository;

    @Override
    public ResponseEntity<List<SyllabusResponse>> viewAllSyllabus() {
        try {
            String msg;
            // get a list syllabus with Active,Draft Status and sort by day created desc
            List<Syllabus> syllabusList = syllabusRepository.findBySyllabusStatusInOrderByCreatedDateDesc(Arrays.asList("active", "draft"));
            if (syllabusList.isEmpty()) {
                msg = "Currently no records.";
                log.error(msg);
                return ResponseEntity.ok(Collections.emptyList());
            } else {
                // List to hold response objects for each syllabus
                List<SyllabusResponse> syllabusResponseList = new ArrayList<>();

                // Iterate over each syllabus and create response objects
                for (Syllabus syllabus : syllabusList) {
                    SyllabusResponse syllabusResponse = SyllabusResponse.builder()
                            .syllabusName(syllabus.getTopicName())
                            .syllabusCode(syllabus.getTopicCode())
                            .createdOn(syllabus.getCreatedDate())
                            .createdBy(syllabus.getCreatedBy().getName())
                            // get the duration of syllabus (sum of duration of all training content) *unit in minutes*
                            .duration(syllabusRepository.getTotalDurationOfTrainingContentByTopicCode(syllabus.getTopicCode()))
                            .syllabusStatus(syllabus.getPublishStatus())
                            // get the list of standard output of the syllabus
                            .syllabusObjectiveList(syllabusRepository.findOutputCodesByTopicCode(syllabus.getTopicCode()))
                            .build();

                    // Add the response object to the list
                    syllabusResponseList.add(syllabusResponse);
                }
                msg = "List of Syllabus is loaded successfully!";
                log.info(msg);
                return ResponseEntity.ok(syllabusResponseList);
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to get list of Syllabus. Error message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> createSyllabus(CreateSyllabusOutlineRequest request) {
        // Extracting user email from JWT token
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String userEmail = jwtService.extractUserEmail(token);
        String msg;

        // Getting the creator user from the database using the extracted email
        log.info("Getting creator information...");
        User creator = userRepository.findByEmail(userEmail).orElse(null);

        try {
            // Generate unique topic code with the first letter from each word of the topic name and two random letters (Uppercase)
            String topicCode = _generateUniqueTopicCode(request.getTopicName());

            // Creating a new syllabus entity with the provided details
            Syllabus syllabus = Syllabus.builder()
                    .topicCode(topicCode)
                    .assignment(request.getAssignment())
                    .assignmentLab(request.getAssignmentLab())
                    .conceptLecture(request.getConceptLecture())
                    .courseObjective(request.getCourseObjective())
                    .createdDate(LocalDate.now())
                    .deleted(false) // Assuming deleted is false by default
                    .exam(request.getExam())
                    .finalPractice(request.getFinalPractice())
                    .finalTheory(request.getFinalTheory())
                    .final_(request.getFinalValue())
                    .gpa(request.getGpa())
                    .guideReview(request.getGuideReview())
                    .createdBy(creator)
                    .priority(request.getPriority())
                    .publishStatus(request.getPublishStatus())
                    .quiz(request.getQuiz())
                    .technicalGroup(request.getTechnicalGroup())
                    .testQuiz(request.getTestQuiz())
                    .topicName(request.getTopicName())
                    .topicOutline(request.getTopicOutline())
                    .trainingAudience(request.getTrainingAudience())
                    .trainingPrinciples(request.getTrainingPrinciples())
                    .version(request.getVersion())
                    // Duration here is number of Day
                    .duration(request.getSyllabus().size())
                    .build();
            log.info("Saving new Syllabus to database...");
            var result = syllabusRepository.save(syllabus);
            //for training_program_syllabuses table (In DB)
            _createTrainingSyllabuses(result, request.getTrainingProgramName());
            //for syllabus_objective table (In DB)
            _createSyllabusObjective(result, request.getOutputCode());
            //for training_unit, training_content, training_material tables (In DB)
            _createSyllabusOutline(result, request);

            msg = "New Syllabus with topicCode: " + topicCode + " has been saved successfully!";
            return ResponseEntity.ok(new ResponseMessage("Success", msg));
        } catch (Exception e) {
            msg = "An error occurred while trying to create new Syllabus. Error message: ";
            log.error(msg + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", msg));
        }
    }

    private void _createSyllabusObjective(Syllabus syllabus, String[] outputCode) {
        try {
            // Iterate through each output code in the array
            for (String element : outputCode) {
                // Find the StandardOutput entity by its ID
                var Item = standardOutputRepository.findById(element).orElse(null);
                // Check if StandardOutput entity exists
                if (Item != null) {
                    // Create a new SyllabusObjective entity
                    SyllabusObjective syllabusObjective =
                            SyllabusObjective.builder()
                                    .id(
                                            SyllabusStandardOutputCompositeKey.builder().topicCode(syllabus.getTopicCode())
                                                    .outputCode(Item.getOutputCode()).build())
                                    .topicCode(syllabus)
                                    .outputCode(Item)
                                    .build();
                    // Save the SyllabusObjective entity to the database
                    syllabusObjectiveRepository.save(syllabusObjective);
                } else {
                    log.error("Cannot find standard output code: " + element);
                    // it will be skipped and can continue to create
                }
            }
        } catch (Exception e) {
            log.info("Error occur in create Syllabus Objective: " + e);
        }
    }

    private void _createTrainingSyllabuses(Syllabus syllabus, String[] trainingProgramName) {
        try {
            // Iterate through each trainingProgramName in the array
            for (String element : trainingProgramName) {
                // Find the trainingProgram entity by its name
                var Item = trainingProgramRepository.getTrainingProgramByName1(element).orElse(null);
                // Check if trainingProgram entity exists
                if (Item != null) {
                    // Create a new TrainingProgramSyllabus entity
                    TrainingProgramSyllabus trainingProgramSyllabus =
                            TrainingProgramSyllabus.builder()
                                    .id(
                                            SyllabusTrainingProgramCompositeKey.builder()
                                                    .topicCode(syllabus.getTopicCode())
                                                    .trainingProgramCode(Item.getTrainingProgramCode())
                                                    .build())
                                    .topicCode(syllabus)
                                    .trainingProgramCode(Item)
                                    .deleted(false)
                                    .build();

                    // Save the TrainingProgramSyllabus entity to the database
                    trainingProgramSyllabusRepository.save(trainingProgramSyllabus);
                } else {
                    log.error("Cannot find trainingProgram name: " + element);
                    // it will be skipped and can continue to create
                }
            }
        } catch (Exception e) {
            log.info("Error occur in create TrainingProgramSyllabus Objective: " + e);
        }
    }

    private void _createSyllabusOutline(Syllabus syllabus, CreateSyllabusOutlineRequest request) {
        try {

            // Iterate over each day in the syllabus
            for (DayDTO day : request.getSyllabus()) {
                log.info("Creating Training units for Day " + day.getDayNumber() + "...");
                // Iterate over each unit in the day
                for (UnitDTO unit : day.getUnitList()) {

                    // Create a new TrainingUnit entity
                    TrainingUnit trainingUnit = TrainingUnit.builder()
                            .unitName(unit.getUnitName())
                            .dayNumber(day.getDayNumber())
                            .id(SyllabusTrainingUnitCompositeKey.builder()
                                    .tCode(syllabus.getTopicCode())
                                    .build())
                            .syllabus(syllabus)
                            .build();
                    // Save it to the database
                    trainingUnitRepository.save(trainingUnit);

                    log.info("Creating Training content for Unit " + trainingUnit.getId().getUCode() + "...");
                    // Iterate over each content in the unit
                    for (ContentDTO content : unit.getContentList()) {
                        // Find the StandardOutput entity by its ID
                        var Item = standardOutputRepository.findById(content.getStandardOutput()).orElse(null);
                        if (Item != null) {
                            // Create a new TrainingContent entity
                            TrainingContent trainingContent = TrainingContent.builder()
                                    .id(SyllabusTrainingUnitTrainingContentCompositeKey.builder()
                                            .id(SyllabusTrainingUnitCompositeKey.builder()
                                                    .tCode(syllabus.getTopicCode())
                                                    .uCode(trainingUnit.getId().getUCode())
                                                    .build())
                                            .build())
                                    .contentName(content.getContentName())
                                    .unitCode(trainingUnit)
                                    .deliveryType(content.getDeliveryType())
                                    .duration(content.getDuration())
                                    .trainingFormat(content.getOnline())
                                    .note(content.getNote())
                                    .outputCode(Item)
                                    .build();
                            // Save the training content to the database
                            trainingContentRepository.save(trainingContent);
                            log.info("Creating Training material...");
                            // Iterate over each material in the content
                            for (MaterialDTO material : content.getTrainingMaterial()) {
                                // Create a new TrainingMaterial entity
                                TrainingMaterial trainingMaterial = TrainingMaterial.builder()
                                        .material(material.getMaterial())
                                        .source(material.getSource())
                                        .trainingContent(trainingContent)
                                        .build();
                                // Save the training material to the database
                                trainingMaterialRepository.save(trainingMaterial);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("An error occurred while trying to create Syllabus Training unit list. Error message: " + e);
        }
    }

    private String _generateUniqueTopicCode(String topicName) {
        try {
            String[] words = topicName.split("\\s+"); // Split topic name into words

            StringBuilder topicCodeBuilder = new StringBuilder();
            // Extract up to three letters from each word
            for (String word : words) {
                if (topicCodeBuilder.length() >= 3) {
                    break; // Maximum of three letters reached
                }
                if (!word.isEmpty()) {
                    topicCodeBuilder.append(word.charAt(0)); // Append the first letter of each word
                }
            }

            // Generate random letters if the topic name is not long enough
            Random random = new Random();
            while (topicCodeBuilder.length() < 3) {
                char randomLetter = (char) (random.nextInt(26) + 'a');
                topicCodeBuilder.append(randomLetter);
            }
            // Append random three-digit number
            int randomNumber = random.nextInt(900) + 100;
            topicCodeBuilder.append(randomNumber);

            String generatedTopicCode = topicCodeBuilder.toString().toUpperCase();

            // Check if the generated topic code already exists in the database
            while (syllabusRepository.findById(generatedTopicCode).isPresent()) {
                // If the code already exists, change the random number
                randomNumber = random.nextInt(900) + 100;
                topicCodeBuilder.replace(topicCodeBuilder.length() - 3, topicCodeBuilder.length(), Integer.toString(randomNumber));
                generatedTopicCode = topicCodeBuilder.toString().toUpperCase();
            }
            return generatedTopicCode;
        } catch (Exception e) {
            log.info("Error occur in create Syllabus Topic code: " + e);
            return null;
        }
    }

    private void _createTrainingSyllabusImport(TrainingProgram trainingProgram, String topicCode) {
        try {
            var Item = syllabusRepository.findById(topicCode).orElse(null);
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

        } catch (Exception e) {
            log.info("Error occur in create TrainingSyllabus func " + e);
        }
    }

    @Override
    public ResponseEntity<List<SyllabusResponse>> searchSyllabus(String searchValue) {
        try {
            String msg;
            // get a list syllabus with topicCode or TopicName or CreatedBy match searchValue
            List<Syllabus> syllabusList = syllabusRepository.searchSyllabus(searchValue);
            if (syllabusList.isEmpty()) {
                // If no syllabuses are found, return an empty list
                msg = "No syllabus match your description";
                log.error(msg);
                return ResponseEntity.ok(Collections.emptyList());
            } else {
                // If syllabuses are found, create a list of SyllabusResponse objects
                List<SyllabusResponse> syllabusResponseList = new ArrayList<>();
                for (Syllabus syllabus : syllabusList) {
                    // Create a SyllabusResponse object for each syllabus found
                    SyllabusResponse syllabusResponse = SyllabusResponse.builder()
                            .syllabusName(syllabus.getTopicName())
                            .syllabusCode(syllabus.getTopicCode())
                            .createdOn(syllabus.getCreatedDate())
                            .createdBy(syllabus.getCreatedBy().getName())
                            // get the duration of syllabus (sum of duration of all training content) *unit in minutes*
                            .duration(syllabusRepository.getTotalDurationOfTrainingContentByTopicCode(syllabus.getTopicCode()))
                            .syllabusStatus(syllabus.getPublishStatus())
                            // get the list of standard output of the syllabus
                            .syllabusObjectiveList(syllabusRepository.findOutputCodesByTopicCode(syllabus.getTopicCode()))
                            .build();
                    syllabusResponseList.add(syllabusResponse);
                }
                msg = "Found all syllabus";
                log.info(msg);
                return ResponseEntity.ok(syllabusResponseList);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve syllabuses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @Override
    public ResponseEntity<List<SyllabusResponse>> searchSyllabusByCreatedDate(LocalDate createdDate) {
        try {
            String msg;
            // get a list syllabus with created Date (*Using DatePicker to input*)
            List<Syllabus> syllabusList = syllabusRepository.findByCreatedDate(createdDate);
            if (syllabusList.isEmpty()) {
                // If no syllabuses are found, return an empty list
                msg = "No syllabus match your description";
                log.error(msg);
                return ResponseEntity.ok(Collections.emptyList());
            } else {
                // If syllabuses are found, create a list of SyllabusResponse objects
                List<SyllabusResponse> syllabusResponseList = new ArrayList<>();
                for (Syllabus syllabus : syllabusList) {
                    // Create a SyllabusResponse object for each syllabus found
                    SyllabusResponse syllabusResponse = SyllabusResponse.builder()
                            .syllabusName(syllabus.getTopicName())
                            .syllabusCode(syllabus.getTopicCode())
                            .createdOn(syllabus.getCreatedDate())
                            .createdBy(syllabus.getCreatedBy().getName())
                            // get the duration of syllabus (sum of duration of all training content) *unit in minutes*
                            .duration(syllabusRepository.getTotalDurationOfTrainingContentByTopicCode(syllabus.getTopicCode()))
                            .syllabusStatus(syllabus.getPublishStatus())
                            // get the list of standard output of the syllabus
                            .syllabusObjectiveList(syllabusRepository.findOutputCodesByTopicCode(syllabus.getTopicCode()))
                            .build();
                    syllabusResponseList.add(syllabusResponse);
                }
                msg = "Found all syllabus";
                log.info(msg);
                return ResponseEntity.ok(syllabusResponseList);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve syllabuses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> updateSyllabus(CreateSyllabusOutlineRequest request, String topicCode) {
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String userEmail = jwtService.extractUserEmail(token);
        String msg;

        // Check if the syllabus exists
        Optional<Syllabus> existingSyllabusOptional = syllabusRepository.findById(topicCode);
        if (existingSyllabusOptional.isEmpty()) {
            // If the syllabus does not exist, return failure response
            msg = "Syllabus with topic code: " + topicCode + " is not found";
            log.error(msg);
            return ResponseEntity.ok(new ResponseMessage("Failure", msg));
        }

        // Getting the creator user from the database using the extracted email
        log.info("Getting modifier's information...");
        User creator = userRepository.findByEmail(userEmail).orElse(null);

        Syllabus existingSyllabus = existingSyllabusOptional.get();

        try {
            assert creator != null;

            // Update the syllabus with the new information
            existingSyllabus.setAssignment(request.getAssignment());
            existingSyllabus.setAssignmentLab(request.getAssignmentLab());
            existingSyllabus.setConceptLecture(request.getConceptLecture());
            existingSyllabus.setCourseObjective(request.getCourseObjective());
            existingSyllabus.setExam(request.getExam());
            existingSyllabus.setFinalPractice(request.getFinalPractice());
            existingSyllabus.setFinalTheory(request.getFinalTheory());
            existingSyllabus.setFinal_(request.getFinalValue());
            existingSyllabus.setGpa(request.getGpa());
            existingSyllabus.setGuideReview(request.getGuideReview());
            existingSyllabus.setModifiedDate(LocalDate.now());
            existingSyllabus.setPriority(request.getPriority());
            existingSyllabus.setPublishStatus(request.getPublishStatus());
            existingSyllabus.setQuiz(request.getQuiz());
            existingSyllabus.setTechnicalGroup(request.getTechnicalGroup());
            existingSyllabus.setTestQuiz(request.getTestQuiz());
            existingSyllabus.setTopicName(request.getTopicName());
            existingSyllabus.setTopicOutline(request.getTopicOutline());
            existingSyllabus.setTrainingAudience(request.getTrainingAudience());
            existingSyllabus.setTrainingPrinciples(request.getTrainingPrinciples());
            existingSyllabus.setVersion(request.getVersion());
            existingSyllabus.setModifiedBy(creator.getName());
            existingSyllabus.setDuration(request.getSyllabus().size());

            syllabusRepository.save(existingSyllabus);

            //Update trainingProgramSyllabus
            trainingProgramSyllabusRepository.deleteByTopicCode(topicCode);
            _createTrainingSyllabuses(existingSyllabus, request.getTrainingProgramName());

            // Update objectives(Delete and create new *Transaction: roll back when error*)
            syllabusObjectiveRepository.deleteByTopicCode(topicCode);
            _createSyllabusObjective(existingSyllabus, request.getOutputCode());

            // Update outline(Delete and create new *Transaction: roll back when error*)
            trainingUnitRepository.deleteTrainingMaterialByTopicCode(topicCode);
            trainingUnitRepository.deleteTrainingContentByTopicCode(topicCode);
            trainingUnitRepository.deleteTrainingUnitByTopicCode(topicCode);
            _createSyllabusOutline(existingSyllabus, request);

            msg = "Syllabus has been updated successfully!";
            return ResponseEntity.ok(new ResponseMessage("Success", msg));
        } catch (Exception e) {
            msg = "An error occurred while trying to update Syllabus";
            log.error("An error occurred while trying to update Syllabus. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", msg));
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteSyllabus(String topicCode) {
        String msg;
        try {
            // Retrieve the syllabus to delete
            Syllabus existedSyllabus = syllabusRepository.findById(topicCode).orElse(null);
            if (existedSyllabus != null) {
                // Mark the syllabus as deleted
                existedSyllabus.setDeleted(true);
                // Save the updated syllabus to mark it as deleted
                syllabusRepository.save(existedSyllabus);
                msg = "Syllabus with Id: " + topicCode + " has been deleted successfully!";
                log.info(msg);
                return ResponseEntity.ok(new ResponseMessage("Success", msg));
            } else {
                // If the syllabus does not exist, return failure response
                msg = "Syllabus with Id: " + topicCode + " is not found!";
                log.error(msg);
                return ResponseEntity.ok(new ResponseMessage("Failure", msg));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to delete Syllabus. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "An error occurred while trying to delete Syllabus"));
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> duplicateSyllabus(String topicCode) {
        String msg;
        try {
            // Retrieve the syllabus response of getDetail
            ResponseEntity<ResponseObject> detailResponse = getDetailSyllabus(topicCode);

            if (detailResponse.getStatusCode() == HttpStatus.OK) {
                // Extract the original syllabus from response
                CreateSyllabusOutlineRequest originalSyllabusDetail = (CreateSyllabusOutlineRequest) Objects.requireNonNull(detailResponse.getBody()).getPayload();

                // Extract the necessary details from the original syllabus
                String topicName = originalSyllabusDetail.getTopicName();

                // Determine the duplicate name
                List<String> listNames = syllabusRepository.findByName2(topicName);
                int count = 1;
                for (String checkName : listNames) {
                    if (checkName.matches("(" + topicName + ")+_\\d")) {
                        count++;
                    }
                }
                String topicNameClone = topicName + "_" + count;
                originalSyllabusDetail.setTopicName(topicNameClone);

                // Create the duplicated syllabus
                ResponseEntity<ResponseMessage> createResponse = createSyllabus(originalSyllabusDetail);
                if (createResponse.getStatusCode() == HttpStatus.OK) {
                    msg = "Duplicate Syllabus with topic" + topicCode + " has been created successfully!";
                    log.info(msg);
                    return ResponseEntity.ok(new ResponseMessage("Success", msg));
                } else {
                    msg = "Failed to create duplicated Syllabus. Error message: " + Objects.requireNonNull(createResponse.getBody()).getMessage();
                    log.error(msg);
                    return ResponseEntity.ok(new ResponseMessage("Failure", "Failed to create duplicated Syllabus"));
                }

            } else {
                msg = "Syllabus with topic code: " + topicCode + " is not found!";
                log.error(msg);
                return ResponseEntity.ok(new ResponseMessage("Failure", msg));
            }
        } catch (Exception e) {
            msg = "An error occurred while trying to create duplicate Syllabus";
            log.error(msg + ". Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", msg));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getDetailSyllabus(String topicCode) {
        try {
            // Retrieve syllabus by topic code
            Syllabus syllabus = syllabusRepository.findById(topicCode).orElse(null);
            String msg;

            if (syllabus != null) {
                // Retrieve syllabus objectives and extract output codes
                String[] outputCodes = syllabus.getSyllabusObjectives().stream()
                        .map(SyllabusObjective::getOutputCode)
                        .map(StandardOutput::getOutputCode)
                        .toArray(String[]::new);

                // Extract unique day numbers and sort them asc
                List<Integer> uniqueDayNumbers = syllabus.getTu().stream()
                        .map(TrainingUnit::getDayNumber)
                        .distinct()
                        .sorted()
                        .toList();

                // Retrieve training program syllabus and extract training program names
                String[] trainingProgramName = trainingProgramSyllabusRepository.findTrainingProgramNamesByTopicCode(topicCode).toArray(new String[0]);

                // Populate DayDTOs for each unique day number
                List<DayDTO> syllabusDays = new ArrayList<>();
                for (int dayNumber : uniqueDayNumbers) {
                    List<UnitDTO> unitDTOs = new ArrayList<>();
                    List<TrainingUnit> unitsWithDayNumber = syllabus.getTu().stream()
                            .filter(unit -> unit.getDayNumber() == dayNumber)
                            .toList();

                    // Populate UnitDTOs for each TrainingUnit with the same dayNumber
                    for (TrainingUnit trainingUnit : unitsWithDayNumber) {
                        List<ContentDTO> contentDTOs = new ArrayList<>();
                        for (TrainingContent trainingContent : trainingUnit.getTrainingContents()) {
                            List<MaterialDTO> materialDTOs = trainingContent.getTrainingMaterials().stream()
                                    .map(material -> MaterialDTO.builder()
                                            .material(material.getMaterial())
                                            .source(material.getSource())
                                            .build())
                                    .collect(Collectors.toList());

                            ContentDTO contentDTO = ContentDTO.builder()
                                    .contentName(trainingContent.getContentName())
                                    .deliveryType(trainingContent.getDeliveryType())
                                    .standardOutput(trainingContent.getOutputCode().getOutputCode())
                                    .note(trainingContent.getNote())
                                    .duration(trainingContent.getDuration())
                                    .online(trainingContent.isTrainingFormat())
                                    .trainingMaterial(materialDTOs)
                                    .build();

                            contentDTOs.add(contentDTO);
                        }

                        UnitDTO unitDTO = UnitDTO.builder()
                                .unitName(trainingUnit.getUnitName())
                                .contentList(contentDTOs)
                                .build();

                        unitDTOs.add(unitDTO);
                    }

                    DayDTO dayDTO = DayDTO.builder()
                            .dayNumber(dayNumber)
                            .unitList(unitDTOs)
                            .build();

                    syllabusDays.add(dayDTO);
                }

                // Take CreateSyllabusOutlineRequest obj as SyllabusDetail
                // Build the custom SyllabusDetail object
                CreateSyllabusOutlineRequest syllabusDetail = CreateSyllabusOutlineRequest.builder()
                        .topicName(syllabus.getTopicName())
                        .topicOutline(syllabus.getTopicOutline())
                        .version(syllabus.getVersion())
                        .technicalGroup(syllabus.getTechnicalGroup())
                        .priority(syllabus.getPriority())
                        .courseObjective(syllabus.getCourseObjective())
                        .publishStatus(syllabus.getPublishStatus())
                        .trainingPrinciples(syllabus.getTrainingPrinciples())
                        .trainingAudience(syllabus.getTrainingAudience())
                        .assignmentLab(syllabus.getAssignmentLab())
                        .conceptLecture(syllabus.getConceptLecture())
                        .guideReview(syllabus.getGuideReview())
                        .testQuiz(syllabus.getTestQuiz())
                        .exam(syllabus.getExam())
                        .quiz(syllabus.getQuiz())
                        .assignment(syllabus.getAssignment())
                        .finalValue(syllabus.getFinal_())
                        .finalTheory(syllabus.getFinalTheory())
                        .finalPractice(syllabus.getFinalPractice())
                        .gpa(syllabus.getGpa())
                        .outputCode(outputCodes)
                        .syllabus(syllabusDays)
                        .trainingProgramName(trainingProgramName)
                        .build();
                msg = "Retrieving details of Syllabus with topic code" + syllabus.getTopicCode() + " successfully!";
                log.info(msg);
                return ResponseEntity.ok(new ResponseObject("Success", msg, syllabusDetail));
            } else {
                msg = "Syllabus with topic code: " + topicCode + " is not found!";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to retrieve details of Syllabus. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "An error occurred while trying to retrieve details of Syllabus", null));
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> importSyllabus(String filename, String choice) {
        File dataFile = new File(filename);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String headerEmail = jwtService.extractUserEmail(token);
        User requestUser = userRepository.findByEmail(headerEmail).orElse(null);
        assert requestUser != null;
        String headerName = requestUser.getName();
        String msg;

        try (Scanner fileScanner = new Scanner(dataFile)) {
            ArrayList<String> lines = new ArrayList<>();

            while (fileScanner.hasNextLine()) {
                lines.add(fileScanner.nextLine());
            }

            for (int index = 1; index < lines.size(); index++) {
                String line = lines.get(index);
                String[] data = line.split(",");
                Syllabus existedSyllabus = syllabusRepository.findById(data[0]).orElse(null);
                var trainingProgram = trainingProgramRepository.getTrainingProgramByName1(data[10]).orElse(null);
                if (trainingProgram == null) {
                    log.error("Cannot find Training program with name " + data[10]);
                }
                switch (choice) {
                    //If file exist replace with the latest one
                    case "Override":
                        if (existedSyllabus == null) {
                            Syllabus syllabus = Syllabus.builder()
                                    .topicCode(data[0])
                                    .topicName(data[1])
                                    .topicOutline(data[2])
                                    .version(data[3])
                                    .courseObjective(data[4])
                                    .createdDate(LocalDate.now())
                                    .createdBy(requestUser)
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .priority(data[5])
                                    .publishStatus(data[6])
                                    .technicalGroup(data[7])
                                    .trainingAudience(Integer.parseInt(data[8]))
                                    .trainingPrinciples(data[9])
                                    .build();
                            syllabusRepository.save(syllabus);
                            _createTrainingSyllabusImport(trainingProgram, data[0]);
                            log.info(String.valueOf(syllabus));
                        } else {
                            existedSyllabus.setTopicName(data[1]);
                            existedSyllabus.setTopicOutline(data[2]);
                            existedSyllabus.setVersion(data[3]);
                            existedSyllabus.setCourseObjective(data[4]);
                            existedSyllabus.setModifiedBy(headerName);
                            existedSyllabus.setModifiedDate(LocalDate.now());
                            existedSyllabus.setPriority(data[5]);
                            existedSyllabus.setPublishStatus(data[6]);
                            existedSyllabus.setTechnicalGroup(data[7]);
                            existedSyllabus.setTrainingAudience(Integer.parseInt(data[8]));
                            existedSyllabus.setTrainingPrinciples(data[9]);
                            syllabusRepository.save(existedSyllabus);
                            _createTrainingSyllabusImport(trainingProgram, data[0]);
                            log.info(String.valueOf(existedSyllabus));
                        }
                        break;
                    //If file exist, duplicate it.
                    case "Allow":
                        if (existedSyllabus != null) {
                            List<String> listNames = syllabusRepository.findByName2(data[1]);
                            int count = 1;
                            for (String checkName : listNames) {
                                if (checkName.matches("(" + data[1] + ")+_\\d")) {
                                    count++;
                                }
                            }
                            String topicNameClone = data[1] + "_" + count;
                            Syllabus syllabus = Syllabus.builder()
                                    .topicCode(_generateUniqueTopicCode(data[1]))
                                    .topicName(topicNameClone)
                                    .topicOutline(data[2])
                                    .version(data[3])
                                    .courseObjective(data[4])
                                    .createdDate(LocalDate.now())
                                    .createdBy(requestUser)
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .priority(data[5])
                                    .publishStatus(data[6])
                                    .technicalGroup(data[7])
                                    .trainingAudience(Integer.parseInt(data[8]))
                                    .trainingPrinciples(data[9])
                                    .build();
                            syllabusRepository.save(syllabus);
                            _createTrainingSyllabusImport(trainingProgram, data[0]);
                            log.info(String.valueOf(syllabus));
                        } else {
                            Syllabus syllabus = Syllabus.builder()
                                    .topicCode(data[0])
                                    .topicName(data[1])
                                    .topicOutline(data[2])
                                    .version(data[3])
                                    .courseObjective(data[4])
                                    .createdDate(LocalDate.now())
                                    .createdBy(requestUser)
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .priority(data[5])
                                    .publishStatus(data[6])
                                    .technicalGroup(data[7])
                                    .trainingAudience(Integer.parseInt(data[8]))
                                    .trainingPrinciples(data[9])
                                    .build();
                            syllabusRepository.save(syllabus);
                            _createTrainingSyllabusImport(trainingProgram, data[0]);
                            log.info(String.valueOf(syllabus));
                        }
                        break;
                    //If file exist, skip it.
                    case "Skip":
                        if (existedSyllabus == null) {
                            Syllabus syllabus = Syllabus.builder()
                                    .topicCode(data[0])
                                    .topicName(data[1])
                                    .topicOutline(data[2])
                                    .version(data[3])
                                    .courseObjective(data[4])
                                    .createdDate(LocalDate.now())
                                    .createdBy(requestUser)
                                    .modifiedBy(headerName)
                                    .modifiedDate(LocalDate.now())
                                    .priority(data[5])
                                    .publishStatus(data[6])
                                    .technicalGroup(data[7])
                                    .trainingAudience(Integer.parseInt(data[8]))
                                    .trainingPrinciples(data[9])
                                    .build();
                            syllabusRepository.save(syllabus);
                            _createTrainingSyllabusImport(trainingProgram, data[0]);
                            log.info(String.valueOf(syllabus));
                        } else {
                            msg = "Syllabus with topic code " + data[0] + " is already exist";
                            log.error(msg);
                        }
                        break;
                }
            }
            msg = "Import Syllabus successfully!";
            log.info(msg);
            return ResponseEntity.ok(new ResponseMessage("Success", msg));
        } catch (Exception e) {
            log.error("An error occurred while trying to import Syllabus. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseMessage("Failure", "An error occurred while trying to import Syllabus"));
        }
    }

}
