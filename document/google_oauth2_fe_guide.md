# Google OAuth2 Login - Hướng Dẫn Tích Hợp Cho Frontend

## Tổng Quan

Cho phép người dùng đăng nhập bằng tài khoản Google mà không cần tạo mật khẩu.

### Luồng Hoạt Động

```
1. Frontend: Hiển thị nút "Đăng nhập bằng Google"
2. User click → Google SDK hiển thị popup đăng nhập
3. User đăng nhập thành công → Frontend nhận ID Token
4. Frontend: Gọi API /api/v1/auth/google với ID Token
5. Backend: Verify token → Tạo/Tìm user → Trả về JWT tokens
6. Frontend: Lưu tokens → Chuyển đến trang chủ
```

---

## Cấu Hình

### 1. Cấu Hình Google Client ID

Thêm vào file `.env` ở backend:

```env
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### 2. Cấu Hình Google OAuth ở Google Cloud Console

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo hoặc chọn project
3. Vào **APIs & Services** → **Credentials**
4. Click **Create Credentials** → **OAuth 2.0 Client ID**
5. Chọn **Application type**: Web application
6. Thêm **Authorized JavaScript origins**:
   - `http://localhost:3000` (development)
   - `https://your-domain.com` (production)
7. Thêm **Authorized redirect URIs**:
   - `http://localhost:3000` (development)
   - `https://your-domain.com` (production)
8. Copy **Client ID** vào `.env`

---

## API Endpoints

### 1. Đăng Nhập Bằng Google

```http
POST /api/v1/auth/google
Content-Type: application/json

{
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response Thành Công (User Mới):**
```json
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

**Response Thành Công (User Đã Tồn Tại):**
```json
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

---

## Các Trường Hợp Lỗi

### 1. Token Không Hợp Lệ

```json
{
  "success": false,
  "message": "Invalid Google ID token",
  "data": null
}
```

**Nguyên nhân:** Token bị sai hoặc hết hạn

### 2. Email Google Chưa Xác Thực

```json
{
  "success": false,
  "message": "Google email is not verified",
  "data": null
}
```

**Nguyên nhân:** User chưa xác thực email với Google

### 3. Lỗi Xác Thực Token

```json
{
  "success": false,
  "message": "Failed to verify Google ID token",
  "data": null
```

**Nguyên nhân:** Lỗi mạng hoặc server Google không phản hồi

### 4. Tài Khoản Bị Vô Hiệu Hóa

```json
{
  "success": false,
  "message": "Account is inactive",
  "data": null
}
```

**Nguyên nhân:** Tài khoản bị admin vô hiệu hóa

---

## Tích Hợp Frontend

### Sử Dụng Thư Viện @react-oauth/google

#### Cài Đặt

```bash
npm install @react-oauth/google
```

#### Cấu Hình App (App.jsx)

```jsx
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
  const clientId = "YOUR_GOOGLE_CLIENT_ID";
  
  return (
    <GoogleOAuthProvider clientId={clientId}>
      <YourAppComponents />
    </GoogleOAuthProvider>
  );
}

export default App;
```

#### Component Đăng Nhập (LoginPage.jsx)

```jsx
import { GoogleLogin } from '@react-oauth/google';
import axios from 'axios';

function LoginPage() {
  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      const response = await axios.post(
        'http://localhost:8080/api/v1/auth/google',
        { idToken: credentialResponse.credential },
        { headers: { 'Content-Type': 'application/json' } }
      );

      const { data } = response.data;

      if (data.success) {
        // Lưu tokens
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        
        // Lưu thông tin user
        localStorage.setItem('user', JSON.stringify(data.user));
        
        // Chuyển đến trang chủ
        window.location.href = '/home';
      }
    } catch (error) {
      console.error('Google login failed:', error);
      const errorMessage = error.response?.data?.message || 'Login failed';
      alert(errorMessage);
    }
  };

  const handleGoogleError = () => {
    console.log('Google Login Failed');
    alert('Đăng nhập Google thất bại. Vui lòng thử lại.');
  };

  return (
    <div className="login-container">
      <h1>Đăng Nhập</h1>
      <GoogleLogin
        onSuccess={handleGoogleSuccess}
        onError={handleGoogleError}
        useOneTap={false}
        theme="outline"
        size="large"
        text="signin_with"
        shape="rectangular"
      />
    </div>
  );
}

export default LoginPage;
```

### Sử Dụng Google Identity Services (Không Thư Viện)

#### HTML

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>

<div id="g_id_onload"
     data-client_id="YOUR_GOOGLE_CLIENT_ID"
     data-context="signin"
     data-ux_mode="popup"
     data-callback="handleCredentialResponse"
     data-auto_prompt="false">
</div>

<div class="g_id_signin"
     data-type="standard"
     data-shape="rectangular"
     data-theme="outline"
     data-text="signin_with"
     data-size="large"
     data-logo_alignment="left">
</div>

<script>
  async function handleCredentialResponse(response) {
    try {
      const res = await fetch('http://localhost:8080/api/v1/auth/google', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ idToken: response.credential }),
      });

      const data = await res.json();

      if (data.success) {
        localStorage.setItem('accessToken', data.data.accessToken);
        localStorage.setItem('refreshToken', data.data.refreshToken);
        localStorage.setItem('user', JSON.stringify(data.data.user));
        window.location.href = '/home';
      } else {
        alert(data.message);
      }
    } catch (error) {
      console.error('Login error:', error);
      alert('Đăng nhập thất bại');
    }
  }
</script>
```

---

## Quy Tắc Nghiệp Vụ

| Scenario | Behavior |
|----------|----------|
| User mới, login Google lần đầu | Tạo user mới, password = null, isEmailVerified = true |
| User đã đăng ký email/password, login Google cùng email | Link Google account, authProvider → GOOGLE |
| User Google, thử login bằng password | Bị chặn: "This account uses Google login. Please sign in with Google." |
| Google email chưa verified | Bị chặn: "Google email is not verified" |

---

## Xử Lý Token

### Lưu Tokens

```javascript
// Sau khi login thành công
localStorage.setItem('accessToken', data.accessToken);
localStorage.setItem('refreshToken', data.refreshToken);
```

### Sử Dụng Token Trong Requests

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

// Thêm token vào mọi request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### Refresh Token

Xem thêm tài liệu authentication về cách refresh token khi hết hạn.

---

## Demo Checklist

- [ ] Cấu hình Google Client ID trong `.env`
- [ ] Thêm origins vào Google Cloud Console
- [ ] Cài đặt thư viện `@react-oauth/google`
- [ ] Wrap app với `GoogleOAuthProvider`
- [ ] Implement `GoogleLogin` button
- [ ] Xử lý response từ Google
- [ ] Gọi API `/api/v1/auth/google`
- [ ] Lưu tokens vào localStorage
- [ ] Xử lý các trường hợp lỗi
- [ ] Test đăng nhập với tài khoản Google mới
- [ ] Test đăng nhập với tài khoản Google đã link

---

## Troubleshooting

### Lỗi "IdpIframe initialisation failed"

**Nguyên nhân:** Client ID chưa đúng hoặc chưa thêm domain vào Authorized JavaScript origins

**Cách sửa:**
1. Kiểm tra Client ID trong code
2. Thêm domain vào Google Cloud Console → Credentials → OAuth 2.0

### Lỗi CORS

**Nguyên nhân:** Frontend chạy domain chưa được allow

**Cách sửa:**
1. Thêm frontend URL vào Authorized JavaScript origins
2. Thêm backend URL vào Authorized redirect URIs nếu cần

### Token hết hạn

**Xử lý:**
- Sử dụng refresh token để lấy access token mới
- Hoặc yêu user đăng nhập lại

---

## Liên Kết Tham Khảo

- [Google Identity Services](https://developers.google.com/identity/sign-in/web/sign-in)
- [@react-oauth/google](https://github.com/MomenSherif/react-oauth/google)
- [Google Cloud Console](https://console.cloud.google.com/)

