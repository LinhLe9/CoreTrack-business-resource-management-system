# Frontend Password Reset Implementation

## Overview
Frontend implementation cho chức năng đổi mật khẩu khi quên mật khẩu, tích hợp với backend API.

## Components Created

### 1. ForgotPasswordForm.tsx
- **Location**: `src/components/auth/ForgotPasswordForm.tsx`
- **Purpose**: Form để người dùng nhập email và yêu cầu đổi mật khẩu
- **Features**:
  - Form validation
  - Loading states
  - Success/error notifications
  - Auto-redirect sau khi gửi email thành công

### 2. ResetPasswordForm.tsx
- **Location**: `src/components/auth/ResetPasswordForm.tsx`
- **Purpose**: Form để người dùng đặt lại mật khẩu với token
- **Features**:
  - Token validation từ URL parameter
  - Password confirmation
  - Password strength validation (minimum 6 characters)
  - Loading states
  - Error handling cho invalid/expired tokens

## Pages Created

### 1. Forgot Password Page
- **Route**: `/forgot-password`
- **File**: `src/app/(auth)/forgot-password/page.tsx`
- **Component**: `ForgotPasswordForm`

### 2. Reset Password Page
- **Route**: `/reset-password?token={token}`
- **File**: `src/app/(auth)/reset-password/page.tsx`
- **Component**: `ResetPasswordForm`

## Updated Components

### LoginForm.tsx
- **Added**: Link "Forgot your password? Click here" phía dưới form login
- **Location**: Dưới nút Login
- **Styling**: Consistent với design system hiện tại

## User Flow

### 1. Quên mật khẩu
```
Login Page → Click "Forgot your password? Click here" 
→ Forgot Password Page → Enter email → Submit 
→ Success message → Redirect to Login
```

### 2. Đặt lại mật khẩu
```
Email link → Reset Password Page → Validate token 
→ Enter new password → Confirm password → Submit 
→ Success message → Redirect to Login
```

## API Integration

### Forgot Password
```typescript
POST /api/auth/forgot-password
Body: { email: string }
Response: Success message or error
```

### Validate Token
```typescript
POST /api/auth/validate-reset-token
Body: { token: string }
Response: "Token is valid" or error
```

### Reset Password
```typescript
POST /api/auth/reset-password
Body: { token: string, newPassword: string }
Response: Success message or error
```

## Features

### ✅ User Experience
- Loading states cho tất cả API calls
- Toast notifications cho success/error
- Auto-redirect sau khi hoàn thành
- Form validation
- Password confirmation

### ✅ Security
- Token validation trước khi hiển thị form reset
- Password strength validation
- Error handling cho invalid/expired tokens

### ✅ UI/UX
- Consistent với design system hiện tại
- Responsive design
- Clear error messages
- Intuitive navigation

### ✅ Error Handling
- Network errors
- Invalid tokens
- Expired tokens
- Password mismatch
- Weak passwords

## Styling

Sử dụng Chakra UI components:
- `Box` - Container
- `Button` - Submit buttons
- `FormControl` - Form fields
- `Input` - Text inputs
- `Heading` - Page titles
- `Text` - Descriptive text
- `Link` - Navigation links
- `Alert` - Error messages
- `VStack` - Vertical layout

## Testing

### Manual Testing Checklist
1. **Forgot Password Flow**:
   - [ ] Click "Forgot your password? Click here" từ login page
   - [ ] Enter valid email
   - [ ] Submit form
   - [ ] Verify success message
   - [ ] Check email received

2. **Reset Password Flow**:
   - [ ] Click link từ email
   - [ ] Verify token validation
   - [ ] Enter new password
   - [ ] Confirm password
   - [ ] Submit form
   - [ ] Verify success message
   - [ ] Try login với password mới

3. **Error Scenarios**:
   - [ ] Invalid email
   - [ ] Invalid token
   - [ ] Expired token
   - [ ] Password mismatch
   - [ ] Weak password

## Integration with Backend

### Email Template
Backend gửi email với link format:
```
http://localhost:3030/reset-password?token={token}
```

### Token Handling
- Token được lấy từ URL parameter
- Validation được thực hiện ngay khi load page
- Token được gửi trong request body cho API calls

## Next Steps

1. **Production Deployment**:
   - Update email template URL trong backend
   - Configure domain cho production

2. **Enhancements**:
   - Add password strength indicator
   - Add rate limiting cho forgot password requests
   - Add CAPTCHA cho forgot password form
   - Add email validation pattern

3. **Testing**:
   - Add unit tests cho components
   - Add integration tests cho API calls
   - Add E2E tests cho complete flow

## Troubleshooting

### Common Issues
1. **Token not found**: Check URL parameter
2. **Invalid token**: Token expired or invalid
3. **Network error**: Check API endpoint
4. **Email not received**: Check spam folder

### Debug Steps
1. Check browser console cho errors
2. Verify API endpoints
3. Check network tab cho failed requests
4. Verify email configuration trong backend
