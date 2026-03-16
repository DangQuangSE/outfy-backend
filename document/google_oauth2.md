# Google OAuth2 Login - Architecture & Implementation Details

## Overview

Cho phép người dùng đăng nhập bằng tài khoản Google mà không cần tạo mật khẩu. Sử dụng cách tiếp cận **REST API** (không redirect server-side):

1. **Frontend** dùng Google Sign-In SDK để lấy **ID Token**
2. **Backend** nhận ID Token → verify với Google API → tìm/tạo user → trả JWT tokens

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Frontend (React / Mobile)                      │
│                                                                   │
│  1. User clicks "Sign in with Google"                            │
│  2. Google Sign-In SDK returns ID Token                          │
│  3. POST /api/v1/auth/google { idToken: "..." }                 │
│  4. Receive JWT tokens (access + refresh)                        │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                     REST API (Spring Boot)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  AuthController                                                   │
│  ├─ POST /register          → AuthService                       │
│  ├─ POST /verify-email      → AuthService                       │
│  ├─ POST /login             → AuthService                       │
│  ├─ POST /google            → GoogleAuthService              ⭐ │
│  ├─ POST /refresh           → AuthService                       │
│  ├─ POST /logout            → AuthService                  🔒  │
│  └─ GET  /me                → AuthService                  🔒  │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                      Service Layer                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  GoogleAuthService  ⭐ (NEW)                                     │
│  ├─ Verify Google ID Token (GoogleIdTokenVerifier)               │
│  ├─ Extract user info (email, name, avatar, sub)                 │
│  ├─ Find or create user in database                              │
│  └─ Reuse AuthService.generateAuthResponse() for JWT tokens     │
│                                                                   │
│  AuthService (MODIFIED)                                           │
│  ├─ generateAuthResponse() → public (was private)                │
│  └─ login() → block if authProvider == GOOGLE                    │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                      Database (PostgreSQL)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  users table                                                      │
│  ├─ auth_provider (VARCHAR) → 'LOCAL' | 'GOOGLE'                │
│  ├─ google_id (VARCHAR, UNIQUE) → Google sub ID                  │
│  └─ password (NULLABLE) → null cho user Google                   │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                    External Services                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Google OAuth2 API                                                │
│  └─ Verifies ID Token authenticity and returns user info         │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow Diagram

### Google Login Flow
```
Frontend           Google          Controller        GoogleAuthService     Database
  │                  │                 │                    │                 │
  ├─Sign in with────>│                 │                    │                 │
  │  Google SDK      │                 │                    │                 │
  │<─ID Token────────│                 │                    │                 │
  │                  │                 │                    │                 │
  ├─POST /google─────────────────────>│                    │                 │
  │  { idToken }     │                 │                    │                 │
  │                  │                 ├─googleLogin()─────>│                 │
  │                  │                 │                    │                 │
  │                  │                 │                    ├─verify token───>│
  │                  │                 │                    │  (Google API)   Google API
  │                  │                 │                    │<─payload────────│
  │                  │                 │                    │                 │
  │                  │                 │                    ├─find by email──>│
  │                  │                 │                    │                 ├─SELECT users
  │                  │                 │                    │                 │
  │                  │                 │                    │  ┌──────────────────────┐
  │                  │                 │                    │  │ User exists?          │
  │                  │                 │                    │  │ YES → link Google acc │
  │                  │                 │                    │  │ NO  → create new user │
  │                  │                 │                    │  └──────────────────────┘
  │                  │                 │                    │                 │
  │                  │                 │                    ├─save user──────>│
  │                  │                 │                    │                 ├─INSERT/UPDATE
  │                  │                 │                    │                 │
  │                  │                 │                    ├─generate JWT    │
  │                  │                 │                    ├─save refresh───>│
  │                  │                 │                    │     token        ├─INSERT
  │                  │                 │<─AuthResponse──────│                 │
  │<─200 OK──────────────────────────>│                    │                 │
  │  { accessToken,  │                 │                    │                 │
  │    refreshToken, │                 │                    │                 │
  │    user }        │                 │                    │                 │
```

## Entity Changes

### User Entity (Modified)
```
┌──────────────────────────────────┐
│            User                   │
├──────────────────────────────────┤
│ id (PK)                           │
│ email (UNIQUE, NOT NULL)          │
│ password (NULLABLE)           ⭐ │  ← null cho user Google
│ full_name                         │
│ phone                             │
│ date_of_birth                     │
│ gender                            │
│ avatar_url                        │  ← tự lấy từ Google
│ role                              │
│ auth_provider (NOT NULL)      ⭐ │  ← 'LOCAL' | 'GOOGLE'
│ google_id (UNIQUE)            ⭐ │  ← Google sub ID
│ is_active                         │
│ is_email_verified                 │  ← true cho user Google
│ email_verified_at                 │
│ created_at                        │
│ updated_at                        │
└──────────────────────────────────┘
```

### AuthProvider Enum (New)
```java
public enum AuthProvider {
    LOCAL,   // Đăng ký bằng email + password
    GOOGLE   // Đăng nhập bằng Google
}
```

## Files Changed

| File | Action | Description |
|------|--------|-------------|
| `pom.xml` | MODIFY | Thêm `google-api-client` dependency |
| `AuthProvider.java` | NEW | Enum: LOCAL, GOOGLE |
| `GoogleLoginRequest.java` | NEW | DTO: `{ idToken: string }` |
| `GoogleAuthService.java` | NEW | Verify Google ID Token, tìm/tạo user |
| `User.java` | MODIFY | Thêm `authProvider`, `googleId`; password nullable |
| `AuthService.java` | MODIFY | `generateAuthResponse()` → public; guard Google user login |
| `AuthController.java` | MODIFY | Thêm `POST /google` endpoint |
| `application.properties` | MODIFY | Thêm `app.google.client-id` |

## Request/Response Examples

### Google Login Request
```json
POST /api/v1/auth/google
Content-Type: application/json

{
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Google Login Success Response (New User)
```json
HTTP/1.1 200 OK

{
  "success": true,
  "message": "Google login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 1800,
    "user": {
      "id": 5,
      "email": "user@gmail.com",
      "fullName": "Nguyễn Văn A",
      "avatarUrl": "https://lh3.googleusercontent.com/...",
      "role": "USER",
      "isEmailVerified": true,
      "createdAt": "2026-03-16T16:00:00"
    }
  },
  "timestamp": "2026-03-16T16:00:00"
}
```

### Google Login Success Response (Existing User - Account Linked)
```json
HTTP/1.1 200 OK

{
  "success": true,
  "message": "Google login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 1800,
    "user": {
      "id": 1,
      "email": "user@gmail.com",
      "fullName": "Nguyễn Văn A",
      "role": "USER",
      "isEmailVerified": true,
      "createdAt": "2026-03-15T10:00:00"
    }
  },
  "timestamp": "2026-03-16T16:00:00"
}
```

## Error Responses

### Invalid Google ID Token
```json
HTTP/1.1 400 Bad Request

{
  "success": false,
  "message": "Invalid Google ID token",
  "data": null
}
```

### Google Email Not Verified
```json
HTTP/1.1 400 Bad Request

{
  "success": false,
  "message": "Google email is not verified",
  "data": null
}
```

### Failed to Verify Token (Network/Server Error)
```json
HTTP/1.1 400 Bad Request

{
  "success": false,
  "message": "Failed to verify Google ID token",
  "data": null
}
```

### Account Inactive
```json
HTTP/1.1 400 Bad Request

{
  "success": false,
  "message": "Account is inactive",
  "data": null
}
```

### Login with Password for Google Account
```json
POST /api/v1/auth/login
{ "email": "user@gmail.com", "password": "abc123" }
```
```json
HTTP/1.1 400 Bad Request

{
  "success": false,
  "message": "This account uses Google login. Please sign in with Google.",
  "data": null
}
```

## Business Logic Details

### Tìm hoặc Tạo User

```
Google ID Token verified
        │
        ▼
  Find user by email
        │
   ┌────┴────┐
   │         │
  YES       NO
   │         │
   ▼         ▼
 User có   Tạo user mới:
 sẵn →     • email = Google email
 Link      • password = null
 Google     • fullName = Google name
 account:   • avatarUrl = Google picture
 • set      • googleId = Google sub
   authPro  • authProvider = GOOGLE
   vider    • isEmailVerified = true
   =GOOGLE  • role = USER
 • set      • isActive = true
   googleId
 • set
   avatar
   (if null)
```

### Quy tắc quan trọng

| Scenario | Behavior |
|----------|----------|
| User mới, login Google lần đầu | Tạo user mới, password = null, isEmailVerified = true |
| User đã đăng ký email/password, login Google cùng email | Link Google account, authProvider → GOOGLE |
| User Google, thử login bằng password | Bị chặn: "Please sign in with Google" |
| Google email chưa verified | Bị chặn: "Google email is not verified" |

## Configuration

### application.properties
```properties
# Google OAuth2
app.google.client-id=${GOOGLE_CLIENT_ID}
```

### Environment Variables (.env)
```
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### Cách lấy Google Client ID

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo hoặc chọn project
3. Vào **APIs & Services** → **Credentials**
4. Click **Create Credentials** → **OAuth 2.0 Client ID**
5. Chọn **Application type**: Web application
6. Thêm **Authorized JavaScript origins**: `http://localhost:3000` (frontend URL)
7. Copy **Client ID** → thêm vào `.env`

## Frontend Integration

### React (với @react-oauth/google)

```bash
npm install @react-oauth/google
```

```jsx
// App.jsx - Wrap app with GoogleOAuthProvider
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
  return (
    <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
      <LoginPage />
    </GoogleOAuthProvider>
  );
}
```

```jsx
// LoginPage.jsx
import { GoogleLogin } from '@react-oauth/google';

function LoginPage() {
  const handleGoogleSuccess = async (response) => {
    const res = await fetch('/api/v1/auth/google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken: response.credential })
    });
    const data = await res.json();
    
    if (data.success) {
      // Lưu tokens và redirect
      localStorage.setItem('accessToken', data.data.accessToken);
      localStorage.setItem('refreshToken', data.data.refreshToken);
    }
  };

  return (
    <GoogleLogin
      onSuccess={handleGoogleSuccess}
      onError={() => console.log('Google Login Failed')}
    />
  );
}
```

## Security Considerations

- ✅ **Server-side verification** — ID Token được verify trực tiếp với Google API, không trust client
- ✅ **Audience check** — Verify token audience khớp với Client ID của app
- ✅ **Email verified check** — Chỉ chấp nhận Google account đã verified email
- ✅ **Stateless JWT** — Sau khi verify Google token, trả về JWT giống flow login thường
- ✅ **Refresh token rotation** — Reuse cùng cơ chế refresh token của hệ thống
- ✅ **Account linking** — Nếu user đã đăng ký bằng email, login Google sẽ link account
- ✅ **Password protection** — User Google không thể login bằng password (và ngược lại)
