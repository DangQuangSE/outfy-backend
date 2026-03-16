# Email Verification & Authentication Integration Guide

## Overview

This document provides FE integration requirements for the email verification and authentication system.

## API Base URL

```
Base URL: http://localhost:8080/api/v1
```

## Response Format

All API responses follow this format:

```json
{
  "success": true | false,
  "message": "Description message",
  "data": { ... }
}
```

---

## API Endpoints

### 1. Register User

| Item | Value |
|------|-------|
| Endpoint | `POST /auth/register` |
| Auth | No |
| Content-Type | `application/json` |

**Request:**

```json
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "gender": "MALE"
}
```

**Response (201 Created):**

```json
{
  "success": true,
  "message": "Registration successful. Please check your email to verify your account.",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "gender": "MALE",
    "role": "USER",
    "isEmailVerified": false,
    "emailVerifiedAt": null,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

**Validation Rules:**
- `email`: Required, valid email format
- `password`: Required, min 6 characters
- `fullName`: Required
- `gender`: Optional, enum: MALE, FEMALE, OTHER

---

### 2. Verify Email (OTP)

| Item | Value |
|------|-------|
| Endpoint | `POST /auth/verify-email` |
| Auth | No |
| Content-Type | `application/json` |

**Request:**

```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": null
}
```

**Error Responses:**

- Invalid OTP (400):
```json
{
  "success": false,
  "message": "Invalid OTP. Please try again.",
  "data": null
}
```

- OTP Expired (400):
```json
{
  "success": false,
  "message": "OTP has expired or does not exist. Please request a new one.",
  "data": null
}
```

- Email Already Verified (409):
```json
{
  "success": false,
  "message": "Email is already verified",
  "data": null
}
```

---

### 3. Resend Verification Email

| Item | Value |
|------|-------|
| Endpoint | `POST /auth/resend-verification-email` |
| Auth | No |
| Content-Type | `application/json` |

**Request:**

```json
{
  "email": "user@example.com"
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Verification email sent successfully",
  "data": null
}
```

**Error Responses:**

- User Not Found (400):
```json
{
  "success": false,
  "message": "User not found",
  "data": null
}
```

- Email Already Verified (409):
```json
{
  "success": false,
  "message": "Email is already verified",
  "data": null
}
```

---

### 4. Login

| Item | Value |
|------|-------|
| Endpoint | `POST /auth/login` |
| Auth | No |
| Content-Type | `application/json` |

**Request:**

```json
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Response (200 OK):**

```json
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
      "email": "user@example.com",
      "fullName": "John Doe",
      "isEmailVerified": true
    }
  }
}
```

**Error Responses:**

- Email Not Verified (400):
```json
{
  "success": false,
  "message": "Please verify your email before logging in.",
  "data": null
}
```

- Invalid Credentials (400):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

---

### 5. Refresh Token

| Item | Value |
|------|-------|
| Endpoint | `POST /auth/refresh` |
| Auth | No |
| Content-Type | `application/json` |

**Request:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (200 OK):**

```json
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

---

### 6. Logout

| Item | Value |
|------|-------|
| Endpoint | `POST /auth/logout` |
| Auth | Yes (Bearer Token) |
| Content-Type | `application/json` |

**Request Header:**

```
Authorization: Bearer <accessToken>
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

---

### 7. Get Current User (/me)

| Item | Value |
|------|-------|
| Endpoint | `GET /auth/me` |
| Auth | Yes (Bearer Token) |
| Content-Type | `application/json` |

**Request Header:**

```
Authorization: Bearer <accessToken>
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "role": "USER",
    "isEmailVerified": true,
    "emailVerifiedAt": "2024-01-15T10:35:00",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

## TypeScript Types

### lib/types/auth.ts

```typescript
// User types
export interface User {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  role: 'USER' | 'ADMIN';
  isEmailVerified: boolean;
  emailVerifiedAt?: string;
  createdAt: string;
}

// Auth Response
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

// Request DTOs
export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
}

export interface VerifyEmailRequest {
  email: string;
  otp: string;
}

export interface ResendVerificationRequest {
  email: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

// API Response wrapper
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: Record<string, string>;
}
```

---

## API Functions

### lib/api/auth.ts

```typescript
import {
  RegisterRequest,
  VerifyEmailRequest,
  ResendVerificationRequest,
  LoginRequest,
  RefreshTokenRequest,
  AuthResponse,
  User,
  ApiResponse,
} from '@/types/auth';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

// Register
export async function register(data: RegisterRequest): Promise<ApiResponse<User>> {
  const res = await fetch(`${API_URL}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return res.json();
}

// Verify Email
export async function verifyEmail(data: VerifyEmailRequest): Promise<ApiResponse<null>> {
  const res = await fetch(`${API_URL}/auth/verify-email`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return res.json();
}

// Resend Verification Email
export async function resendVerification(data: ResendVerificationRequest): Promise<ApiResponse<null>> {
  const res = await fetch(`${API_URL}/auth/resend-verification-email`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return res.json();
}

// Login
export async function login(data: LoginRequest): Promise<ApiResponse<AuthResponse>> {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return res.json();
}

// Refresh Token
export async function refreshToken(data: RefreshTokenRequest): Promise<ApiResponse<AuthResponse>> {
  const res = await fetch(`${API_URL}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return res.json();
}

// Logout
export async function logout(accessToken: string): Promise<ApiResponse<null>> {
  const res = await fetch(`${API_URL}/auth/logout`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${accessToken}`,
    },
  });
  return res.json();
}

// Get Current User
export async function getCurrentUser(accessToken: string): Promise<ApiResponse<User>> {
  const res = await fetch(`${API_URL}/auth/me`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  return res.json();
}
```

---

## Token Storage

### Access Token
- **Storage**: Memory (memory state) or sessionStorage
- **Usage**: Attach to Authorization header for authenticated requests
- **Lifetime**: 30 minutes (1800 seconds)

### Refresh Token
- **Storage**: httpOnly cookie (recommended) or localStorage
- **Usage**: Use to get new access token when expired
- **Lifetime**: 7 days (604800000 ms)

### Token Refresh Logic
```typescript
// Check if token is expired
function isTokenExpired(expiresIn: number): boolean {
  const expiryTime = localStorage.getItem('tokenExpiry');
  if (!expiryTime) return true;
  return Date.now() > parseInt(expiryTime);
}

// Before making authenticated API call
async function authenticatedFetch(url: string, options: RequestInit = {}) {
  let accessToken = localStorage.getItem('accessToken');
  
  // Check if token needs refresh (5 minutes before expiry)
  if (isTokenExpired(1800 - 300)) {
    const refreshToken = localStorage.getItem('refreshToken');
    const response = await refreshToken({ refreshToken });
    if (response.success) {
      accessToken = response.data.accessToken;
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('tokenExpiry', String(Date.now() + response.data.expiresIn * 1000));
    }
  }
  
  return fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      Authorization: `Bearer ${accessToken}`,
    },
  });
}
```

---

## Demo Flow

### Complete Email Verification Demo Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Register                                                 │
│    POST /auth/register                                      │
│    → User created with isEmailVerified = false              │
│    → OTP sent to email (check console/log)                 │
│    → NO tokens returned                                     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Verify Email                                            │
│    POST /auth/verify-email                                   │
│    { email, otp }                                           │
│    → isEmailVerified = true                                 │
│    → Can now login                                          │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Login                                                    │
│    POST /auth/login                                         │
│    → Returns accessToken + refreshToken                     │
│    → Store tokens securely                                  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Authenticated Actions                                    │
│    - GET /auth/me (get user profile)                        │
│    - POST /auth/logout (logout)                             │
└─────────────────────────────────────────────────────────────┘
```

### OTP Input UI Recommendations

1. **6-digit input**: Use single input field or 6 separate inputs
2. **Auto-focus**: Auto-move to next input on digit entry
3. **Paste support**: Allow pasting full 6-digit code
4. **Timer**: Show countdown for resend (5 minutes)
5. **Resend button**: Disable during countdown

---

## Error Handling

### Global Error Handler

```typescript
function handleApiError(error: any): string {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  return 'An unexpected error occurred';
}

// Common error messages to handle:
- "Invalid OTP. Please try again."
- "OTP has expired or does not exist. Please request a new one."
- "Email is already verified"
- "Please verify your email before logging in."
- "Invalid email or password"
- "Email already registered"
- "Invalid refresh token"
- "Refresh token has expired"
- "Refresh token has been revoked"
```

---

## Security Checklist

- [ ] Store tokens securely (httpOnly cookies recommended)
- [ ] Include Authorization header for protected endpoints
- [ ] Handle token refresh before expiration
- [ ] Clear tokens on logout
- [ ] Never log sensitive data
- [ ] Use HTTPS in production
- [ ] Validate email format before sending
- [ ] Sanitize user input

---

## Configuration

### Environment Variables (.env.local)

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

---

## Notes

1. **OTP is in-memory**: Restarting BE clears all pending OTPs
2. **Login blocked**: Until email is verified
3. **Registration returns NO tokens**: User must verify email first
4. **Token expiry**: 30 min access, 7 days refresh
5. **Demo**: Use console to see OTP in development (or check email)

