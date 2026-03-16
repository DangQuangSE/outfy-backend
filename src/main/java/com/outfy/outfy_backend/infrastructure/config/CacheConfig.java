package com.outfy.outfy_backend.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${app.otp.expiration-minutes:5}")
    private long otpExpirationMinutes;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("emailOtp");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(otpExpirationMinutes, TimeUnit.MINUTES)
                .maximumSize(10_000));
        return cacheManager;
    }
}
