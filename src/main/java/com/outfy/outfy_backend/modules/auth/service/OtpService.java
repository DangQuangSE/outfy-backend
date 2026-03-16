package com.outfy.outfy_backend.modules.auth.service;

import com.outfy.outfy_backend.common.exception.BusinessRuleViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final String CACHE_NAME = "emailOtp";
    private static final int OTP_LENGTH = 6;

    private final CacheManager cacheManager;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Generate a 6-digit OTP and store it in cache with email as key.
     * If an OTP already exists for this email, it will be overwritten.
     */
    public String generateAndStoreOtp(String email) {
        String otp = generateOtp();
        Cache cache = getCache();
        cache.put(email.toLowerCase(), otp);
        logger.info("OTP generated and stored in cache for email: {}", email);
        return otp;
    }

    /**
     * Verify the OTP for the given email.
     * If valid, the OTP is evicted from cache (one-time use).
     */
    public void verifyOtp(String email, String otp) {
        Cache cache = getCache();
        String cachedOtp = cache.get(email.toLowerCase(), String.class);

        if (cachedOtp == null) {
            throw new BusinessRuleViolationException("OTP has expired or does not exist. Please request a new one.");
        }

        if (!cachedOtp.equals(otp)) {
            throw new BusinessRuleViolationException("Invalid OTP. Please try again.");
        }

        // OTP is valid — evict it so it can't be reused
        cache.evict(email.toLowerCase());
        logger.info("OTP verified successfully for email: {}", email);
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(900_000) + 100_000; // 100000 - 999999
        return String.valueOf(otp);
    }

    private Cache getCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new IllegalStateException("Cache '" + CACHE_NAME + "' not found. Check CacheConfig.");
        }
        return cache;
    }
}
