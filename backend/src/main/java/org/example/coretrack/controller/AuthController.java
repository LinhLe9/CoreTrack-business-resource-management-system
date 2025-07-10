package org.example.coretrack.controller;

import org.example.coretrack.dto.auth.AuthResponse;
import org.example.coretrack.dto.auth.LoginRequest;
import org.example.coretrack.dto.auth.RegistrationRequest;
import org.example.coretrack.dto.auth.ResendValidationTokenResquest;
import org.example.coretrack.dto.auth.ValidationTokenRequest;
import org.example.coretrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
