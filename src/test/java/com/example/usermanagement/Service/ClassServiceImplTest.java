package com.example.usermanagement.Service;

import com.example.usermanagement.Response.UserDTOImp;
import com.example.usermanagement.dto.Request.CreateClassDTO;
import com.example.usermanagement.dto.Request.TrainerSyllabusRequest;
import com.example.usermanagement.dto.Request.UpdateCalendarRequest;
import com.example.usermanagement.dto.Request.UpdateClass3Request;
import com.example.usermanagement.dto.Response.CreateClassResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.Response.UpdateClass3Response;
import com.example.usermanagement.dto.Response.UpdateClassResponse;
import com.example.usermanagement.dto.TrainerSyllabusDTO;
import com.example.usermanagement.dto.UpdateClassDTO;
import com.example.usermanagement.entity.Class;
import com.example.usermanagement.entity.*;
import com.example.usermanagement.entity.composite_key.ClassUserCompositeKey;
import com.example.usermanagement.entity.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.*;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service_implements.ClassServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

import static com.example.usermanagement.enums.Permission.*;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassServiceImplTest {
    User user = User.builder()
            .userId(1)
            .name("CTV")
            .email("ctv@gmail.com")
            .password("1")
            .role(UserPermission.builder()
                    .role(Role.SUPER_ADMIN)
                    .syllabus(
                            List.of(
                                    SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                    .trainingProgram(
                            List.of(
                                    TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                    .userClass(
                            List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                    .userManagement(
                            List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
                    .learningMaterial(List.of())
                    .build())
            .build();
    //Training program data
    TrainingProgram trainingProgram = TrainingProgram.builder()
            .trainingProgramCode(2)
            .name("OJT")
            .userID(user)
            .startDate(LocalDate.of(2024, 1, 1))
            .duration(30)
            .status("active")
            .createdBy("admin")
            .createdDate(LocalDate.now())
            .build();
    //Class data
    Class class2 = Class.builder()
            .classId("CS107")
            .trainingProgramCode(trainingProgram)
            .className("Java03")
            .duration("2")
            .status("active")
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 8, 5))
            .createdBy("admin")
            .createdDate(LocalDate.now())
            .deactivated(false)
            .attendee("Group 2")
            .attendeePlanned(30)
            .attendeeAccepted(25)
            .attendeeActual(20)
            .location("MIT")
            .build();
    //Class user data
    ClassUser classUser = ClassUser.builder()
            .id(new ClassUserCompositeKey(user.getUserId(), class2.getClassId()))
            .classId(class2)
            .userID(user)
            .userType("TRAINER")
            .build();
    //Class learning day data
    ClassLearningDay classLearningDay = ClassLearningDay.builder()
            .date(1)
            .month(1)
            .year(2024)
            .enrollDate(LocalDate.of(2024, 1, 1))
            .timeFrom(Time.valueOf("09:00:00"))
            .timeTo(Time.valueOf("12:00:00"))
            .classId(class2)
            .build();
    //Syllabus data
    Syllabus syllabus = Syllabus.builder()
            .topicCode("Java")
            .topicName("Java Spring Boot")
            .duration(30)
            .technicalGroup("FPT")
            .version("1")
            .trainingAudience(30)
            .topicOutline("java outline")
            .trainingPrinciples("principles")
            .priority("high")
            .publishStatus("public")
            .createdBy(user)
            .createdDate(LocalDate.now())
            .courseObjective("Java SB")
            .deleted(false)
            .assignmentLab("as")
            .conceptLecture("simple")
            .guideReview("guide")
            .testQuiz("testquiz")
            .exam("exam")
            .quiz("quiz")
            .assignment("assignment")
            .final_("final")
            .finalTheory("finaltheory")
            .finalPractice("finalpractice")
            .gpa("gpa")
            .build();
    //Training program syllabus data
    TrainingProgramSyllabus trainingProgramSyllabus = TrainingProgramSyllabus.builder()
            .id(new SyllabusTrainingProgramCompositeKey(syllabus.getTopicCode(), trainingProgram.getTrainingProgramCode()))
            .topicCode(syllabus)
            .trainingProgramCode(trainingProgram)
            .deleted(false)
            .build();
    UserClassSyllabus userClassSyllabus = UserClassSyllabus.builder()
            .userId(user)
            .classCode(class2)
            .topicCode(syllabus)
            .build();
    UserDTOImp userDTO = UserDTOImp.builder()
            .userId(1)
            .name("CTV")
            .email("ctv@gmail.com")
            .role(UserPermission.builder()
                    .role(Role.SUPER_ADMIN)
                    .syllabus(
                            List.of(
                                    SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                    .trainingProgram(
                            List.of(
                                    TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                    .userClass(
                            List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                    .userManagement(
                            List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
                    .learningMaterial(List.of())
                    .build())
            .build();
    UserPermission userPermission = UserPermission.builder()
            .role(Role.SUPER_ADMIN)
            .syllabus(List.of())
            .trainingProgram(List.of())
            .userClass(List.of())
            .userManagement(List.of(USER_CREATE))
            .learningMaterial(List.of())
            .build();
    @InjectMocks
    private ClassServiceImpl classService;
    @Mock
    private ClassRepository classRepository;
    @Mock
    private JWTService jwtService;
    @Mock
    private ClassLearningDayRepository classLearningDayRepository;
    @Mock

    private UserRepository userRepository;
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private TrainingProgramSyllabusRepository trainingProgramSyllabusRepository;
    @Mock
    private ClassUserRepository classUserRepository;
    @Mock
    private UserClassSyllabusRepository userClassSyllabusRepository;
    @Mock
    private UserPermissionRepository userPermissionRepository;
    //User data
    @Mock
    private SyllabusRepository syllabusRepository;

    @Test
    void createClass_ReturnCreateClassResponse_Success() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        List<ClassLearningDay> classLearningDays = new ArrayList<>();
        List<ClassUser> classUsers = new ArrayList<>();
        List<UserClassSyllabus> userSyllabusList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add(0, "2024-12-12");
        List<String> list1 = new ArrayList<>();
        list1.add("ctv@gmail.com");
        List<String> list2 = new ArrayList<>();
        list2.add("ctv@gmail.com");

//        list2.add("admin@gmail.com");
        List<String> topicList = new ArrayList<>(Arrays.asList("Java"));
        TrainerSyllabusRequest trainerSyllabusRequest = new TrainerSyllabusRequest("ctv@gmail.com", topicList);
        List<TrainerSyllabusRequest> list3 = new ArrayList<>();
        list3.add(trainerSyllabusRequest);

        CreateClassDTO createClassDTO = CreateClassDTO.builder()
                .classCode("CS107")
                .nameClass("IT")
                .classTimeTo("13:00:00")
                .classTimeFrom("17:00:00")
                .location("HCM")
                .startDate(String.valueOf(LocalDate.now().plusDays(1)))
                .endDate(String.valueOf(LocalDate.now().plusDays(4)))
                .createdBy("ADMIN")
                .attendee("Fresher")
                .attendeeAccepted(23)
                .attendeePlanned(23)
                .attendeeActual(24)
                .trainingProgram(2)
                .listDay(list)
                .admin(list1)
                .attendeeList(list2)
                .trainer(list3)
                .build();


        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));

        when(syllabusRepository.findById(anyString())).thenReturn(Optional.of(syllabus));
        when(userRepository.findUserByEmail(user.getName())).thenReturn(Optional.of(userDTO));
        when(classRepository.findById(class2.getClassId())).thenReturn(Optional.empty());
        when(trainingProgramRepository.findById(trainingProgram.getTrainingProgramCode())).thenReturn(Optional.of(trainingProgram));
        when(jwtService.extractUserEmail(token)).thenReturn(user.getName());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(classLearningDayRepository.saveAll(any())).thenReturn(classLearningDays);
        when(classUserRepository.saveAll(any())).thenReturn(classUsers);
        when(userClassSyllabusRepository.saveAll(any())).thenReturn(userSyllabusList);


        ResponseEntity<CreateClassResponse> response = classService.createClass(createClassDTO);

        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("New Class created successfully!");


    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:12 PM
     * @description: test deactivate class that success
     * @update:
     */
    @Test
    public void testDeactivateClass_Successfullly() {

        String classCode = "CS107";
        Class existingClass = class2;
        existingClass.setDeactivated(false);

        when(classRepository.findById(classCode)).thenReturn(Optional.of(class2));
        ResponseEntity<String> response = classService.deactivateClass(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("This class is now deactivated successfully!");
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:13 PM
     * @description: test deactivate class that class does not exist
     * @update:
     */
    @Test
    public void testDeactivateClass_isNull() {

        String classCode = "CS107";
        Class existingClass = class2;
        existingClass.setDeactivated(false);

        when(classRepository.findById(classCode)).thenReturn(empty());
        ResponseEntity<String> response = classService.deactivateClass(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo("This class with Id: " + classCode + " does not exist!");
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:13 PM
     * @description: test deactivate class that class deactivated before
     * @update:
     */
    @Test
    public void testDeactivateClass_isDeactivatedBefore() {

        String classCode = "CS107";
        Class existingClass = class2;
        existingClass.setDeactivated(true);

        when(classRepository.findById(classCode)).thenReturn(Optional.of(class2));
        ResponseEntity<String> response = classService.deactivateClass(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo("This class is already deactivated!");
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:14 PM
     * @description: test update class learning day that class learning day does not exist
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_isNull() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 2))
                .timeFrom("09:00:00")
                .timeTo("11:00:00")
                .value("Only")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(null);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getMessage()).isEqualTo("The class learning day does not exist!");
        Assertions.assertThat(response.getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:14 PM
     * @description: test update class learning day that success with only value
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_Successfully() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 1))
                .timeFrom("09:00:00")
                .timeTo("11:00:00")
                .value("Only")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(classLearningDay);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getMessage()).isEqualTo("The class learning day has been updated successfully!");
        Assertions.assertThat(response.getPayload()).isEqualTo(classLearningDay);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:33 PM
     * @description: test update class learning day that time to is before time from
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_TimeToBeforeTimeFrom() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 1))
                .timeFrom("09:00:00")
                .timeTo("08:30:00")
                .value("Only")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(classLearningDay);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getMessage()).isEqualTo("The timeFrom must begin before timeTo!");
        Assertions.assertThat(response.getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 1:58 PM
     * @description: test update class learning day that time from or time to is null
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_TimeFormatIsNull() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 1))
                .timeFrom(null)
                .timeTo("08:30:00")
                .value("Only")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(classLearningDay);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getMessage()).isEqualTo("An error occurred while trying to update class learning day!");
        Assertions.assertThat(response.getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 2:17 PM
     * @description: test update class learning day with all class
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_All_Successfully() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 1))
                .timeFrom("09:00:00")
                .timeTo("11:00:00")
                .value("All")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(classLearningDay);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getMessage()).isEqualTo("All class learning day has been updated successfully!");
        Assertions.assertThat(response.getPayload()).isEqualTo(classLearningDay);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 2:47 PM
     * @description: test update class learning day that null value
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_NullException() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 1))
                .timeFrom("09:00:00")
                .timeTo("11:00:00")
                .value("")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(classLearningDay);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getMessage()).isEqualTo("An error occurred while trying to update class learning day!");
        Assertions.assertThat(response.getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 2:44 PM
     * @description: test update class learning day that value is difference "Only" or "All"
     * @update:
     */
    @Test
    public void testUpdateClassLearningDay_FailValue() {
        UpdateCalendarRequest updateCalendarRequest = UpdateCalendarRequest
                .builder()
                .classId("CS107")
                .enrollDate(LocalDate.of(2024, 1, 1))
                .timeFrom("09:00:00")
                .timeTo("11:00:00")
                .value("Ok")
                .build();
        String classCode = "CS107";
        when(classRepository.findByClassCode(classCode)).thenReturn(mock(Class.class));
        when(classLearningDayRepository.findByClassIdAndAndEnrollDate(any(Class.class), any(LocalDate.class))).thenReturn(classLearningDay);

        ResponseObject response = classService.updateClassLearningDay(updateCalendarRequest);

        Assertions.assertThat(response.getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getMessage()).isEqualTo("The value must be \"Only\" or \"All\"!");
        Assertions.assertThat(response.getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 2:54 PM
     * @description: test get all class that successfully with it is empty.
     * @update:
     */
    @Test
    public void testGetAllClass_Successfully_EmptyList() {
        List<Class> listClass = new ArrayList<>();
        when(classRepository.getAll()).thenReturn(listClass);

        ResponseEntity<ResponseObject> response = classService.getAllClass();

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("List of class is currently empty!");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 3:06 PM
     * @description: test get all class successfully without empty list
     * @update:
     */
    @Test
    public void testGetAllClass_SuccessfullyList() {
        List<Class> listClass = new ArrayList<>();
        listClass.add(class2);
        when(classRepository.getAll()).thenReturn(listClass);

        ResponseEntity<ResponseObject> response = classService.getAllClass();

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("List of class loaded successfully!");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(listClass);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 3:34 PM
     * @description: test get class by id that not found the class
     * @update:
     */
    @Test
    public void testGetClassById_IsNotFound() {
        String classCode = "CS107";
        when(classRepository.findById(classCode)).thenReturn(Optional.empty());

        ResponseEntity<ResponseObject> response = classService.getClassById(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Class with Id: " + classCode + " is not found.");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 3:34 PM
     * @description: test get class by id that success
     * @update:
     */
    @Test
    public void testGetClassById_Successfully() {
        String classCode = "CS107";
        when(classRepository.findById(classCode)).thenReturn(Optional.of(class2));

        ResponseEntity<ResponseObject> response = classService.getClassById(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Class with Id: " + classCode + " is loaded successfully!");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(Optional.of(class2));
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 4:05 PM
     * @description: test get class by id with classId is null
     * @update:
     */
    @Test
    public void testGetClassById_ClassIdIsNull() {
        String classCode = null;
        when(classRepository.findById(classCode)).thenReturn(Optional.empty());

        ResponseEntity<ResponseObject> response = classService.getClassById(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Class with Id: " + classCode + " is not found.");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(null);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/26/2024 4:34 PM
     * @description: test get all class's exception
     * @update:
     */
    @Test
    void testGetAllClass_GetException() {
        when(classRepository.getAll()).thenThrow(new RuntimeException(""));

        ResponseEntity<ResponseObject> response = classService.getAllClass();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failure", response.getBody().getStatus());
        assertEquals("An error occurred while trying to create List of Class. Error message: ", response.getBody().getMessage());
        assertNull(response.getBody().getPayload());
    }

    @Test
    void testGetClassByName_Success() {
        String className = "Java03";
        List<Class> classList = new ArrayList<>();
        classList.add(class2);
        when(classRepository.getClassByName(className)).thenReturn(classList);
        ResponseEntity<ResponseObject> response = classService.getClassByName(className);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Class with Name: " + className + " is loaded successfully");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(classList);
    }

    @Test
    void testGetClassByName_isNotFound() {
        String className = "Java03";
        when(classRepository.getClassByName(className)).thenReturn(Collections.emptyList());
        ResponseEntity<ResponseObject> response = classService.getClassByName(className);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Class(es) with Name: " + className + " is not found!");
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(null);
    }

    @Test
    void testGetClassByName_exception() {
        String className = "Java03";
        when(classRepository.getClassByName(className)).thenThrow(NullPointerException.class);
        ResponseEntity<ResponseObject> response = classService.getClassByName(className);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
        assertEquals("An error occurred while trying to get Class information by Name. Error message: null", response.getBody().getMessage());
        Assertions.assertThat(response.getBody().getPayload()).isEqualTo(null);
    }

    @Test
    void testGetClassDetail_success() {
        Set<ClassLearningDay> classLearningDays = new HashSet<>();
        classLearningDays.add(classLearningDay);
        Set<UserClassSyllabus> userClassSyllabusSet = new HashSet<>();
        userClassSyllabusSet.add(userClassSyllabus);
        Set<ClassUser> classUserSet = new HashSet<>();
        classUserSet.add(classUser);
        Set<TrainingProgramSyllabus> trainingProgramSyllabusSet = new HashSet<>();
        trainingProgramSyllabusSet.add(trainingProgramSyllabus);

        trainingProgram.setTrainingProgramSyllabus(trainingProgramSyllabusSet);
        class2.setClassLearningDays(classLearningDays);
        class2.setUserClassSyllabus(userClassSyllabusSet);
        class2.setClassUsers(classUserSet);

        when(classRepository.findById(class2.getClassId())).thenReturn(Optional.of(class2));
        when(trainingProgramRepository.findById(2)).thenReturn(Optional.ofNullable(trainingProgram));
        ResponseEntity<ResponseObject> response = classService.getClassDetail(class2.getClassId());
        assertNotNull(response);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Get class detail successful!");
        assertNotNull(response.getBody().getPayload());
    }


    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/27/2024 9:12 PM
     * @description: test get class by id exception
     * @update:
     */
    @Test
    void testGetClassById_GetException() {
        String classCode = "CS107";
        when(classRepository.findById(classCode)).thenThrow(new NullPointerException());

        ResponseEntity<ResponseObject> response = classService.getClassById(classCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failure", response.getBody().getStatus());
        assertNull(response.getBody().getPayload());
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/27/2024 10:00 PM
     * @description: test deactivate class with exception
     * @update:
     */
    @Test
    public void testDeactivateClass_getException() {

        String classCode = "CS107";
        Class existingClass = class2;
        existingClass.setDeactivated(false);

        when(classRepository.findById(classCode)).thenThrow(new RuntimeException());
        ResponseEntity<String> response = classService.deactivateClass(classCode);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isEqualTo("Some problem occurs when deactivate class with Id: " + classCode);
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/27/2024 11:13 PM
     * @description: test update class 3 that success
     * @update:
     */
    @Test
    void testUpdateClass3_isSuccess() {
        String topic_code = syllabus.getTopicCode();
        int training_program_code = trainingProgram.getTrainingProgramCode();
        UpdateClass3Request updateClass3Request = UpdateClass3Request.builder()
                .deleted(true)
                .topicCode(topic_code)
                .trainingProgramCode(training_program_code)
                .build();

        TrainingProgramSyllabus trainingProgramSyllabusUpdate = TrainingProgramSyllabus.builder()
                .id(SyllabusTrainingProgramCompositeKey.builder()
                        .topicCode(updateClass3Request.getTopicCode())
                        .trainingProgramCode(updateClass3Request.getTrainingProgramCode())
                        .build())
                .topicCode(syllabus)
                .deleted(updateClass3Request.isDeleted())
                .trainingProgramCode(trainingProgram)
                .build();

        when(syllabusRepository.findById(topic_code)).thenReturn(Optional.of(syllabus));
        when(trainingProgramRepository.findById(training_program_code)).thenReturn(Optional.of(trainingProgram));
        when(trainingProgramSyllabusRepository.findByIdTopicCodeAndIdTrainingProgramCode(topic_code, training_program_code)).thenReturn(Optional.of(trainingProgramSyllabusUpdate));

        UpdateClass3Response response = classService.updateClass3(updateClass3Request);

        assertNotNull(response);
        assertEquals("Class Syllabus has been updated successfully!", response.getStatus());
    }


    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/27/2024 11:18 PM
     * @description: test update class 3 that get exception
     * @update:
     */
    @Test
    void testUpdateClass3_getException() {
        String topic_code = syllabus.getTopicCode();
        int training_program_code = trainingProgram.getTrainingProgramCode();
        UpdateClass3Request updateClass3Request = UpdateClass3Request.builder()
                .deleted(true)
                .topicCode(topic_code)
                .trainingProgramCode(training_program_code)
                .build();

        TrainingProgramSyllabus trainingProgramSyllabus1 = TrainingProgramSyllabus.builder()
                .id(new SyllabusTrainingProgramCompositeKey(topic_code, training_program_code))
                .topicCode(syllabus)
                .trainingProgramCode(trainingProgram)
                .deleted(false)
                .build();

        when(syllabusRepository.findById(topic_code)).thenReturn(Optional.of(syllabus));
        when(trainingProgramRepository.findById(training_program_code)).thenReturn(Optional.of(trainingProgram));
        when(trainingProgramSyllabusRepository.findByIdTopicCodeAndIdTrainingProgramCode(topic_code, training_program_code)).thenThrow(new RuntimeException());

        UpdateClass3Response response = classService.updateClass3(updateClass3Request);

        assertNotNull(response);
        assertEquals("Failure", response.getStatus());
        assertEquals(null, response.getUpdatedClass3());
    }


    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/27/2024 11:50 PM
     * @description: test update class with class does not exist
     * @update:
     */
    @Test
    void testUpdateClass_ClassNotExist() {
        String classCode = class2.getClassId();
        UpdateClassDTO updateClassRequest = UpdateClassDTO.builder()
                .className("OJT FPT")
                .duration("30")
                .startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .attendeeActual("30")
                .attendee("32")
                .attendeePlanned("30")
                .attendeeAccepted("12")
                .classTimeFrom("09:00:00")
                .classTimeTo("11:00:00")
                .location("England")
                .status("active")
                .trainingProgram("1")
                .listDay(Collections.singletonList("2024-04-01"))
                .attendeeList(Collections.singletonList("ctv@gmail.com"))
                .admin(Collections.singletonList("admin@gmail.com"))
                .trainer(Collections.singletonList(TrainerSyllabusDTO.builder()
                        .gmail("ctv@gmail.com")
                        .classCode(Collections.singletonList("JAVA"))
                        .build()))
                .moderEmail("admin@gmail.com")
                .build();

        when(classRepository.findById(classCode)).thenReturn(Optional.empty());

        UpdateClassResponse response = classService.updateClass(updateClassRequest, classCode);

        assertNotNull(response);
        assertEquals(1, response.getStatus());
        assertEquals("The Class with Id: " + classCode + " does not exist!", response.getMessage());
        assertEquals(null, response.getUpdatedClass());
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/28/2024 12:04 AM
     * @description: test update class with exception
     * @update:
     */
    @Test
    void testUpdateClass_getException() {
        String classCode = class2.getClassId();
        UpdateClassDTO updateClassRequest = UpdateClassDTO.builder()
                .className("OJT FPT")
                .duration("30")
                .startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .attendeeActual("30")
                .attendee("32")
                .attendeePlanned("30")
                .attendeeAccepted("12")
                .classTimeFrom("09:00:00")
                .classTimeTo("11:00:00")
                .location("England")
                .status("planning")
                .trainingProgram("1")
                .listDay(Collections.singletonList("2024-04-01"))
                .attendeeList(Collections.singletonList("ctv@gmail.com"))
                .admin(Collections.singletonList("admin@gmail.com"))
                .trainer(Collections.singletonList(TrainerSyllabusDTO.builder()
                        .gmail("ctv@gmail.com")
                        .classCode(Collections.singletonList("JAVA"))
                        .build()))
                .moderEmail("admin@gmail.com")
                .build();

        when(classRepository.findById(classCode)).thenThrow(new RuntimeException(""));

        UpdateClassResponse response = classService.updateClass(updateClassRequest, classCode);

        assertNotNull(response);
        assertEquals(2, response.getStatus());
        assertEquals("An error occurred while trying to update Class. Exception!", response.getMessage());
        assertEquals(null, response.getUpdatedClass());
    }

    /*
     * @author: Le Ngoc Tam Nhu
     * @since: 3/28/2024 12:26 PM
     * @description: test update class success
     * @update:
     */
    @Test
    public void testUpdateClass_Success() {
        List<String> list = new ArrayList<>();
        list.add(0, "2024-04-12");
        List<String> listctv = new ArrayList<>();
        listctv.add("Man Nhi");
        List<String> listadmin = new ArrayList<>();
        listadmin.add("admin@gmail.com");
        List<String> listcode = new ArrayList<>();
        listcode.add("JAVA");
        TrainerSyllabusDTO trainerSyllabusDTO = new TrainerSyllabusDTO("ctv@gmail.com", listcode);
        List<TrainerSyllabusDTO> list3 = new ArrayList<>();
        list3.add(trainerSyllabusDTO);

        String classCode = class2.getClassId();
        UpdateClassDTO updateClassRequest = UpdateClassDTO.builder()
                .className("OJT FPT")
                .duration("30")
                .startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .attendeeActual("30")
                .attendee("32")
                .attendeePlanned("30")
                .attendeeAccepted("12")
                .classTimeFrom("09:00:00")
                .classTimeTo("11:00:00")
                .location("England")
                .status("planning")
                .trainingProgram(Integer.toString(trainingProgram.getTrainingProgramCode()))
                .listDay(list)
                .attendeeList(listctv)
                .admin(listadmin)
                .trainer(list3)
                .moderEmail("admin@gmail.com")
                .build();

        Class classUpdate = Class.builder()
                .classId(classCode)
                .trainingProgramCode(trainingProgram)
                .className(updateClassRequest.getClassName())
                .duration(updateClassRequest.getDuration())
                .status(updateClassRequest.getStatus())
                .startDate(updateClassRequest.getStartDate())
                .endDate(updateClassRequest.getEndDate())
                .createdBy(class2.getCreatedBy())
                .createdDate(class2.getCreatedDate())
                .deactivated(class2.isDeactivated())
                .attendee(updateClassRequest.getAttendee())
                .attendeePlanned(Integer.parseInt(updateClassRequest.getAttendeePlanned()))
                .attendeeAccepted(Integer.parseInt(updateClassRequest.getAttendeeAccepted()))
                .attendeeActual(Integer.parseInt(updateClassRequest.getAttendeeAccepted()))
                .location(updateClassRequest.getLocation())
                .build();

        when(classRepository.findById(classCode)).thenReturn(Optional.of(class2));
        //when(classRepository.save(classUpdate)).thenReturn(classUpdate);

        when(classUserRepository.findByClassId_ClassId(classCode)).thenReturn(Collections.singletonList(classUser));
        when(userClassSyllabusRepository.findByClassCode_ClassId(classCode)).thenReturn(Collections.singletonList(userClassSyllabus));
        when(classLearningDayRepository.findByClassId_ClassId(classCode)).thenReturn(Collections.singletonList(classLearningDay));

        when(classLearningDayRepository.findByClassId_ClassId(classCode)).thenReturn(Collections.singletonList(classLearningDay));

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(trainingProgramRepository.findById(any())).thenReturn(Optional.of(trainingProgram));

        UpdateClassResponse response = classService.updateClass(updateClassRequest, classCode);

        assertNotNull(response);
        assertEquals("The Class with Id: " + classCode + " has been updated successfully!", response.getMessage());
        assertNotNull((response.getUpdatedClass()));
    }

    @Test
    void testFilterClass_success() {
        List<String> location = new ArrayList<>(Arrays.asList("Room 102"));
        List<String> classTime = new ArrayList<>();
        String startDate = "";
        String endDate = "";
        List<String> status = new ArrayList<>();
        String trainer = "";
        ResponseEntity<ResponseObject> response = classService.filterClass(location, classTime, startDate, endDate, status, trainer);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Success");
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Class(es) matching the filters has been found!");
        assertNotNull(response.getBody().getPayload());
    }


    @Test
    void testCreateClass_getException() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        List<ClassLearningDay> classLearningDays = new ArrayList<>();
        List<ClassUser> classUsers = new ArrayList<>();
        List<UserClassSyllabus> userSyllabusList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add(0, "2024-12-12");
        List<String> list1 = new ArrayList<>();
        list1.add("ctv@gmail.com");
        List<String> list2 = new ArrayList<>();
        list2.add("ctv@gmail.com");

//        list2.add("admin@gmail.com");
        List<String> topicList = new ArrayList<>(Arrays.asList("Java"));
        TrainerSyllabusRequest trainerSyllabusRequest = new TrainerSyllabusRequest("ctv@gmail.com", topicList);
        List<TrainerSyllabusRequest> list3 = new ArrayList<>();
        list3.add(trainerSyllabusRequest);

        CreateClassDTO createClassDTO = CreateClassDTO.builder()
                .classCode("CS107")
                .nameClass("IT")
                .classTimeTo("13:00:00")
                .classTimeFrom("17:00:00")
                .location("HCM")
                .startDate(String.valueOf(LocalDate.now().plusDays(1)))
                .endDate(String.valueOf(LocalDate.now().plusDays(4)))
                .createdBy("ADMIN")
                .attendee("Fresher")
                .attendeeAccepted(23)
                .attendeePlanned(23)
                .attendeeActual(24)
                .trainingProgram(2)
                .listDay(list)
                .admin(list1)
                .attendeeList(list2)
                .trainer(list3)
                .build();

        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));

        when(syllabusRepository.findById(anyString())).thenReturn(Optional.of(syllabus));
        when(userRepository.findUserByEmail(user.getName())).thenReturn(Optional.of(userDTO));
        when(classRepository.findById(class2.getClassId())).thenReturn(Optional.empty());
        when(trainingProgramRepository.findById(trainingProgram.getTrainingProgramCode())).thenReturn(Optional.of(trainingProgram));
        when(jwtService.extractUserEmail(token)).thenReturn(user.getName());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(classLearningDayRepository.saveAll(any())).thenReturn(classLearningDays);
        when(classUserRepository.saveAll(any())).thenReturn(classUsers);
        when(userClassSyllabusRepository.saveAll(any())).thenThrow(new RuntimeException(""));

        ResponseEntity<CreateClassResponse> response = classService.createClass(createClassDTO);

        assertNotNull(response);
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("An error occurred while trying to create Class. Error message: ");
    }

}
