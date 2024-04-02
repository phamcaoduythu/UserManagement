package com.example.usermanagement.service_implements;

import com.example.usermanagement.dto.Request.CreateRequest;
import com.example.usermanagement.dto.Request.LoginRequest;
import com.example.usermanagement.dto.Response.AuthenticationResponse;
import com.example.usermanagement.dto.Response.LoginResponse;
import com.example.usermanagement.dto.Response.ResponseObject;
import com.example.usermanagement.dto.UserDTO;
import com.example.usermanagement.dto.UserWithRoleDTO;
import com.example.usermanagement.entity.Token;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.enums.TokenType;
import com.example.usermanagement.repository.TokenRepository;
import com.example.usermanagement.repository.UserPermissionRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.AuthenticationService;
import com.example.usermanagement.service.JWTService;
import com.example.usermanagement.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.usermanagement.utils.StringHandler.checkEmailRegrex;
import static com.example.usermanagement.utils.StringHandler.randomStringGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;
    @Autowired
    private final UserRepository userRepository;
    private final MailService mailService;
    private final UserPermissionRepository userPermissionRepository;

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:35 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        try {
            String msg;
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            var user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            if (user != null) {
                var token = jwtService.generateToken(user);
                revokeAllUserToken(user);
                saveUserToken(user, token);
                msg = "User login successfully";
                log.info(msg);
                LoginResponse loginResponse = LoginResponse.builder()
                        .status("Success")
                        .message(msg)
                        .token(token)
                        .userInfo(user).build();
                return ResponseEntity.ok(loginResponse);
            } else {
                msg = "This email is not existed!";
                log.error(msg);
                LoginResponse loginResponse = LoginResponse.builder()
                        .status("Failure")
                        .message(msg)
                        .token(null)
                        .userInfo(null)
                        .build();
                return ResponseEntity.ok(loginResponse);
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to login an account. Error message: " + e.getMessage());
            return ResponseEntity.ok(new LoginResponse("Failure", "Error occurred", null, null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:35 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> createUser(CreateRequest userRequest) throws RuntimeException {
        try {
            String msg;
            //check email
            if (!checkEmailRegrex(userRequest.getEmail())) {
                msg = "Failed to create User >> Invalid email format";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
            //get permission
            var permission = userPermissionRepository.findUserPermissionByRole(Role.valueOf(userRequest.getRole())).orElse(null);
            if (permission != null) {
                //check exist
                var existedUser = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
                if (existedUser == null) {
                    //encrypt password
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String password = randomStringGenerator(10);
                    String encodedPassword = passwordEncoder.encode(password);
                    //get created by
                    String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                            .getRequest().getHeader("Authorization").substring(7);
                    String headerEmail = jwtService.extractUserEmail(token);
                    String headerName = userRepository.getUserNameByEmail(headerEmail);
                    //build user
                    User user = User.builder()
                            .name(userRequest.getName())
                            .email(userRequest.getEmail())
                            .password(encodedPassword)
                            .phone(userRequest.getPhone())
                            .dob(userRequest.getDob())
                            .gender(userRequest.getGender())
                            .status(true)
                            .createdBy(headerName)
                            .createdDate(LocalDate.now())
                            .modifiedBy(headerName)
                            .modifiedDate(LocalDate.now())
                            .role(permission)
                            .build();
                    var savedUser = userRepository.save(user);
//                    try {
                        mailService.sendEmail(user.getEmail(), user.getName(), password);
//                    } catch (MessagingException e) {
//                        msg = "An error occurred while trying to send email message. Error message: " + e.getMessage();
//                        log.error(msg);
//                        return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
//                    }
                    msg = "New User created successfully";
                    log.info(msg);
                    ResponseObject createResponse = ResponseObject.builder()
                            .status("Success")
                            .message(msg)
                            .payload(UserWithRoleDTO.builder()
                                    .name(savedUser.getName())
                                    .role(savedUser.getRole().getRole())
                                    .email(savedUser.getEmail())
                                    .phone(savedUser.getPhone())
                                    .dob(savedUser.getDob())
                                    .gender(savedUser.getGender())
                                    .status(savedUser.isStatus())
                                    .createdBy(savedUser.getCreatedBy())
                                    .createdDate(savedUser.getCreatedDate())
                                    .modifiedBy(savedUser.getModifiedBy())
                                    .modifiedDate(savedUser.getModifiedDate())
                                    .build())
                            .build();
                    return ResponseEntity.ok(createResponse);
                } else {
                    log.error("This Email: " + existedUser.getEmail() + " is already existed!");
                    return ResponseEntity.ok(new ResponseObject("Failure", "Email existed", null));
                }
            } else {
                log.error("This Role " + userRequest.getRole() + " does not existed!");
                return ResponseEntity.ok(new ResponseObject("Failure", "Role does not exist", null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to create User. Error message: ");
            return ResponseEntity.ok(new ResponseObject("Failure", "Error occurred", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:35 PM
     * description:
     * update:
     */
    @Override
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            final String userEmail = jwtService.extractUserEmail(token);
            if (userEmail != null) {
                var existedUser = userRepository.findByEmail(userEmail).orElseThrow();
                if (jwtService.isTokenValid(token, existedUser)) {
                    var newToken = jwtService.generateToken(existedUser);
                    revokeAllUserToken(existedUser);
                    saveUserToken(existedUser, newToken);
                    var authResponse = AuthenticationResponse.builder()
                            .status("Success")
                            .token(newToken)
                            .refreshToken(token)
                            .build();
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            } else {
                var authResponse = AuthenticationResponse.builder()
                        .status("Failure")
                        .token(null)
                        .refreshToken(token)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to validate user token. Error message: " + e.getMessage());
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:35 PM
     * description:
     * update:
     */
    @Override
    public ResponseEntity<ResponseObject> getLoggedInUser(HttpServletRequest request) {
        try {
            String msg;
            String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest().getHeader("Authorization").substring(7);
            String userEmail = jwtService.extractUserEmail(token);
            if (userEmail != null) {
                var loggedInUser = userRepository.findByEmail(userEmail).orElseThrow();
                if (jwtService.isTokenValid(token, loggedInUser)) {
                    UserDTO userDTO = userRepository.findUserByEmail(userEmail).orElse(null);
                    msg = "Get user success";
                    ResponseObject responseObject = ResponseObject.builder()
                            .status("Success")
                            .message(msg)
                            .payload(UserWithRoleDTO.builder()
                                    .name(userDTO.getName())
                                    .role(userDTO.getRole().getRole())
                                    .email(userDTO.getEmail())
                                    .phone(userDTO.getPhone())
                                    .dob(userDTO.getDob())
                                    .gender(userDTO.getGender())
                                    .status(userDTO.isStatus())
                                    .createdBy(userDTO.getCreatedBy())
                                    .createdDate(userDTO.getCreatedDate())
                                    .modifiedBy(userDTO.getModifiedBy())
                                    .modifiedDate(userDTO.getModifiedDate())
                                    .build())
                            .build();
                    return ResponseEntity.ok(responseObject);
                } else {
                    msg = "The token and the UserDetails are not matching!";
                    log.error(msg);
                    return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
                }
            } else {
                msg = "The token is unavailable!";
                log.error(msg);
                return ResponseEntity.ok(new ResponseObject("Failure", msg, null));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to log in account. Error message: " + e.getMessage());
            return ResponseEntity.ok(new ResponseObject("Failure", "An error has occurred", null));
        }
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:35 PM
     * description:
     * update:
     */
    public void saveUserToken(User user, String token) {
        var userToken = Token.builder()
                .token(token)
                .user(user)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(userToken);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:35 PM
     * description:
     * update:
     */
    public void revokeAllUserToken(User user) {
        var tokenList = tokenRepository.findAllUserTokenByUserId(user.getUserId());
        tokenList.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(tokenList);
    }

}
