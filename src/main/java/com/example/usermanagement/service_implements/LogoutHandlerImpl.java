package com.example.usermanagement.service_implements;

import com.example.usermanagement.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:38 PM
     * description:
     * update:
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String authenHeader = request.getHeader("Authorization");
        String token;
        if (authenHeader == null || !authenHeader.startsWith("Bearer ")) {
            return;
        }
        token = authenHeader.substring(7);
        var storedToken = tokenRepository.findByToken(token).orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
