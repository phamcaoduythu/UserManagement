package com.example.usermanagement.Service;

import com.example.usermanagement.Response.UserDTOImp;
import com.example.usermanagement.dto.Request.CreateRequest;
import com.example.usermanagement.dto.Request.LoginRequest;
import com.example.usermanagement.dto.Response.LoginResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.entity.Token;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.UserPermission;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.TokenRepository;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service.MailService;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.service_implements.AuthenticationServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.usermanagement.enums.Permission.USER_CREATE;
import static com.example.usermanagement.enums.Permission.USER_VIEW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthenticationServiceImplTest {
    private static final Logger logger = LogManager.getLogger(AuthenticationServiceImplTest.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserPermissionRepository userPermissionRepository;
    @Mock
    private JWTService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    @Mock
    private MailService mailService;
    @Mock
    private TokenRepository tokenRepository;

    @Test
    void User_Login_NotExist() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("1")
                .build();

        // mock data
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<LoginResponse> response = authenticationService.login(loginRequest);

    }

    @Test
    void User_Login_Success() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        UserPermission userPermission = UserPermission.builder()
                .role(Role.USER)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_VIEW))
                .learningMaterial(List.of())
                .build();

        int userId = 1;
        // Create a mock User object
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermission);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("1")
                .build();

        User user = User.builder()
                .email("dmin@gmail.com")
                .password("1")
                .build();
        Token token1 = new Token();
        List<Token> tokenList = new ArrayList<>();
        tokenList.add(token1);
        when(tokenRepository.findAllUserTokenByUserId(userId)).thenReturn(tokenList);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(user)).thenReturn("mockedToken");

        var token = jwtService.generateToken(user);
        var person = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        ResponseEntity<LoginResponse> response = authenticationService.login(loginRequest);
        Assertions.assertThat(response.getBody().getMessage());

    }

    @Test
    public void User_Login_ExceptionThrown() {
        // Mocking data
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("1")
                .build();
        // Mocking behavior to throw an exception
        when(userRepository.findByEmail(loginRequest.getEmail())).thenThrow(new RuntimeException("Test Exception"));
        // Performing the login
        ResponseEntity<LoginResponse> responseEntity = authenticationService.login(loginRequest);

    }


    @Test
    void User_CreateUser_Success() throws MessagingException {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();
        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        // Create a mock User object
        int userId = 123;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        CreateRequest request =
                CreateRequest.builder()
                        .name("Albert Einstein")
                        .email("hovanlocan01012019@gmail.com")
                        .phone("0972156450")
                        .gender("Male")
                        .role("SUPER_ADMIN")
                        .status(true)
                        .build();
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .gender(request.getGender())
                        .role(mockUser.getRole())
                        .status(request.isStatus())
                        .build();
        when(userRepository.save(any())).thenReturn(user);

        mailService.sendEmail(user.getEmail(), user.getName(), user.getPassword());

        ResponseEntity<ResponseObject> response = authenticationService.createUser(request);
        log.info("Response value: " + response.toString());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }


    @Test
    void User_CreateUser_ThrowException() throws MessagingException {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();
        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        // Create a mock User object
        int userId = 123;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        when(userRepository.findByEmail(Mockito.any())).thenThrow(new RuntimeException("Test Exception"));
        CreateRequest request =
                CreateRequest.builder()
                        .name("Albert Einstein")
                        .email("hovanlocan01012019@gmail.com")
                        .phone("0972156450")
                        .gender("Male")
                        .role("SUPER_ADMIN")
                        .status(true)
                        .build();
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .gender(request.getGender())
                        .role(mockUser.getRole())
                        .status(request.isStatus())
                        .build();
        mailService.sendEmail(any(), any(), any());
        ResponseEntity<ResponseObject> response = authenticationService.createUser(request);
        log.info("Response value: " + response.toString());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void User_CreateUser_InvalidEmailFormat() throws MessagingException {
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();

        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        // Create a mock User object
        int userId = 123;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        CreateRequest request =
                CreateRequest.builder()
                        .name("Albert Einstein")
                        .email("hovanlocan01012019@hotmail.com")
                        .phone("0972156450")
                        .gender("Male")
                        .role("SUPER_ADMIN")
                        .status(true)
                        .build();
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .gender(request.getGender())
                        .role(mockUser.getRole())
                        .status(request.isStatus())
                        .build();
        mailService.sendEmail(user.getEmail(), user.getName(), user.getPassword());
        ResponseEntity<ResponseObject> response = authenticationService.createUser(request);
        log.info("Response value: " + response.toString());
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
    }

    @Test
    void User_CreateUser_EmailExist() throws MessagingException {
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();

        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        // Create a mock User object
        int userId = 123;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        CreateRequest request =
                CreateRequest.builder()
                        .name("Albert Einstein")
                        .email("hovanlocan01012019@gmail.com")
                        .phone("0972156450")
                        .gender("Male")
                        .role("SUPER_ADMIN")
                        .status(true)
                        .build();
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .gender(request.getGender())
                        .role(mockUser.getRole())
                        .status(request.isStatus())
                        .build();
        mailService.sendEmail(user.getEmail(), user.getName(), user.getPassword());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        ResponseEntity<ResponseObject> response = authenticationService.createUser(request);
        log.info("Response value: " + response.toString());
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
    }

    @Test
    void User_CreateUser_RoleNotExist() throws MessagingException {
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();

        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.empty());
        // Create a mock User object
        int userId = 123;
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(userId);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        CreateRequest request =
                CreateRequest.builder()
                        .name("Albert Einstein")
                        .email("hovanlocan01012019@gmail.com")
                        .phone("0972156450")
                        .gender("Male")
                        .role("SUPER_ADMIN")
                        .status(true)
                        .build();
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .gender(request.getGender())
                        .role(mockUser.getRole())
                        .status(request.isStatus())
                        .build();
        mailService.sendEmail(user.getEmail(), user.getName(), user.getPassword());
        ResponseEntity<ResponseObject> response = authenticationService.createUser(request);
        log.info("Response value: " + response.toString());
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo("Failure");
    }

    @Test
    void User_GetLoggedInUser_Success() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer admin@gmail.com");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();
        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setUserId(1);
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        User user =
                User.builder()
                        .name("admin")
                        .email("admin@gmail.com")
                        .role(mockUser.getRole())
                        .build();
        when(jwtService.extractUserEmail(user.getEmail())).thenReturn("admin@gmail.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token, user)).thenReturn(true);
        when(userRepository.findUserByEmail("admin@gmail.com")).thenReturn(Optional.of(mockUser));
        ResponseEntity<ResponseObject> response = authenticationService.getLoggedInUser(httpServletRequestMock);
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("Get user success");
    }

    @Test
    void User_GetLoggedInUser_TokenAndUserDetailsNotMatch() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer admin@gmail.com");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();
        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        User user = User.builder()
                .name("admin")
                .email("admin@gmail.com")
                .role(mockUser.getRole())
                .build();
        when(jwtService.extractUserEmail(user.getEmail())).thenReturn("admin@gmail.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token, user)).thenReturn(false);
        ResponseEntity<ResponseObject> response = authenticationService.getLoggedInUser(httpServletRequestMock);
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("The token and the UserDetails are not matching!");
    }

    @Test
    void User_GetLoggedInUser_TokenIsInvalid() {
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer admin@gmail.com");
        ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(customServletRequestAttributes);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        UserPermission userPermission = UserPermission.builder()
                .role(Role.SUPER_ADMIN)
                .syllabus(List.of())
                .trainingProgram(List.of())
                .userClass(List.of())
                .userManagement(List.of(USER_CREATE))
                .learningMaterial(List.of())
                .build();
        when(userPermissionRepository.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
        UserDTOImp mockUser = new UserDTOImp();
        mockUser.setRole(userPermissionRepository.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
        User user = User.builder()
                .name("admin")
                .email("admin@gmail.com")
                .role(mockUser.getRole())
                .build();
        when(jwtService.extractUserEmail(user.getEmail())).thenReturn(null);
        ResponseEntity<ResponseObject> response = authenticationService.getLoggedInUser(httpServletRequestMock);
        Assertions.assertThat(response.getBody().getMessage()).isEqualTo("The token is unavailable!");
    }





}