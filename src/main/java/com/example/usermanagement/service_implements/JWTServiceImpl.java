package com.example.usermanagement.service_implements;

import com.example.usermanagement.repository.TokenRepository;
import com.example.usermanagement.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTServiceImpl implements JWTService {

    private final TokenRepository tokenRepository;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long jwtRefreshExpiration;

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:36 PM
     * description:
     * update:
     */
    @Override
    public String extractUserEmail(String token) {
        return extractClaimsFromToken(token, Claims::getSubject);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:36 PM
     * description:
     * update:
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        var validToken = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);
        String email = userDetails.getUsername();
        return (email.equals(extractUserEmail(token)) && !isTokenExpired(token) && validToken);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:36 PM
     * description:
     * update:
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtRefreshExpiration);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:36 PM
     * description:
     * update:
     */
    private String generateToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:37 PM
     * description:
     * update:
     */
    private String buildToken(HashMap<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:37 PM
     * description:
     * update:
     */
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:37 PM
     * description:
     * update:
     */
    private Date extractExpiration(String token) {
        return extractClaimsFromToken(token, Claims::getExpiration);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:37 PM
     * description:
     * update:
     */
    public <T> T extractClaimsFromToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:37 PM
     * description:
     * update:
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /*
     * author: Ho Van Loc An
     * since: 3/6/2024 8:37 PM
     * description:
     * update:
     */
    public Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
