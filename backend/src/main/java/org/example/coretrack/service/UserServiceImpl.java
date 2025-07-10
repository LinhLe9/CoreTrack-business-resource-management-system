package org.example.coretrack.service;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.dto.auth.AuthResponse;
import org.example.coretrack.dto.auth.CreateUserRequest;
import org.example.coretrack.dto.auth.LoginRequest;
import org.example.coretrack.dto.auth.RegistrationRequest;
import org.example.coretrack.model.auth.Role;
import org.example.coretrack.model.auth.VerificationToken;
//import org.example.coretrack.model.PasswordResetToken;
import org.example.coretrack.repository.UserRepository;
// import org.example.coretrack.repository.PasswordResetTokenRepository;
import org.example.coretrack.repository.VerificationTokenRepository;
import org.example.coretrack.security.JwtService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//for the email
import jakarta.mail.MessagingException;


import org.springframework.security.authentication.AuthenticationManager;

import java.util.Calendar;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    // private PasswordResetTokenRepository passwordResetTokenRepository;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private final EmailService mailSender;
    private final JwtService jwtService;

    private final String defaultPassword = "Welcome1234!";

    public UserServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService mailSender,
            JwtService jwtService,
            VerificationTokenRepository tokenRepository
            // PasswordResetTokenRepository passwordResetTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        // this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public AuthResponse registerOwner(RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.OWNER);
        user.setEnabled(false); // Disabled until verification
        userRepository.save(user);

        // create token and save to database by helper method
        String token = UUID.randomUUID().toString();
        saveVerificationTokenForUser(user, token);

        // send email
        sendVerificationEmail(user,token);
        return new AuthResponse(null, user.getEmail(), user.getRole());
    }

    // saveVerificationTokenForUser helper method
    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }

    // send email helper method
    // Email sending helpers
    private void sendVerificationEmail(User user, String code) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE: " + code;
        StringBuilder htmlMessageBuilder = new StringBuilder();
        htmlMessageBuilder.append("<html>")
                .append("<body style=\"font-family: Arial, sans-serif;\">")
                .append("<div style=\"background-color: #f5f5f5; padding: 20px;\">")
                .append("<h2 style=\"color: #333;\">Welcome to our app!</h2>");
        if (!user.getRole().equals(Role.OWNER)) { // check role
            htmlMessageBuilder.append("<p style=\"font-size: 16px;\">Please using the following login information to login to our system:")
                    .append("<br> <b> Email: ").append(user.getEmail())
                    .append("<br> <b> Password: ").append(defaultPassword).append("</p>");
        }
        htmlMessageBuilder.append("<p style=\"font-size: 16px;\">Please enter the verification code below within 24 hours to continue:</p>")
                .append("<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">")
                .append("<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">").append(verificationCode).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        String htmlMessage = htmlMessageBuilder.toString();
        try {
            mailSender.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void  createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(createUserRequest.getEmail());
        user.setUsername(createUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setRole(createUserRequest.getRole());
        user.setEnabled(false); // Employee accounts are unenabled by default
        userRepository.save(user);

        // create random token
        String token = UUID.randomUUID().toString();
        saveVerificationTokenForUser(user, token);

        // send email
        sendVerificationEmail(user,token);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "invalidToken";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public String resendValidationToken(String email){
        User foundUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("Email does not exists"));
        if (foundUser.isEnabled()) {
            throw new IllegalStateException ("Validated email"); // User đã được kích hoạt rồi
        }
        String newToken = UUID.randomUUID().toString();
        VerificationToken token = tokenRepository.findByUser(foundUser)
                                    .orElseThrow(() -> new IllegalStateException("Token for this user does not exist"));
        token.setToken(newToken);

        tokenRepository.save(token);
        sendVerificationEmail(foundUser,newToken);
        
        return "valid";
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, loginRequest.getEmail(), user.getRole());
    }

//    @Override
//    public void createPasswordResetTokenForUser(User user, String token) {
//        PasswordResetToken myToken = new PasswordResetToken(token, user);
//        passwordResetTokenRepository.save(myToken);
//    }
//
//    @Override
//    public String validatePasswordResetToken(String token) {
//        return "";
//    }
//
//    @Override
//    public void changeUserPassword(User user, String password) {
//
//    }
//
//    @Override
//    public User getUserByPasswordResetToken(String token) {
//        return null;
//    }
}
