package com.outfy.outfy_backend.modules.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.outfy.outfy_backend.common.exception.BusinessRuleViolationException;
import com.outfy.outfy_backend.modules.auth.dto.response.AuthResponse;
import com.outfy.outfy_backend.modules.auth.entity.User;
import com.outfy.outfy_backend.modules.auth.enums.AuthProvider;
import com.outfy.outfy_backend.modules.auth.enums.UserRole;
import com.outfy.outfy_backend.modules.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

    private final UserRepository userRepository;
    private final AuthService authService;

    @Value("${app.google.client-id}")
    private String googleClientId;

    public GoogleAuthService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public AuthResponse googleLogin(String idTokenString) {
        logger.info("Processing Google login");

        // Verify the Google ID token
        GoogleIdToken.Payload payload = verifyGoogleToken(idTokenString);

        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String fullName = (String) payload.get("name");
        String avatarUrl = (String) payload.get("picture");

        logger.info("Google token verified for email: {}", email);

        // Find existing user or create new one
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();

            // If user registered with email/password, link their Google account
            if (user.getAuthProvider() == AuthProvider.LOCAL) {
                user.setAuthProvider(AuthProvider.GOOGLE);
                user.setGoogleId(googleId);
                if (avatarUrl != null && user.getAvatarUrl() == null) {
                    user.setAvatarUrl(avatarUrl);
                }
                user.setIsEmailVerified(true);
                user = userRepository.save(user);
                logger.info("Linked Google account to existing user: {}", user.getId());
            }
        } else {
            // Create new user from Google info
            user = new User();
            user.setEmail(email);
            user.setPassword(null); // No password for Google users
            user.setFullName(fullName);
            user.setAvatarUrl(avatarUrl);
            user.setGoogleId(googleId);
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setRole(UserRole.USER);
            user.setIsActive(true);
            user.setIsEmailVerified(true); // Google emails are already verified

            user = userRepository.save(user);
            logger.info("New user created from Google login with id: {}", user.getId());
        }

        if (!user.getIsActive()) {
            throw new BusinessRuleViolationException("Account is inactive");
        }

        return authService.generateAuthResponse(user);
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new BusinessRuleViolationException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new BusinessRuleViolationException("Google email is not verified");
            }

            return payload;
        } catch (BusinessRuleViolationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error verifying Google ID token", e);
            throw new BusinessRuleViolationException("Failed to verify Google ID token");
        }
    }
}
