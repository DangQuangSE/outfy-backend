package com.outfy.outfy_backend.modules.auth.service;

import com.outfy.outfy_backend.modules.auth.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@outfy.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.otp.expiration-minutes:5}")
    private long otpExpirationMinutes;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String otp) {
        try {
            String subject = "Email Verification - Outfy";
            String body = buildVerificationEmailBody(user.getFullName(), otp);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildVerificationEmailBody(String fullName, String otp) {
        return String.format(
            "Hello %s,\n\n" +
            "Welcome to Outfy! Please verify your email address to complete your registration.\n\n" +
            "Your verification code is: %s\n\n" +
            "This code will expire in %d minutes.\n\n" +
            "If you did not create this account, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Outfy Team",
            fullName, otp, otpExpirationMinutes
        );
    }

    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            String subject = "Password Reset - Outfy";
            String body = String.format(
                "Hello %s,\n\n" +
                "Please click the link below to reset your password:\n\n" +
                "%s\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Outfy Team",
                user.getFullName(), resetLink
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
