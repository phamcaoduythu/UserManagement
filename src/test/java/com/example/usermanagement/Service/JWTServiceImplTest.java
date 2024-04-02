package com.example.usermanagement.Service;

import com.example.usermanagement.repository.TokenRepository;
import com.example.usermanagement.service_implements.JWTServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@ExtendWith(MockitoExtension.class)
public class JWTServiceImplTest {
    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private JWTServiceImpl jwtService;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey = "1";


}
