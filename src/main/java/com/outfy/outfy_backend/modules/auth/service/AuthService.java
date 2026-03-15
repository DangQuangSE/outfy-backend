package com.outfy.outfy_backend.modules.auth.service;

import com.outfy.outfy_backend.common.exception.BusinessRuleViolationException;
import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.modules.auth.dto.request.LoginRequest;
import com.outfy.outfy_backend.modules.auth.dto.request.RefreshTokenRequest;
import com.outfy.outfy_backend.modules.auth.dto.request.RegisterRequest;
import com.outfy.outfy_backend.modules.auth.dto.response.AuthResponse;
import com.outfy.outfy_backend.modules.auth.dto.response.UserResponse;
import com.outfy.outfy_backend.modules.auth.entity.RefreshToken;
import com.outfy.outfy_backend.modules.auth.entity.User;
import com.outfy.outfy_backend.modules.auth.enums.UserRole;
import com.outfy.outfy_backend.modules.auth.mapper.UserMapper;
import com.outfy.outfy_backend.modules.auth.repository.RefreshTokenRepository;
import com.outfy.outfy_backend.modules.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final long ACCESS_TOKEN_EXPIRY_MINUTES = 30;
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleViolationException("Email already registered");
        }

        // Use mapper to convert request to entity
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setIsActive(true);

        User saved = userRepository.save(user);
        logger.info("User registered successfully with id: {}", saved.getId());

        return generateAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        logger.info("User login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessRuleViolationException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new BusinessRuleViolationException("Account is inactive");
        }

        logger.info("User logged in successfully: {}", user.getId());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        logger.info("Refreshing access token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid refresh token"));

        if (refreshToken.getIsRevoked()) {
            throw new BusinessRuleViolationException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleViolationException("Refresh token has expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));

        // Revoke old refresh token
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);

        logger.info("Token refreshed successfully for user: {}", user.getId());

        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(Long userId) {
        logger.info("Logging out user: {}", userId);
        refreshTokenRepository.revokeAllUserTokens(userId);
        logger.info("All refresh tokens revoked for user: {}", userId);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token
        RefreshToken token = new RefreshToken();
        token.setUserId(user.getId());
        token.setToken(refreshToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS));
        token.setIsRevoked(false);
        refreshTokenRepository.save(token);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(ACCESS_TOKEN_EXPIRY_MINUTES * 60L);
        response.setUser(userMapper.toResponse(user));

        return response;
    }
}

