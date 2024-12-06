package com.example.taskmangementsystem.g.utils;

import com.example.taskmangementsystem.g.models.App_User;
import com.example.taskmangementsystem.g.repositorys.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private UserRepository userRepository;

    private final String secretKey = "GHFCFXVoiuytdrxcfgvbhnmIUYTRDSXCkjhgfdxRESXDFCGVBHNYTREDC56789";

    // Convert the secretKey String to a SecretKey
    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract All Claims using JwtParserBuilder
    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Use the SecretKey here
                .build();
        return parser.parseClaimsJws(token).getBody();
    }

    // Generate JWT token
    public String generateToken(String email) {
//        String email = getUsernameFromEmail(email);  // Get username based on email

        Key signingKey = getSigningKey();  // Get the signing key

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                .signWith(signingKey)  // Use the signingKey here
                .compact();
    }

    public String getUsernameFromEmail(String email) {
        App_User appUser = userRepository.findByEmailIgnoreCase(email);  // Assuming you have a userRepository
        if (appUser != null) {
            return appUser.getUsername();  // Return the username of the user
        }
        throw new UsernameNotFoundException("User with email " + email + " not found");
    }

    // Validate JWT token
    public boolean validateToken(String token, String username) {
        String usernameFromToken = extractUsername(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    // Extract Username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract Expiration Date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
