package com.example.usermanagement.Service;

import com.example.usermanagement.entity.Token;
import com.example.usermanagement.repository.TokenRepository;
import com.example.usermanagement.service_implements.LogoutHandlerImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogoutHandlerImplTest {
    @Spy
    HttpServletResponse response = mock(HttpServletResponse.class);
    @Spy
    Authentication authentication = mock(Authentication.class);
    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private LogoutHandlerImpl logoutHandler;
    @Spy
    private HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    void test_LogoutHandler_Success() {
        // Mock authorization
        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        Token mockToken = new Token();

        // Mock token responsitory
        when(tokenRepository.findByToken("mockToken")).thenReturn(Optional.of(mockToken));
        // call logout
        logoutHandler.logout(request, response, authentication);
    }

}
