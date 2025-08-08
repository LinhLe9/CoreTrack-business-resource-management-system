package org.example.coretrack.controller;

import org.example.coretrack.dto.auth.AuthResponse;
import org.example.coretrack.dto.auth.LoginRequest;
import org.example.coretrack.dto.auth.RegistrationRequest;
import org.example.coretrack.dto.auth.ResendValidationTokenResquest;
import org.example.coretrack.dto.auth.ValidationTokenRequest;
import org.example.coretrack.dto.auth.UserDetailResponse;
import org.example.coretrack.dto.auth.ForgotPasswordRequest;
import org.example.coretrack.dto.auth.ResetPasswordRequest;
import org.example.coretrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    // @Autowired
    // private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerOwner(@RequestBody RegistrationRequest registrationRequest) {
        userService.registerOwner(registrationRequest);
        return ResponseEntity.ok("Owner registered successfully! Please check your email for verification.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if ("valid".equals(result)) {
            return ResponseEntity.ok("Account activated successfully!");
        }
        return ResponseEntity.badRequest().body("Invalid or expired token.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody ValidationTokenRequest request) {
        String result = userService.validateVerificationToken(request.getToken());

        if ("valid".equals(result)) {
            return ResponseEntity.ok("Account activated successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/resend-token")
    public ResponseEntity<?> resendVerificationToken(@RequestBody ResendValidationTokenResquest request) {
        String result = userService.resendValidationToken(request.getEmail());
        if ("valid".equals(result)) {
        return ResponseEntity.ok("Verification token resent successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to resend token. Email not found or user already verified.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userService.login(loginRequest);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            userService.logout();
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during logout: " + e.getMessage());
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDetailResponse> getCurrentUserDetails() {
        try {
            // Get current user email from security context
            String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
            
            UserDetailResponse userDetails = userService.getCurrentUserDetails(currentUserEmail);
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== PASSWORD RESET ENDPOINTS =====
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            String result = userService.forgotPassword(request.getEmail());
            if ("valid".equals(result)) {
                return ResponseEntity.ok("Password reset email sent successfully. Please check your email.");
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            String result = userService.resetPassword(request.getToken(), request.getNewPassword());
            if ("valid".equals(result)) {
                return ResponseEntity.ok("Password reset successfully.");
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestBody ValidationTokenRequest request) {
        try {
            String result = userService.validatePasswordResetToken(request.getToken());
            if ("valid".equals(result)) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
