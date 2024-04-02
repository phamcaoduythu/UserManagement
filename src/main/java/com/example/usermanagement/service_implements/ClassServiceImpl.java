package com.example.usermanagement.service_implements;


import com.example.usermanagement.dto.Request.CreateClassDTO;
import com.example.usermanagement.dto.Request.UpdateCalendarRequest;
import com.example.usermanagement.dto.Request.UpdateClass3Request;
import com.example.usermanagement.dto.Response.*;
import com.example.usermanagement.dto.*;
import com.example.usermanagement.entity.Class;
import com.example.usermanagement.entity.*;
import com.example.usermanagement.entity.composite_key.ClassUserCompositeKey;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.usermanagement.repository.*;
import com.example.usermanagement.service.ClassService;
import com.example.usermanagement.service.JWTService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {


    @Autowired
    JWTService jwtService;

    @Autowired
    ClassLearningDayRepository classLearningDayRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    TrainingProgramRepository trainingProgramRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassUserRepository classUserRepository;

    @Autowired
    SyllabusRepository syllabusRepository;

    @Autowired
    UserClassSyllabusRepository userClassSyllabusRepository;

    @Autowired
    TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;


    /**
     * Create Class
     * Author: Nguyen Vinh Khang
     *
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<CreateClassResponse> createClass(CreateClassDTO request) {
        try {
            var existedClass = classRepository.findById(request.getClassCode()).orElse(null);
            if (existedClass != null) {
                return ResponseEntity.ok(new CreateClassResponse("This Class is already existed!", "Failure", null));
            }

            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String userEmail = jwtService.extractUserEmail(token);
            var requester = userRepository.findUserByEmail(userEmail).orElse(null);

            LocalDate startDate = LocalDate.parse(request.getStartDate());
            LocalDate endDate = LocalDate.parse(request.getEndDate());
            String duration = String.valueOf(ChronoUnit.DAYS.between(startDate, endDate));
            String timeFromString = request.getClassTimeFrom().split(":").length == 3 ? request.getClassTimeFrom() : request.getClassTimeFrom() + ":00";
            String timeToString = request.getClassTimeTo().split(":").length == 3 ? request.getClassTimeTo() : request.getClassTimeTo() + ":00";

            if (startDate.isBefore(LocalDate.now()) || startDate.isEqual(LocalDate.now())
                    || endDate.isBefore(LocalDate.now()) || endDate.isEqual(LocalDate.now())) {
                return ResponseEntity.ok(new CreateClassResponse("Invalid start Date! Start Date for Class should be at least one day after today!", "Failure", null));
            } else if (startDate.isAfter(endDate)) {
                return ResponseEntity.ok(new CreateClassResponse("Invalid start Date! Start Date must be before end Date!", "Failure", null));
            }

            User user;

            Class classInfo = null;
            List<ClassLearningDay> classLearningDays = new ArrayList<>();
            List<ClassUser> classUsers = new ArrayList<>();
            List<UserClassSyllabus> userSyllabusList = new ArrayList<>();

            TrainingProgram trainingProgram = trainingProgramRepository.findById(request.getTrainingProgram()).orElse(null);
            if (trainingProgram == null) {
                return ResponseEntity.ok(new CreateClassResponse("Training program with ID " + request.getTrainingProgram() + " does not exit!", "Failure", null));
            }

            if (request.getAttendeePlanned() < 0 || request.getAttendeeActual() < 0 || request.getAttendeeAccepted() < 0) {
                return ResponseEntity.ok(new CreateClassResponse("Attendee number cannot be negative!", "Failure", null));
            }

            classInfo = Class.builder()
                    .classId(request.getClassCode())
                    .className(request.getNameClass())
                    .duration(duration + " days")
                    .attendee(request.getAttendee())
                    .startDate(startDate)
                    .endDate(endDate)
                    .modifiedDate(LocalDate.now())
                    .attendeeAccepted(request.getAttendeeAccepted())
                    .attendeePlanned(request.getAttendeePlanned())
                    .attendeeActual(request.getAttendeeActual())
                    .createdDate(LocalDate.now())
                    .createdBy(requester.getName())
                    .modifiedBy(requester.getName())
                    .timeFrom(Time.valueOf(timeFromString))
                    .timeTo(Time.valueOf(timeToString))
                    .status("Planning")
                    .trainingProgramCode(trainingProgram)
                    .location(request.getLocation())
                    .build();

            /**
             *
             */
            for (int i = 0; i < request.getListDay().size(); i++) {
                String[] Date = request.getListDay().get(i).split("-");
                ClassLearningDay classLearningDay = ClassLearningDay.builder()
                        .classId(classInfo)
                        .enrollDate(LocalDate.parse(request.getListDay().get(i)))
                        .date(Integer.parseInt(Date[2]))
                        .year(Integer.parseInt(Date[0]))
                        .month(Integer.parseInt(Date[1]))
                        .timeFrom(Time.valueOf(timeFromString))
                        .timeTo(Time.valueOf(timeToString))
                        .build();
                classLearningDays.add(classLearningDay);
            }

            for (int i = 0; i < request.getAdmin().size(); i++) {
                user = userRepository.findByEmail(request.getAdmin().get(i)).orElse(null);
                if (user != null) {
                    ClassUser classAdmin = ClassUser.builder()
                            .id(ClassUserCompositeKey.builder()
                                    .userId(user.getUserId())
                                    .classId(classInfo.getClassId())
                                    .build())
                            .classId(classInfo)
                            .userID(user)
                            .userType(user.getRole().getRole().name())
                            .build();
                    classUsers.add(classAdmin);
                } else {
                    return ResponseEntity.ok(new CreateClassResponse("This Admin: " + request.getAdmin().get(i) + " does not exist!", "Failure", null));
                }
            }

            for (int i = 0; i < request.getAttendeeList().size(); i++) {
                user = userRepository.findByEmail(request.getAttendeeList().get(i)).orElse(null);
                if (user != null) {
                    ClassUser classAttendee = ClassUser.builder()
                            .id(ClassUserCompositeKey.builder()
                                    .userId(user.getUserId())
                                    .classId(classInfo.getClassId())
                                    .build())
                            .classId(classInfo)
                            .userID(user)
                            .userType(user.getRole().getRole().name())
                            .build();
                    classUsers.add(classAttendee);
                } else {
                    return ResponseEntity.ok(new CreateClassResponse("This Attendee User: " + request.getAttendeeList().get(i) + " does not exist!", "Failure", null));
                }
            }

            for (int i = 0; i < request.getTrainer().size(); i++) {
                user = userRepository.findByEmail(request.getTrainer().get(i).getGmail()).orElse(null);
                if (user != null) {
                    for (int j = 0; j < request.getTrainer().get(i).getTopicCode().size(); j++) {
                        Syllabus syllabus = syllabusRepository.findById(request.getTrainer().get(i).getTopicCode().get(j)).orElse(null);
                        if (syllabus != null) {
                            UserClassSyllabus userClassSyllabus = UserClassSyllabus.builder()
                                    .userId(user)
                                    .topicCode(syllabus)
                                    .classCode(classInfo)
                                    .userType(user.getRole().getRole().name())
                                    .build();
                            userSyllabusList.add(userClassSyllabus);
                        } else {
                            return ResponseEntity.ok(new CreateClassResponse("This Syllabus: " + request.getTrainer().get(i).getTopicCode() + " does not exist!", "Failure", null));
                        }
                    }
                } else {
                    return ResponseEntity.ok(new CreateClassResponse("This Trainer: " + request.getTrainer().get(i).getGmail() + " does not exist!", "Failure", null));
                }
            }
            classRepository.save(classInfo);
            classLearningDayRepository.saveAll(classLearningDays);
            classUserRepository.saveAll(classUsers);
            userClassSyllabusRepository.saveAll(userSyllabusList);

            CreateClassDTO createClassDTO = CreateClassDTO.builder()
                    .classCode(classInfo.getClassId())
                    .nameClass(classInfo.getClassName())
                    .classTimeTo(request.getClassTimeTo())
                    .classTimeFrom(request.getClassTimeFrom())
                    .location(classInfo.getLocation())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .createdBy(requester.getName())
                    .attendee(request.getAttendee())
                    .attendeeAccepted(request.getAttendeeAccepted())
                    .attendeePlanned(request.getAttendeePlanned())
                    .attendeeActual(request.getAttendeeActual())
                    .trainingProgram(request.getTrainingProgram())
                    .listDay(request.getListDay())
                    .admin(request.getAdmin())
                    .attendeeList(request.getAttendeeList())
                    .trainer(request.getTrainer())
                    .build();
            log.info(String.valueOf(request));
            return ResponseEntity.ok(new CreateClassResponse("New Class created successfully!", "Success", createClassDTO));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.ok(new CreateClassResponse("An error occurred while trying to create Class. Error message: " + exception.getMessage(), "Failure", null));
        }
    }

    /**
     * Get list of class
     * Author: Nguyen Vinh Khang
     *
     * @return list of class
     */
    @Override
    public ResponseEntity<ResponseObject> getAllClass() {
        try {
            List<Class> classList = classRepository.getAll();
            if (classList.isEmpty()) {
                String msg = "List of class is currently empty!";
                return ResponseEntity.ok(new ResponseObject("Success", msg, null));
            } else {
                String msg = "List of class loaded successfully!";
                return ResponseEntity.ok(new ResponseObject("Success", msg, classList));
            }
        } catch (Exception exception) {
            String msg = "An error occurred while trying to create List of Class. Error message: " + exception.getMessage();
            return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
        }
    }


    /**
     * Search class by id
     * Author: Nguyen Vinh Khang
     *
     * @param classCode
     * @return Class
     */
    @Override
    public ResponseEntity<ResponseObject> getClassById(String classCode) {
        try {
            Optional<Class> optionalClass = classRepository.findById(classCode);
            if (!optionalClass.isPresent()) {
                String msg = "Class with Id: " + classCode + " is not found.";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            } else {
                String msg = "Class with Id: " + classCode + " is loaded successfully!";
                log.info(msg);
                return ResponseEntity.ok(new ResponseObject("Success", msg, optionalClass));
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            String msg = "An error occurred while trying to get Class by Id. Error message: " + ex.getMessage();
            return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
        }
    }

    /**
     * Author: Nguyen Vinh Khang
     *
     * @param classCode
     * @return full detail of class
     */
    public ResponseEntity<ResponseObject> getClassDetail(String classCode) {
        try {
            Class classDetail = classRepository.findById(classCode).orElse(null);
            if (classDetail != null) {

                List<String> listDay = new ArrayList<>();
                List<ClassLearningDay> classLearningDays = classDetail.getClassLearningDays().stream().toList();


                TrainingProgram trainingProgram = trainingProgramRepository.findById(classDetail.getTrainingProgramCode().getTrainingProgramCode()).orElse(null);
                List<TrainerDTO> trainerList = new ArrayList<>();
                List<User> trainers = new ArrayList<>();

                List<UserClassSyllabus> userClassSyllabuses = classDetail.getUserClassSyllabus().stream().toList();

                List<UserDTOClass> attendeeList = new ArrayList<>();
                List<UserDTOClass> adminList = new ArrayList<>();
                List<ClassUser> classUsers = classDetail.getClassUsers().stream().toList();

                List<SyllabusDTO> syllabusList = new ArrayList<>();
                List<TrainingProgramSyllabus> trainingProgramSyllabuses = trainingProgram.getTrainingProgramSyllabus().stream().toList();


                Map<Integer, List<String>> trainerSyllabusMap = new HashMap<>();

                for (int i = 0; i < classLearningDays.size(); i++) {
                    listDay.add(classLearningDays.get(i).getEnrollDate().toString());
                }

                for (int i = 0; i < userClassSyllabuses.size(); i++) {
                    List<String> syllabusCodeList;
                    if (trainerSyllabusMap.containsKey(userClassSyllabuses.get(i).getUserId().getUserId())) {
                        syllabusCodeList = trainerSyllabusMap.get(userClassSyllabuses.get(i).getUserId().getUserId());
                    } else {
                        syllabusCodeList = new ArrayList<>();
                        trainers.add(userClassSyllabuses.get(i).getUserId());
                    }
                    syllabusCodeList.add(userClassSyllabuses.get(i).getTopicCode().getTopicCode());
                    trainerSyllabusMap.put(userClassSyllabuses.get(i).getUserId().getUserId(), syllabusCodeList);
                }

                for (int i = 0; i < trainers.size(); i++) {
                    TrainerDTO trainerDTO = TrainerDTO.builder()
                            .userId(trainers.get(i).getUserId())
                            .userEmail(trainers.get(i).getEmail())
                            .syllabusList(trainerSyllabusMap.get(trainers.get(i).getUserId()))
                            .build();
                    trainerList.add(trainerDTO);
                }

                for (int i = 0; i < classUsers.size(); i++) {
                    if (classUsers.get(i).getUserType().equalsIgnoreCase("user")) {
                        UserDTOClass trainee = UserDTOClass.builder()
                                .gmail(classUsers.get(i).getUserID().getEmail())
                                .name(classUsers.get(i).getUserID().getName())
                                .id(classUsers.get(i).getUserID().getUserId())
                                .build();
                        attendeeList.add(trainee);
                    } else {
                        UserDTOClass admin = UserDTOClass.builder()
                                .id(classUsers.get(i).getUserID().getUserId())
                                .gmail(classUsers.get(i).getUserID().getEmail())
                                .name(classUsers.get(i).getUserID().getName())
                                .build();
                        adminList.add(admin);
                    }
                }
                for (int i = 0; i < trainingProgramSyllabuses.size(); i++) {
                    SyllabusDTO syllabusDTO = SyllabusDTO.builder()
                            .topicName(trainingProgramSyllabuses.get(i).getTopicCode().getTopicName())
                            .topicCode(trainingProgramSyllabuses.get(i).getTopicCode().getTopicCode())
                            .createdBy(trainingProgramSyllabuses.get(i).getTopicCode().getCreatedBy().getCreatedBy())
                            .publishStatus(trainingProgramSyllabuses.get(i).getTopicCode().getPublishStatus())
                            .createdDate(String.valueOf(trainingProgramSyllabuses.get(i).getTopicCode().getCreatedDate()))
                            .version(trainingProgramSyllabuses.get(i).getTopicCode().getVersion())
                            .build();
                    syllabusList.add(syllabusDTO);
                }
/*chưa hoàn thiện
                User creator = userRepository.findByEmail(classDetail.getCreatedBy()).orElse(null);
                User reviewer = userRepository.findByEmail(classDetail.getReview()).orElse(null);
                User approver = userRepository.findByEmail(classDetail.getApprove()).orElse(null);
                User modifier = userRepository.findByEmail(classDetail.getModifiedBy()).orElse(null);
                UserDTOClass modifierDTO = null;

                if (modifier!=null){
                   modifierDTO = UserDTOClass.builder()
                           .id(modifier.getUserId())
                           .gmail(modifier.getEmail())
                           .name(modifier.getName())
                           .build();
                }

                UserDTOClass created = UserDTOClass.builder()
                        .gmail(creator.getEmail())
                        .name(creator.getName())
                        .id(creator.getUserId())
                        .build();

                UserDTOClass review = UserDTOClass.builder()
                        .id(reviewer.getUserId())
                        .gmail(reviewer.getEmail())
                        .name(reviewer.getName())
                        .build();

                UserDTOClass approve = UserDTOClass.builder()
                        .name(approver.getName())
                        .gmail(approver.getEmail())
                        .id(approver.getUserId())
                        .build();
*/
                ClassDetailResponse classDetailResponse = ClassDetailResponse.builder()
                        .classLearningDays(listDay)
                        .classCode(classDetail.getClassId())
                        .nameClass(classDetail.getClassName())
                        .duration(classDetail.getDuration())
                        .status(classDetail.getStatus())
                        .classTimeFrom(String.valueOf(classDetail.getTimeFrom()))
                        .classTimeTo(String.valueOf(classDetail.getTimeTo()))
                        .location(classDetail.getLocation())
                        .startDate(String.valueOf(classDetail.getStartDate()))
                        .endDate(String.valueOf(classDetail.getEndDate()))
                        .createdBy(classDetail.getCreatedBy())
                        .review(classDetail.getReview())
                        .approve(classDetail.getApprove())
                        .attendee(classDetail.getAttendee())
                        .attendeePlanned(String.valueOf(classDetail.getAttendeePlanned()))
                        .attendeeAccepted(String.valueOf(classDetail.getAttendeeAccepted()))
                        .attendeeActual(String.valueOf(classDetail.getAttendeeActual()))
                        .createdDate(String.valueOf(classDetail.getCreatedDate()))
                        .modifiedBy(classDetail.getModifiedBy())
                        .modifiedDate(String.valueOf(classDetail.getModifiedDate()))
                        .deactivated(classDetail.isDeactivated())
                        .trainer(trainerList)
                        .attendeeList(attendeeList)
                        .admin(adminList)
                        .syllabusList(syllabusList)
                        .build();
                String msg = "Get class detail successful!";
                return ResponseEntity.ok(new ResponseObject("Success", msg, classDetailResponse));
            } else {
                String msg = "Class with Id: " + classCode + " does not existed!";
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception ex) {
            String msg = "An error occurred while trying to get Class details. Error message:" + ex.getMessage();
            return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
        }
    }


    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/6/2024 9:06 PM
     * @description: deactivate class
     * @update:
     */
    @Override
    public ResponseEntity<String> deactivateClass(String classCode) {
        try {
            Class existingclass = classRepository.findById(classCode).orElse(null);
            if (existingclass != null) {
                if (existingclass.isDeactivated()) {
                    return ResponseEntity.status(200).body("This class is already deactivated!");
                } else {
                    existingclass.setDeactivated(true);
                    classRepository.save(existingclass);
                    return ResponseEntity.status(200).body("This class is now deactivated successfully!");
                }
            } else {
                return ResponseEntity.status(400).body("This class with Id: " + classCode + " does not exist!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Some problem occurs when deactivate class with Id: " + classCode);
        }
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/6/2024 9:06 PM
     * @description: update class
     * @update:
     */
    @Override
    public UpdateClassResponse updateClass(UpdateClassDTO updateClassRequest, String classCode) {
        try {
            Class existingClass = classRepository.findById(classCode).orElse(null);
            if (existingClass != null) {
                //Validate the day list:
                if (updateClassRequest.getListDay().isEmpty()) {
                    return UpdateClassResponse.builder()
                            .message("You must fill in Class learning day information!")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                //Check valid start and end day
                if (updateClassRequest.getStartDate().isBefore(existingClass.getStartDate())) {
                    return UpdateClassResponse.builder()
                            .message("The start Date for Class should be at least one day after today!")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                //Validate the learning day
                for (int i = 0; i < updateClassRequest.getListDay().size(); i++) {
                    LocalDate date = LocalDate.parse(updateClassRequest.getListDay().get(i));
                    if (date.isBefore(updateClassRequest.getStartDate())) {
                        return UpdateClassResponse.builder()
                                .message("The learning Date for Class should be start after start Date!")
                                .updatedClass(null)
                                .status(2)
                                .build();
                    } else if (date.isAfter(updateClassRequest.getEndDate())) {
                        return UpdateClassResponse.builder()
                                .message("The learning Date for this Class should be end before end Date!")
                                .updatedClass(null)
                                .status(2)
                                .build();
                    }
                }

                //Validate the attendee list:
                if (updateClassRequest.getAttendeeList().isEmpty()) {
                    return UpdateClassResponse.builder()
                            .message("You must fill in the Student information!")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                //Validate the admin list:
                if (updateClassRequest.getAdmin().isEmpty()) {
                    return UpdateClassResponse.builder()
                            .message("You must fill in the Admin information!")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                //Validate the trainer list:
                if (updateClassRequest.getTrainer().isEmpty()) {
                    return UpdateClassResponse.builder()
                            .message("You must fill in the Trainer information!")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }


                //Update class information from request
                existingClass.setClassName(updateClassRequest.getClassName());
                existingClass.setDuration(updateClassRequest.getDuration());
                existingClass.setStartDate(updateClassRequest.getStartDate());
                existingClass.setEndDate(updateClassRequest.getEndDate());
                existingClass.setAttendeeActual(Integer.parseInt(updateClassRequest.getAttendeeActual()));
                existingClass.setAttendee(updateClassRequest.getAttendee());
                existingClass.setAttendeePlanned(Integer.parseInt(updateClassRequest.getAttendeePlanned()));
                existingClass.setTimeFrom(Time.valueOf((updateClassRequest.getClassTimeFrom())));
                existingClass.setTimeTo(Time.valueOf((updateClassRequest.getClassTimeTo())));
                existingClass.setLocation(updateClassRequest.getLocation());
                existingClass.setStatus(updateClassRequest.getStatus());
                existingClass.setModifiedDate(LocalDate.now());

                //Update class into database
                //Class updatedClass = classRepository.save(existingClass);

                List<ClassUser> classuserList = classUserRepository.findByClassId_ClassId(classCode);
                List<UserClassSyllabus> usersyllabusList = userClassSyllabusRepository.findByClassCode_ClassId(classCode);
                List<ClassLearningDay> classlearningdayList = classLearningDayRepository.findByClassId_ClassId(classCode);


                classLearningDayRepository.deleteAll(classlearningdayList);
                classUserRepository.deleteAll(classuserList);

//                    userClassSyllabusRepository.deleteInBatch(usersyllabusList);
                userClassSyllabusRepository.deleteAll(usersyllabusList);
                userClassSyllabusRepository.flush();

                List<ClassUser> classUserList = new ArrayList<>();
                List<UserClassSyllabus> userSyllabusList = new ArrayList<>();
                List<ClassLearningDay> classLearningDayList = new ArrayList<>();
                User user;

                for (int i = 0; i < updateClassRequest.getListDay().size(); i++) {
                    LocalDate date = LocalDate.parse(updateClassRequest.getListDay().get(i));
                    String[] getDate = updateClassRequest.getListDay().get(i).split("-");
                    ClassLearningDay learnDay = ClassLearningDay.builder()
                            .classId(existingClass)
                            .date(Integer.parseInt(getDate[2]))
                            .month(Integer.parseInt(getDate[1]))
                            .year(Integer.parseInt(getDate[0]))
                            .enrollDate(date)
                            .timeFrom(existingClass.getTimeFrom())
                            .timeTo(existingClass.getTimeTo())
                            .build();
                    classLearningDayList.add(learnDay);
                }

                for (int i = 0; i < updateClassRequest.getAttendeeList().size(); i++) {
                    Optional<User> userOptional = userRepository.findByEmail(updateClassRequest.getAttendeeList().get(i));
                    user = userOptional.orElse(null);
                    if (user != null) {
                        boolean userExist = false;
                        for (ClassUser existingUser : classUserList) {
                            if (existingUser.getUserID().getUserId() == user.getUserId()) {
                                userExist = true;
                                break;
                            }
                        }

                        if (!userExist) {
                            ClassUser classUser = ClassUser.builder()
                                    .id(ClassUserCompositeKey.builder()
                                            .userId(user.getUserId())
                                            .classId(existingClass.getClassId())
                                            .build())
                                    .userID(user)
                                    .classId(existingClass)
                                    .userType(user.getRole().getRole().name())
                                    .build();
                            classUserList.add(classUser);
                        }
                    }
                }

                for (int i = 0; i < updateClassRequest.getAdmin().size(); i++) {
                    Optional<User> adminOptional = userRepository.findByEmail(updateClassRequest.getAdmin().get(i));
                    user = adminOptional.orElse(null);
                    if (user != null) {
                        ClassUser classAdmin = ClassUser.builder()
                                .id(ClassUserCompositeKey.builder()
                                        .userId(user.getUserId())
                                        .classId(existingClass.getClassId())
                                        .build())
                                .userID(user)
                                .classId(existingClass)
                                .userType(user.getRole().getRole().name())
                                .build();
                        classUserList.add(classAdmin);
                    }
                }

                for (int i = 0; i < updateClassRequest.getTrainer().size(); i++) {
                    Optional<User> trainerOptional = userRepository.findByEmail(updateClassRequest.getTrainer().get(i).getGmail());
                    User trainer = trainerOptional.orElse(null);
                    if (trainer != null) {
                        for (int j = 0; j < updateClassRequest.getTrainer().get(i).getClassCode().size(); j++) {
                            Optional<Syllabus> syllabusOptional = syllabusRepository.findById(updateClassRequest.getTrainer().get(i).getClassCode().get(j));
                            Syllabus syllabus = syllabusOptional.orElse(null);
                            if (syllabus != null) {
                                UserClassSyllabus userClassSyllabus = UserClassSyllabus.builder()
                                        .classCode(existingClass)
                                        .topicCode(syllabus)
                                        .userId(trainer)
                                        .userType(trainer.getRole().getRole().name())
                                        .build();
                                userSyllabusList.add(userClassSyllabus);
                            }
                        }
                    }
                }

                User moder = userRepository.findByEmail(updateClassRequest.getModerEmail()).get();
                existingClass.setModifiedBy(moder.getEmail());

                classLearningDayRepository.saveAll(classLearningDayList);
                classUserRepository.saveAll(classUserList);
                userClassSyllabusRepository.saveAll(userSyllabusList);

                existingClass.setTrainingProgramCode(trainingProgramRepository.findById(Integer.parseInt(updateClassRequest.getTrainingProgram())).get());
                classRepository.save(existingClass);


                //Format Date and Time
                String formatTimeFrom = existingClass.getTimeFrom().toString();
                formatTimeFrom = formatTimeFrom.substring(0, formatTimeFrom.lastIndexOf(":"));

                String formatTimeTo = existingClass.getTimeTo().toString();
                formatTimeTo = formatTimeTo.substring(0, formatTimeTo.lastIndexOf(":"));

                //Return DTO
                UpdatedClassDTO updateClassDTO = UpdatedClassDTO.builder()
                        .classCode(existingClass.getClassId())
                        .className(existingClass.getClassName())
                        .duration(existingClass.getDuration())
                        .attendee(existingClass.getAttendee())
                        .classTimeFrom(formatTimeFrom)
                        .classTimeTo(formatTimeTo)
                        .attendeeAccepted(Integer.toString(existingClass.getAttendeeAccepted()))
                        .attendeeActual(Integer.toString(existingClass.getAttendeeActual()))
                        .attendeePlanned(Integer.toString(existingClass.getAttendeePlanned()))
                        .startDate(existingClass.getStartDate())
                        .endDate(existingClass.getEndDate())
                        .location(existingClass.getLocation())
                        .status(existingClass.getStatus())
                        .build();

                log.info("The Class with Id: " + existingClass.getClassId() + " has been updated successfully!");
                return UpdateClassResponse.builder()
                        .status(0)
                        .updatedClass(updateClassDTO)
                        .message("The Class with Id: " + existingClass.getClassId() + " has been updated successfully!")
                        .build();
            } else {
                //class does not exist
                log.info("The Class with Id: " + classCode + " does not exist!");
                return UpdateClassResponse.builder()
                        .status(1)
                        .updatedClass(null)
                        .message("The Class with Id: " + classCode + " does not exist!")
                        .build();
            }
        } catch (Exception e) {
            log.info("An error occurred while trying to update Class. Error message:" + e.getMessage());
            return UpdateClassResponse.builder()
                    .status(2)
                    .updatedClass(null)
                    .message("An error occurred while trying to update Class. Exception!" + e.getMessage())
                    .build();
        }
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/8/2024 9:34 PM
     * @description: update class 3
     * @update:
     */

    @Override
    public UpdateClass3Response updateClass3(UpdateClass3Request updateClass3Request) {
        try {
            boolean status = false;
            status = updateClass3Request.isDeleted();
            String topicCode = null;
            topicCode = updateClass3Request.getTopicCode();

            int trainingProgramCode = updateClass3Request.getTrainingProgramCode();

            var syllabus = syllabusRepository.findById(topicCode).orElse(null);
            var trainingProgram = trainingProgramRepository.findById(trainingProgramCode).orElse(null);

            TrainingProgramSyllabus trainingProgramSyllabus = trainingProgramSyllabusRepository.findByIdTopicCodeAndIdTrainingProgramCode(topicCode, trainingProgramCode).orElse(null);

            TrainingProgramSyllabus trainingProgramSyllabusUpdate = TrainingProgramSyllabus.builder()
                    .id(SyllabusTrainingProgramCompositeKey.builder()
                            .topicCode(topicCode)
                            .trainingProgramCode(trainingProgramCode)
                            .build())
                    .topicCode(syllabus)
                    .deleted(status)
                    .trainingProgramCode(trainingProgram)
                    .build();
            //update database if syllabus exists
            if (trainingProgramSyllabus != null) {
                TrainingProgramSyllabus trainingProgramSyllabusResponse = trainingProgramSyllabusRepository.save(trainingProgramSyllabusUpdate);
                log.info("Class Syllabus has been updated successfully!");
                return UpdateClass3Response.builder()
                        .status("Class Syllabus has been updated successfully!")
                        .updatedClass3(trainingProgramSyllabusUpdate)
                        .build();
                //update database if syllabus not exists
            } else {
                //TrainingProgramSyllabus trainingProgramSyllabus1Response = trainingProgramSyllabusRepository.save(trainingProgramSyllabusUpdate);
                log.info("Class Syllabus has been updated successfully!");
                return UpdateClass3Response.builder()
                        .status("Success")
                        .updatedClass3(trainingProgramSyllabusUpdate)
                        .build();
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to update Class Syllabus. Error message: " + e);
            return UpdateClass3Response.builder()
                    .status("Failure")
                    .updatedClass3(null)
                    .build();
        }
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/11/2024 12:26 PM
     * @description: update class calendar
     * @update:
     */

    @Override
    public ResponseObject updateClassLearningDay(UpdateCalendarRequest updateCalendarRequest) {
        try {
            String classId = updateCalendarRequest.getClassId();
            LocalDate enrollDate = updateCalendarRequest.getEnrollDate();
            String timeFromBefore = updateCalendarRequest.getTimeFrom();
            String timeToBefore = updateCalendarRequest.getTimeTo();
            String value = updateCalendarRequest.getValue();

            Class classExist = classRepository.findByClassCode(classId);

            ClassLearningDay classLearningDayExist = classLearningDayRepository.findByClassIdAndAndEnrollDate(classExist, enrollDate);

            if (classLearningDayExist != null) {
                Time timeFrom = Time.valueOf(timeFromBefore);
                Time timeTo = Time.valueOf(timeToBefore);
                if (timeFrom.after(timeTo)) {
                    return ResponseObject.builder()
                            .status("Failure")
                            .message("The timeFrom must begin before timeTo!")
                            .payload(null)
                            .build();
                }
                if (value.equals("Only")) {
                    classLearningDayExist.setTimeFrom(timeFrom);
                    classLearningDayExist.setTimeTo(timeTo);
                    classLearningDayRepository.save(classLearningDayExist);
                    log.info("Updating Class learning day...");
                    return ResponseObject.builder()
                            .status("Success")
                            .message("The class learning day has been updated successfully!")
                            .payload(classLearningDayExist)
                            .build();
                } else if (value.isBlank()) {
                    log.error("An error occurred while trying to update Class learning day.");
                    return ResponseObject.builder()
                            .status("Failure")
                            .message("An error occurred while trying to update class learning day!")
                            .payload(null)
                            .build();
                } else if (value.equals("All")) {
                    List<ClassLearningDay> classLearningDayList = classLearningDayRepository.findByClassId_ClassId(classId);
                    for (int i = 0; i < classLearningDayList.size(); i++) {
                        classLearningDayList.get(i).setTimeFrom(timeFrom);
                        classLearningDayList.get(i).setTimeTo(timeTo);
                    }
                    classLearningDayRepository.saveAll(classLearningDayList);
                    log.info("Updating Class learning day...");
                    return ResponseObject.builder()
                            .status("Success")
                            .message("All class learning day has been updated successfully!")
                            .payload(classLearningDayExist)
                            .build();
                }
                log.info("Updating Class learning day...");
                return ResponseObject.builder()
                        .status("Failure")
                        .message("The value must be \"Only\" or \"All\"!")
                        .payload(null)
                        .build();
            } else {
                log.info("The Class learning day does not exist!");
                return ResponseObject.builder()
                        .status("Failure")
                        .message("The class learning day does not exist!")
                        .payload(null)
                        .build();
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to update Class learning day. Error message: " + e);
            return ResponseObject.builder()
                    .status("Failure")
                    .message("An error occurred while trying to update class learning day!")
                    .payload(null)
                    .build();
        }
    }


    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/11/2024 10:52 PM
     * @description: view training calendar by day
     * @update:
     */

//    @Override
//    public ResponseEntity<ResponseObject> getCalendarbyDay(LocalDate currentDate) {
//        try {
//            //Format date:
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//            LocalDate localDate = LocalDate.parse(currentDate, formatter);
//            List<ClassLearningDay> learningDayList = classLearningDayRepository.findByEnrollDate(currentDate);
//            if (!learningDayList.isEmpty()) {
//                List<CalendarDayResponse> calendarDayResponseList = classRepository.getCalendarByDay(currentDate);
//                List<CalendarDayResponse> calendarDayResponse = new ArrayList<>();
//                CalendarDayResponse c = null;
//                for (int i = 0; i < calendarDayResponseList.size(); i++) {
//                    c = CalendarDayResponse.builder()
//                            .classCode(calendarDayResponseList.get(i).getClassCode())
//                            .timeFrom(calendarDayResponseList.get(i).getTimeFrom())
//                            .timeTo(calendarDayResponseList.get(i).getTimeTo())
//                            .enrollDate(calendarDayResponseList.get(i).getEnrollDate())
//                            .userType(calendarDayResponseList.get(i).getUserType())
//                            .build();
//                    calendarDayResponse.add(c);
//                }
//                return ResponseEntity.ok(new ResponseObject("Successful", "List of classroom", calendarDayResponse));
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("Not successful", "Couldn't find any calendar!", null));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Failed", "Some problem when find any calendar in this day", e.getMessage()));
//        }
//    }

    /**
     * Search class by name
     * Author: Nguyen Vinh Khang
     *
     * @param name
     * @return list of class
     */
    @Override
    public ResponseEntity<ResponseObject> getClassByName(String name) {
        try {
            List<Class> listClass = classRepository.getClassByName(name);
            if (listClass.isEmpty()) {
                String msg = "Class(es) with Name: " + name + " is not found!";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            } else {
                String msg = "Class with Name: " + name + " is loaded successfully";
                log.info(msg);
                return ResponseEntity.ok(new ResponseObject("Success", msg, listClass));
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            String msg = "An error occurred while trying to get Class information by Name. Error message: " + ex.getMessage();
            return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
        }
    }

    /*
     Duplicate object when email is null. (Need to fix in future).
     */
    @Override
    @JsonIgnore
    public ResponseEntity<ResponseObject> filterClass(List<String> location, List<String> classTime, String startDate, String endDate, List<String> status, String trainer) {
        try {
            LocalDate LDstartDate;
            LocalDate LDendDate;
            if (location.isEmpty()) {
                List<Class> classList = classRepository.getAll();
                for (int i = 0; i < classList.size(); i++) {
                    location.add(classList.get(i).getLocation());
                }
            }
            if (classTime.isEmpty()) {
                classTime.add("Morning");
                classTime.add("Noon");
                classTime.add("Night");
            }
            if (startDate == "") {
                LDstartDate = LocalDate.parse("1900-01-01");

            } else {
                LDstartDate = LocalDate.parse(startDate);
            }
            if (endDate == "") {
                LDendDate = LocalDate.parse("2100-01-01");
            } else {
                LDendDate = LocalDate.parse(endDate);
            }
            if (status.isEmpty()) {
                status.add("Planning");
                status.add("Scheduled");
                status.add("Opening");
                status.add("Closed");
            }
            if (trainer == "") {
                trainer = "@";
            }
            //int trainerID = userRepository.findByEmail(trainer).orElse(null).getUserId();
            Time timeFrom = null;
            Time timeTo = null;
            List<Object> classFilter = new ArrayList<>();
            List<Object> classes;
            for (int i = 0; i < classTime.size(); i++) {
                switch (classTime.get(i)) {
                    case "Morning":
                        timeFrom = Time.valueOf("08:00:00");
                        timeTo = Time.valueOf("12:00:00");
                        classes = classRepository.classFilter(location, LDstartDate, LDendDate, timeFrom, timeTo, status, trainer);
                        classFilter.add(classes);
                        break;
                    case "Noon":
                        timeFrom = Time.valueOf("13:00:00");
                        timeTo = Time.valueOf("17:00:00");
                        classes = classRepository.classFilter(location, LDstartDate, LDendDate, timeFrom, timeTo, status, trainer);
                        classFilter.add(classes);
                        break;
                    case "Night":
                        timeFrom = Time.valueOf("18:00:00");
                        timeTo = Time.valueOf("21:00:00");
                        classes = classRepository.classFilter(location, LDstartDate, LDendDate, timeFrom, timeTo, status, trainer);
                        classFilter.add(classes);
                        break;
                }
            }
            if (classFilter.isEmpty()) {
                String msg = "Class(es) matching the filters is not found.";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            } else {
                String msg = "Class(es) matching the filters has been found!";
                log.info(classFilter.toString());
                return ResponseEntity.ok(new ResponseObject("Success", msg, classFilter));
            }
        } catch (Exception ex) {
            String msg = "An error occurred while trying to filter Class(es). Error message: " + ex.getMessage();
            log.error(msg);
            return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
        }
    }

}
