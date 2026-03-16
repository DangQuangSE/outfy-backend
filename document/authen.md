# Email Verification Architecture & Implementation Details

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     REST API (Spring Boot)                   │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  AuthController                                               │
│  ├─ POST /register          → AuthService.register()        │
│  ├─ POST /verify-email      → AuthService.verifyEmail()     │
│  ├─ POST /resend-verification-email                         │
│  ├─ POST /login             → AuthService.login()           │
│  ├─ POST /refresh           → AuthService.refreshToken()    │
│  ├─ POST /logout            → AuthService.logout()     🔒  │
│  └─ GET  /me                → AuthService.getUserById() 🔒  │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│                      Service Layer                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  AuthService                                                  │
│  ├─ Handles user authentication logic                        │
│  ├─ Manages JWT token creation and validation                │
│  ├─ Orchestrates email verification flow                     │
│  └─ Checks isEmailVerified before login                      │
│                                                               │
│  OtpService                                                   │
│  ├─ Generates 6-digit OTP (SecureRandom)                     │
│  ├─ Stores OTP in Caffeine cache (TTL 5 min)                 │
│  └─ Verifies and evicts OTP (one-time use)                   │
│                                                               │
│  EmailService                                                 │
│  ├─ Sends verification emails with OTP code                  │
│  ├─ Sends password reset emails (future)                     │
│  └─ Manages email templates and SMTP                         │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│                  Repository Layer (Data Access)              │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  UserRepository                                               │
│  └─ CRUD operations on User entity                           │
│                                                               │
│  RefreshTokenRepository                                       │
│  └─ CRUD operations on refresh tokens                        │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│               Cache Layer (Caffeine In-Memory)               │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  emailOtp cache                                               │
│  ├─ Key: email (lowercase)                                   │
│  ├─ Value: 6-digit OTP string                                │
│  ├─ TTL: 5 minutes (configurable)                            │
│  └─ Max entries: 10,000                                      │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│                      Database Layer                           │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  PostgreSQL                                                   │
│  ├─ users table (+ is_email_verified, email_verified_at)   │
│  └─ refresh_tokens table                                     │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│                    External Services                          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  SMTP Server (Gmail/Outlook/SendGrid)                        │
│  └─ Sends actual emails                                      │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Data Flow Diagrams

### Registration Flow
```
Client                Controller              Service              Cache/DB
  │                       │                      │                    │
  ├─POST /register───────>│                      │                    │
  │                       ├─validate input       │                    │
  │                       ├─register()──────────>│                    │
  │                       │                      ├─encode password    │
  │                       │                      ├─create user───────>│
  │                       │                      │                    ├─INSERT users
  │                       │                      │                    │  (isEmailVerified=false)
  │                       │                      │                    │
  │                       │                      ├─generateOtp()     │
  │                       │                      ├─store OTP─────────>│
  │                       │                      │                    ├─CACHE put(email, otp)
  │                       │                      │                    │  [TTL 5 min]
  │                       │                      │                    │
  │                       │                      ├─send email◄────┐  │
  │                       │                      │   (with OTP)   │  │
  │                       │<─────────UserResponse            SMTP   │
  │<─201 Created─────────┤  (NO tokens!)         │                │  │
  │                       │                      │                │  │
  └─Email Sent──────────────────────────────────────────────────>SMTP
```

### Email Verification Flow
```
User              Client              Controller              Service              Cache/DB
 │                  │                       │                    │                    │
 ├─Receives Email─┐ │                       │                    │                    │
 │  (with OTP)     │                       │                    │                    │
 │                  │                       │                    │                    │
 ├─Enters OTP──────┤                       │                    │                    │
 │                  └─POST /verify-email──>│                    │                    │
 │                    {email, otp}          ├─verifyEmail()────>│                    │
 │                                          │                   ├─get OTP from cache>│
 │                                          │                   │                   ├─CACHE get(email)
 │                                          │                   │                   │
 │                                          │                   ├─compare OTP       │
 │                                          │                   │  (throw if invalid │
 │                                          │                   │   or expired)      │
 │                                          │                   │                   │
 │                                          │                   ├─evict OTP────────>│
 │                                          │                   │                   ├─CACHE evict(email)
 │                                          │                   │                   │
 │                                          │                   ├─update user──────>│
 │                                          │                   │                   ├─UPDATE users
 │                                          │                   │                   │  (isEmailVerified=true)
 │                                          │<────Success───────│
 │<─Success Message─────────────────────────│
 │
```

### Resend Verification Email Flow
```
User              Client              Controller              Service              Cache/DB
 │                  │                       │                    │                    │
 ├─Lost Email──────>│                       │                    │                    │
 │                  ├─POST /resend─────────>│                    │                    │
 │                  │                       ├─resendVerification│                    │
 │                  │                       │  Email()──────────>│                    │
 │                  │                       │                   ├─find user─────────>│
 │                  │                       │                   │                   ├─SELECT
 │                  │                       │                   │                   │
 │                  │                       │                   ├─check verified     │
 │                  │                       │                   │ (throw error if)    │
 │                  │                       │                   │                   │
 │                  │                       │                   ├─generate new OTP  │
 │                  │                       │                   ├─store OTP─────────>│
 │                  │                       │                   │                   ├─CACHE put(email, otp)
 │                  │                       │                   │                   │  [overwrites old OTP]
 │                  │                       │                   │                   │
 │                  │                       │                   ├─send email◄────┐  │
 │                  │                       │                   │                │  │
 │                  │<─────Success──────────│                   │                │  │
 │<─New Email Sent──────────────────────────────────────────────>────────────────SMTP
```

### Login Flow
```
Client                Controller              Service              Database
  │                       │                      │                    │
  ├─POST /login──────────>│                      │                    │
  │                       ├─login()─────────────>│                    │
  │                       │                      ├─find user─────────>│
  │                       │                      │                    ├─SELECT
  │                       │                      │                    │
  │                       │                      ├─check password     │
  │                       │                      ├─check isActive     │
  │                       │                      ├─check isEmailVerified ⛔
  │                       │                      │  (throw if false)  │
  │                       │                      │                    │
  │                       │                      ├─generate JWT       │
  │                       │                      ├─save refresh token>│
  │                       │                      │                    ├─INSERT refresh_tokens
  │                       │<─────AuthResponse────│                    │
  │<─200 OK──────────────┤                      │                    │
```

### Refresh Token Flow
```
Client                Controller              Service              Database
  │                       │                      │                    │
  ├─POST /refresh────────>│                      │                    │
  │  {refreshToken}        ├─refreshToken()─────>│                    │
  │                       │                      ├─find token────────>│
  │                       │                      │                    ├─SELECT refresh_tokens
  │                       │                      │                    │
  │                       │                      ├─check isRevoked    │
  │                       │                      ├─check expiresAt    │
  │                       │                      ├─find user─────────>│
  │                       │                      │                    ├─SELECT users
  │                       │                      │                    │
  │                       │                      ├─revoke old token──>│
  │                       │                      │                    ├─UPDATE (isRevoked=true)
  │                       │                      │                    │
  │                       │                      ├─generate new JWT   │
  │                       │                      ├─save new refresh──>│
  │                       │                      │     token           ├─INSERT refresh_tokens
  │                       │<─────AuthResponse────│                    │
  │<─200 OK──────────────┤                      │                    │
```

### Logout Flow 🔒 (Requires Access Token)
```
Client                Controller              Service              Database
  │                       │                      │                    │
  ├─POST /logout─────────>│                      │                    │
  │  [Authorization:       │                      │                    │
  │   Bearer <token>]      │                      │                    │
  │                       ├─getUserId from       │                    │
  │                       │  SecurityContext      │                    │
  │                       ├─logout(userId)──────>│                    │
  │                       │                      ├─revoke ALL────────>│
  │                       │                      │  refresh tokens     ├─UPDATE refresh_tokens
  │                       │                      │                    │  SET isRevoked=true
  │                       │                      │                    │  WHERE userId=?
  │                       │<─────Success─────────│                    │
  │<─200 OK──────────────┤                      │                    │
```

### Get Current User (/me) Flow 🔒 (Requires Access Token)
```
Client                Controller              Service              Database
  │                       │                      │                    │
  ├─GET /me──────────────>│                      │                    │
  │  [Authorization:       │                      │                    │
  │   Bearer <token>]      │                      │                    │
  │                       ├─getUserId from       │                    │
  │                       │  SecurityContext      │                    │
  │                       ├─getUserById(id)─────>│                    │
  │                       │                      ├─find user─────────>│
  │                       │                      │                    ├─SELECT users
  │                       │<─────UserResponse────│                    │
  │<─200 OK──────────────┤                      │                    │
```

## Entity Relationships

```
┌──────────────────────────────┐
│          User                 │
├──────────────────────────────┤
│ id (PK)                       │
│ email (UNIQUE)                │
│ password                      │
│ full_name                     │
│ phone                         │
│ date_of_birth                 │
│ gender                        │
│ avatar_url                    │
│ role                          │
│ is_active                     │
│ is_email_verified             │
│ email_verified_at             │
│ created_at                    │
│ updated_at                    │
└──────────┬───────────────────┘
           │
           │ (1 User : Many Tokens)
           │
┌──────────┴───────────────────────┐    ┌──────────────────────────────────┐
│   RefreshToken                    │    │   OTP (Caffeine Cache)           │
├───────────────────────────────────┤    ├──────────────────────────────────┤
│ id (PK)                           │    │ key: email (lowercase)           │
│ token (UNIQUE)                    │    │ value: 6-digit OTP string        │
│ user_id (FK → User.id)            │    │ TTL: 5 minutes (auto-expire)    │
│ expires_at                        │    │ Max entries: 10,000              │
│ is_revoked                        │    │                                  │
│ created_at                        │    │ (NOT stored in database)         │
└───────────────────────────────────┘    └──────────────────────────────────┘
```

## Request/Response Examples

### 1. Register Request
```json
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePassword123",
  "fullName": "John Doe",
  "phone": "+1-555-0123"
}
```

### 1. Register Response
```json
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "Registration successful. Please check your email to verify your account.",
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "phone": "+1-555-0123",
    "role": "USER",
    "isEmailVerified": false,
    "emailVerifiedAt": null,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Verify Email Request
```json
POST /api/v1/auth/verify-email
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "otp": "123456"
}
```

### 2. Verify Email Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Email verified successfully",
  "data": null
}
```

### 3. Resend Verification Email Request
```json
POST /api/v1/auth/resend-verification-email
Content-Type: application/json

{
  "email": "john.doe@example.com"
}
```

### 3. Resend Verification Email Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Verification email sent successfully",
  "data": null
}
```

### 4. Login (before email verification)
```json
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

### 4. Login Error Response (email not verified)
```json
HTTP/1.1 400 Bad Request

{
  "success": false,
  "message": "Please verify your email before logging in.",
  "data": null
}
```

### 5. Login Success Response
```json
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

```json
HTTP/1.1 200 OK

{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 1800,
    "user": {
      "id": 1,
      "email": "john.doe@example.com",
      "fullName": "John Doe",
      "isEmailVerified": true
    }
  }
}
```

### 6. Refresh Token Request/Response
```json
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

```json
HTTP/1.1 200 OK

{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...(new)",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...(new)",
    "tokenType": "Bearer",
    "expiresIn": 1800,
    "user": { ... }
  }
}
```

### 7. Logout Request/Response 🔒
```json
POST /api/v1/auth/logout
Authorization: Bearer <accessToken>
```

```json
HTTP/1.1 200 OK

{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

### 8. Get Current User (/me) Request/Response 🔒
```json
GET /api/v1/auth/me
Authorization: Bearer <accessToken>
```

```json
HTTP/1.1 200 OK

{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "phone": "+1-555-0123",
    "role": "USER",
    "isEmailVerified": true,
    "emailVerifiedAt": "2024-01-15T10:35:00",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

## Error Responses

### Invalid Email Format
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "Invalid email format",
  "errors": {
    "email": "Invalid email format"
  }
}
```

### Email Already Registered
```json
HTTP/1.1 409 Conflict
{
  "success": false,
  "message": "Email already registered",
  "data": null
}
```

### Invalid OTP
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "Invalid OTP. Please try again.",
  "data": null
}
```

### OTP Expired
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "OTP has expired or does not exist. Please request a new one.",
  "data": null
}
```

### Email Already Verified
```json
HTTP/1.1 409 Conflict
{
  "success": false,
  "message": "Email is already verified",
  "data": null
}
```

### Email Not Verified (Login)
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "Please verify your email before logging in.",
  "data": null
}
```

### Invalid Refresh Token
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "Invalid refresh token",
  "data": null
}
```

### Refresh Token Expired
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "Refresh token has expired",
  "data": null
}
```

### Refresh Token Revoked
```json
HTTP/1.1 400 Bad Request
{
  "success": false,
  "message": "Refresh token has been revoked",
  "data": null
}
```

### Unauthorized (no/invalid access token for /me, /logout)
```json
HTTP/1.1 401 Unauthorized
```

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `spring.mail.host` | smtp.gmail.com | SMTP server hostname |
| `spring.mail.port` | 587 | SMTP server port |
| `spring.mail.username` | your-email@gmail.com | Email account username |
| `spring.mail.password` | your-app-password | Email account password |
| `spring.mail.from` | noreply@outfy.com | Sender email address |
| `app.frontend.url` | http://localhost:3000 | Frontend URL |
| `app.otp.expiration-minutes` | 5 | OTP lifetime in minutes |
| Access Token Expiry | 30 minutes | JWT access token lifetime |
| Refresh Token Expiry | 7 days | JWT refresh token lifetime |

## Security Considerations

### OTP Security
- ✅ SecureRandom for cryptographic randomness (6-digit code)
- ✅ One-time use only (evicted from cache after verification)
- ✅ 5-minute expiration window (auto-expired by Caffeine TTL)
- ✅ Not stored in database (in-memory only, no leak risk)
- ✅ Email required alongside OTP (prevents brute-force on OTP alone)

### Authentication Security
- ✅ Register does NOT return JWT tokens (forces email verification first)
- ✅ Login checks `isEmailVerified` before issuing tokens
- ✅ Password encoded with BCrypt
- ✅ JWT-based stateless authentication
- ✅ `/me` and `/logout` require valid access token (`.authenticated()` in SecurityConfig)
- ✅ Refresh token rotation: old token revoked, new token issued
- ✅ Logout revokes ALL refresh tokens for the user

### Email Security
- ✅ SMTP with TLS/SSL (port 587)
- ✅ No sensitive data in plain text
- ✅ OTP code sent in email body
- ✅ Rate limiting recommended (not yet implemented)

### Input Validation
- ✅ Email format validation (@Valid annotation)
- ✅ Password strength validation (min 6 chars)
- ✅ Required field validation
- ✅ SQL injection protection (JPA parameterized queries)

### Transaction Safety
- ✅ @Transactional on all service methods
- ✅ Atomic database operations
- ✅ Rollback on exception

## Future Enhancements

1. **HTML Email Templates** - Use Thymeleaf for rich emails
2. **Rate Limiting** - Prevent spam/brute force attacks
3. **Multiple Email Providers** - SendGrid, AWS SES, Mailgun
4. **Email Change Verification** - Verify new email addresses
5. **Password Reset** - Reuse OTP infrastructure
6. **Two-Factor Authentication** - OTP-based 2FA for login
7. **Redis Cache** - Replace Caffeine for multi-instance deployments
8. **Batch Email Sending** - Async email processing
9. **Email Analytics** - Track opens and clicks
10. **Multi-language Support** - i18n for email content
