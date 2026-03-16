# Email Verification Feature Implementation

## Overview
This document describes the email verification feature implemented for user registration in the Outfy Backend application. OTP codes are stored in **Caffeine in-memory cache** (not database) with automatic expiration.

## Features Implemented

### 1. OTP-based Email Verification
- **Service**: `OtpService` with Caffeine cache
- **OTP expires after 5 minutes** (configurable via `app.otp.expiration-minutes`)
- **SecureRandom 6-digit code generation**
- **One-time use** (evicted from cache after successful verification)

### 2. Updated User Entity
- Added `isEmailVerified` field (default: false)
- Added `emailVerifiedAt` field to track verification timestamp
- New users start with unverified email status

### 3. Email Service
- Sends verification emails using Spring Mail
- Email contains 6-digit OTP code and expiration info
- Supports password reset emails (foundation for future feature)

### 4. Authentication Service Updates
- **register()**: Creates user, generates OTP, sends email. Returns `UserResponse` (NO tokens)
- **verifyEmail()**: Validates OTP from cache and marks email as verified
- **resendVerificationEmail()**: Generates new OTP (overwrites old in cache) and sends email
- **login()**: Checks `isEmailVerified` — blocks login if not verified

### 5. Cache Configuration
- **CacheConfig.java**: Configures Caffeine cache named `emailOtp`
- TTL: 5 minutes (configurable)
- Max entries: 10,000

### 6. API Endpoints

#### Register User
```
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "fullName": "John Doe",
  "phone": "+1234567890"
}

Response (201 Created):
{
  "success": true,
  "message": "Registration successful. Please check your email to verify your account.",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "isEmailVerified": false,
    ...
  }
}
```

#### Verify Email
```
POST /api/v1/auth/verify-email
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "123456"
}

Response (200 OK):
{
  "success": true,
  "message": "Email verified successfully",
  "data": null
}
```

#### Resend Verification Email
```
POST /api/v1/auth/resend-verification-email
Content-Type: application/json

{
  "email": "user@example.com"
}

Response (200 OK):
{
  "success": true,
  "message": "Verification email sent successfully",
  "data": null
}
```

#### Login (blocked if email not verified)
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123"
}

Error Response (email not verified):
{
  "success": false,
  "message": "Please verify your email before logging in.",
  "data": null
}
```

## Configuration

### Required Environment Variables

```properties
# Email Server Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password  # For Gmail, use App Password
MAIL_FROM=noreply@outfy.com

# Frontend URL
FRONTEND_URL=http://localhost:3000

# OTP Configuration
OTP_EXPIRATION_MINUTES=5  # Default: 5 minutes
```

### Example: Gmail Configuration
For Gmail, you need to:
1. Enable 2-Step Verification in your Google account
2. Generate an App Password: Google Account → Security → App Passwords
3. Use: `MAIL_USERNAME=your-email@gmail.com`
4. Use: `MAIL_PASSWORD=your-16-character-app-password`

### Default Configuration (in application.properties)
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
app.otp.expiration-minutes=5
```

## Workflow

### User Registration Flow
1. User calls `POST /auth/register` with email, password, fullName
2. System validates input and checks if email exists
3. User account created with `isEmailVerified = false`
4. 6-digit OTP generated and stored in Caffeine cache (TTL 5 min)
5. Verification email sent with OTP code
6. Response returns `UserResponse` only — **NO access/refresh tokens**
7. User must verify email before they can login

### Email Verification Flow
1. User receives email with 6-digit OTP code
2. User submits email + OTP via `POST /auth/verify-email`
3. System checks OTP from cache (throws error if expired or invalid)
4. System marks email as verified with timestamp
5. OTP evicted from cache (one-time use)
6. User can now login and receive tokens

### Login Flow (with email verification check)
1. User calls `POST /auth/login` with email and password
2. System validates credentials
3. System checks `isActive` (blocks if inactive)
4. **System checks `isEmailVerified`** (blocks if not verified) ⛔
5. JWT access token and refresh token issued

### Resend Verification Email Flow
1. User calls `POST /auth/resend-verification-email`
2. System finds user by email
3. Checks if email already verified (throws error if yes)
4. Generates new OTP (overwrites old one in cache)
5. Sends new verification email with fresh OTP

## Error Handling

### Registration Errors
- **400 Bad Request**: Invalid email format, password too short
- **409 Conflict**: Email already registered

### Verification Errors
- **400 Bad Request**: Invalid OTP code
- **400 Bad Request**: OTP expired or does not exist

### Login Errors
- **400 Bad Request**: Invalid email or password
- **400 Bad Request**: Email not verified
- **400 Bad Request**: Account inactive

### Resend Email Errors
- **400 Bad Request**: User with email not found
- **409 Conflict**: Email already verified

## Security Considerations

1. **OTP Security**:
   - Uses `SecureRandom` for cryptographically secure 6-digit codes
   - Stored in-memory only (not persisted to DB, no leak risk)
   - Auto-expires after 5 minutes via Caffeine TTL
   - One-time use (evicted after successful verification)
   - Email required alongside OTP (prevents blind brute-force)

2. **Authentication Security**:
   - Register does NOT issue tokens (forces email verification)
   - Login checks `isEmailVerified` before issuing tokens
   - Password hashed with BCrypt

3. **Rate Limiting**: Consider implementing rate limiting on:
   - Registration endpoint
   - Resend verification endpoint
   - Verify email endpoint (prevent OTP brute-force)

4. **HTTPS**: Ensure all endpoints use HTTPS in production

## Dependencies

- `spring-boot-starter-mail`: Email sending functionality
- `spring-boot-starter-cache`: Spring Cache abstraction
- `com.github.ben-manes.caffeine:caffeine`: High-performance in-memory cache

## Files

### Created Files:
- `CacheConfig.java` - Caffeine cache configuration (emailOtp cache)
- `OtpService.java` - OTP generation, storage, and verification via cache
- `EmailService.java` - Email sending service
- `VerifyEmailRequest.java` - DTO with `email` + `otp` fields
- `ResendVerificationEmailRequest.java` - DTO for resending email

### Modified Files:
- `User.java` - Added verification fields
- `UserResponse.java` - Added verification fields
- `AuthService.java` - Added verification methods, login check, register returns UserResponse
- `AuthController.java` - Register returns `ApiResponse<UserResponse>`
- `pom.xml` - Added Spring Cache + Caffeine dependencies
- `application.properties` - Added OTP configuration

### Deleted Files:
- `EmailVerificationToken.java` - No longer needed (OTP in cache)
- `EmailVerificationTokenRepository.java` - No longer needed

## Testing the Feature

### Test 1: Registration
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPassword123",
    "fullName": "Test User",
    "phone": "+1234567890"
  }'
```

### Test 2: Login before verification (should fail)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPassword123"
  }'
```

### Test 3: Verify Email
```bash
curl -X POST http://localhost:8080/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

### Test 4: Resend Verification Email
```bash
curl -X POST http://localhost:8080/api/v1/auth/resend-verification-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'
```

### Test 5: Login after verification (should succeed)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPassword123"
  }'
```

## Notes

- **Login requires email verification** — users cannot login until they verify their email
- **OTP is stored in-memory** — restarting the server will invalidate all pending OTPs
- **Resending OTP** overwrites the old OTP in cache (only the latest OTP is valid)
- For multi-instance deployments, consider switching from Caffeine to Redis
