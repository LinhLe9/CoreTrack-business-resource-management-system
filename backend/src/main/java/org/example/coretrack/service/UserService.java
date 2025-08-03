package org.example.coretrack.service;

import org.example.coretrack.dto.auth.AuthResponse;
import org.example.coretrack.dto.auth.CreateUserRequest;
import org.example.coretrack.dto.auth.LoginRequest;
import org.example.coretrack.dto.auth.RegistrationRequest;
import org.example.coretrack.dto.auth.UserDetailResponse;
import org.example.coretrack.model.auth.User;
import java.util.List;

public interface UserService {
    // method to register
    AuthResponse registerOwner(RegistrationRequest registrationRequest);

    // method for owner register other employees to system
    void createUser(CreateUserRequest createUserRequest);

    void saveVerificationTokenForUser(User user, String token);

    // method to verify the new user email
    String validateVerificationToken(String token);

    // method to resend the verify email
    String resendValidationToken (String email);

    // method to login
    AuthResponse login(LoginRequest loginRequest);
    
    // method to logout
    void logout();
    
    // method to get all users with details
    List<UserDetailResponse> getAllUsers();
    
    // method to get current user details
    UserDetailResponse getCurrentUserDetails(String email);
    
//
//    void createPasswordResetTokenForUser(User user, String token);
//
//    // method to verify the new employee account and reset new password
//    String validatePasswordResetToken(String token);
//
//    // method to set the new password
//    void changeUserPassword(User user, String password);
//    User getUserByPasswordResetToken(String token);
}
