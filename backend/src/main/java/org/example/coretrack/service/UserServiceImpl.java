package org.example.coretrack.service;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;
import org.example.coretrack.dto.auth.AuthResponse;
import org.example.coretrack.dto.auth.CreateUserRequest;
import org.example.coretrack.dto.auth.LoginRequest;
import org.example.coretrack.dto.auth.RegistrationRequest;
import org.example.coretrack.dto.auth.UserDetailResponse;
import org.example.coretrack.model.auth.Role;
import org.example.coretrack.model.auth.VerificationToken;
import org.example.coretrack.model.auth.PasswordResetToken;
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.repository.PasswordResetTokenRepository;
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
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private CompanyService companyService;
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
            VerificationTokenRepository tokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            CompanyService companyService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.companyService = companyService;
    }

    @Override
    public AuthResponse registerOwner(RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new company for this owner
        String companyName = registrationRequest.getCompanyName() != null ? 
            registrationRequest.getCompanyName() : 
            registrationRequest.getUsername() + "'s Company";
        
        Company company = companyService.createCompany(companyName, "Company for " + registrationRequest.getUsername());
        
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.OWNER);
        user.setEnabled(false); // Disabled until verification
        user.setCompany(company);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // create token and save to database by helper method
        String token = UUID.randomUUID().toString();
        saveVerificationTokenForUser(user, token);

        // send email
        sendVerificationEmail(user,token);
        return new AuthResponse(null, user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    // saveVerificationTokenForUser helper method
    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }

    // send email helper method
    // Email sending helpers
    private void sendVerificationEmail(User user, String code) { 
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE: " + code;
        String validationLink = "http://localhost:3030/register/validation?token=" + code;
        
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
        htmlMessageBuilder.append("<p style=\"font-size: 16px;\">Please click the link below or enter the verification code to verify your account within 24 hours:</p>")
                .append("<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">")
                .append("<p style=\"font-size: 16px; margin-bottom: 15px;\"><strong>Option 1:</strong> Click the verification link:</p>")
                .append("<a href=\"").append(validationLink).append("\" style=\"display: inline-block; background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Verify Account</a>")
                .append("<p style=\"font-size: 16px; margin-top: 20px;\"><strong>Option 2:</strong> Enter the verification code manually:</p>")
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
        
        // Get the current user (creator) from the request
        User creator = userRepository.findById(createUserRequest.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Creator user not found"));
        
        User user = new User();
        user.setEmail(createUserRequest.getEmail());
        user.setUsername(createUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setRole(createUserRequest.getRole());
        user.setEnabled(false); // Employee accounts are unenabled by default
        user.setCreatedBy(creator); // Set who created this user
        user.setCreatedAt(LocalDateTime.now()); // Set creation timestamp
        user.setCompany(creator.getCompany()); // Assign to the same company as the creator
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
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isEnabled());
    }

    @Override
    public List<UserDetailResponse> getAllUsers(User currentUser) {
        // Check if current user is OWNER
        if (!Role.OWNER.equals(currentUser.getRole())) {
            throw new RuntimeException("Access denied. Only OWNER can view all users.");
        }
        
        // Get all users created by this owner and the owner itself
        List<User> users = userRepository.findByCreatedByOrId(currentUser, currentUser.getId());
        return users.stream()
                .map(this::convertToUserDetailResponse)
                .toList();
    }

    @Override
    public void logout() {
        // Clear the security context
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        
        // For now, we just clear the security context
        // The client should remove the JWT token from localStorage
    }

    @Override
    public UserDetailResponse getCurrentUserDetails(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserDetailResponse(user);
    }

    // Helper method to convert User to UserDetailResponse
    private UserDetailResponse convertToUserDetailResponse(User user) {
        String createdByEmail = null;
        String createdByUsername = null;
        
        if (user.getCreatedBy() != null) {
            createdByEmail = user.getCreatedBy().getEmail();
            createdByUsername = user.getCreatedBy().getUsername();
        }
        
        return new UserDetailResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.isEnabled(),
            createdByEmail,
            createdByUsername,
            user.getCreatedAt()
        );
    }

    // ===== NEW METHODS FOR USER TARGETS =====

    @Override
    public User getCurrentUserOwner() {
        try {
            Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();    
            if (principal instanceof User) {
                User currentUser = (User) principal;
                // If createdBy is null, this is the original owner
                if (currentUser.getCreatedBy() == null) {
                    return currentUser;
                } else {
                    // If createdBy is not null, return the createdBy user
                    return currentUser.getCreatedBy();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> getOwners() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return List.of();
            }
            
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            // Find all users with OWNER role created by the current owner
            return userRepository.findByRoleAndCreatedByEmail("OWNER", owner.getEmail());
        } catch (Exception e) {
            System.err.println("Error getting owners: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getWarehouseStaff() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            return userRepository.findWarehouseStaffUsersByOwner(owner);
        } catch (Exception e) {
            System.err.println("Error getting warehouse staff: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getSaleStaff() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            return userRepository.findSaleStaffUsersByOwner(owner);
        } catch (Exception e) {
            System.err.println("Error getting sale staff: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getProductionStaff() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            return userRepository.findProductionStaffUsersByOwner(owner);
        } catch (Exception e) {
            System.err.println("Error getting production staff: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getAllEnabledUsers() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return List.of();
            }
            
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            // Get all users created by the current owner OR the owner itself
            return userRepository.findByCreatedByOrId(owner, owner.getId());
        } catch (Exception e) {
            System.err.println("Error getting all enabled users: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getInventoryNotificationTargets() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            List<User> targets = new java.util.ArrayList<>();
            targets.add(owner);
            targets.addAll(getWarehouseStaff());
            return targets;
        } catch (Exception e) {
            System.err.println("Error getting inventory notification targets: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getProductionNotificationTargets() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            List<User> targets = new java.util.ArrayList<>();
            targets.add(owner);
            targets.addAll(getProductionStaff());
            return targets;
        } catch (Exception e) {
            System.err.println("Error getting production notification targets: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getPurchasingNotificationTargets() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            List<User> targets = new java.util.ArrayList<>();
            targets.add(owner);
            targets.addAll(getWarehouseStaff());
            return targets;
        } catch (Exception e) {
            System.err.println("Error getting purchasing notification targets: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getSaleNotificationTargets() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            List<User> targets = new java.util.ArrayList<>();
            targets.add(owner);
            targets.addAll(getSaleStaff());
            return targets;
        } catch (Exception e) {
            System.err.println("Error getting sale notification targets: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getGeneralNotificationTargets() {
        try {
            User owner = getCurrentUserOwner();
            if (owner == null) {
                return List.of();
            }
            
            List<User> targets = new java.util.ArrayList<>();
            targets.add(owner);
            targets.addAll(getWarehouseStaff());
            targets.addAll(getSaleStaff());
            targets.addAll(getProductionStaff());
            return targets;
        } catch (Exception e) {
            System.err.println("Error getting general notification targets: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Helper method to get current user from SecurityContext
     */
    private User getCurrentUser() {
        try {
            Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();    
            if (principal instanceof User) {
                return (User) principal;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // ===== PASSWORD RESET METHODS =====
    
    @Override
    public String forgotPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            if (!user.isEnabled()) {
                return "User account is not enabled";
            }
            
            // Delete any existing tokens for this user
            passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);
            
            // Generate token
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user, token);
            
            // Send email
            sendPasswordResetEmail(user, token);
            
            return "valid";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    @Override
    public String validatePasswordResetToken(String token) {
        try {
            PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
            if (passwordResetToken == null) {
                return "invalid";
            }
            
            Calendar cal = Calendar.getInstance();
            if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                return "expired";
            }
            
            return "valid";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    @Override
    public String resetPassword(String token, String newPassword) {
        try {
            String result = validatePasswordResetToken(token);
            if (!"valid".equals(result)) {
                return result;
            }
            
            PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
            User user = passwordResetToken.getUser();
            
            // Update password
            changeUserPassword(user, newPassword);
            
            // Delete the token
            passwordResetTokenRepository.delete(passwordResetToken);
            
            return "valid";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }
    
    @Override
    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        return passwordResetToken != null ? passwordResetToken.getUser() : null;
    }
    
    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
    
    /**
     * Send password reset email to user
     */
    private void sendPasswordResetEmail(User user, String token) {
        try {
            String subject = "Password Reset Request";
            String resetLink = "http://localhost:3030/reset-password?token=" + token;
            String emailContent = String.format(
                "<html><body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>Hello %s,</p>" +
                "<p>You have requested to reset your password. Please click the link below to reset your password:</p>" +
                "<p><a href='%s'>Reset Password</a></p>" +
                "<p>This link will expire in 24 hours.</p>" +
                "<p>If you did not request this password reset, please ignore this email.</p>" +
                "<p>Best regards,<br>CoreTrack Team</p>" +
                "</body></html>",
                user.getUsername(), resetLink
            );
            
            mailSender.sendVerificationEmail(user.getEmail(), subject, emailContent);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    // ===== COMPANY-BASED METHODS =====
    
    @Override
    public Company getCurrentUserCompany() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return currentUser.getCompany();
    }
    
    @Override
    public List<UserDetailResponse> getAllUsersInCompany(User currentUser) {
        Company company = currentUser.getCompany();
        List<User> users = userRepository.findByCompanyAndEnabledTrue(company);
        return users.stream()
                .map(this::convertToUserDetailResponse)
                .toList();
    }
    
    @Override
    public List<User> getUsersByRoleInCompany(Role role) {
        Company company = getCurrentUserCompany();
        return userRepository.findByRoleAndCompanyAndEnabledTrue(role, company);
    }
    
    @Override
    public List<User> getAllEnabledUsersInCompany() {
        Company company = getCurrentUserCompany();
        return userRepository.findByCompanyAndEnabledTrue(company);
    }
    
    @Override
    public List<User> getNotificationTargetsInCompany(String notificationType) {
        Company company = getCurrentUserCompany();
        
        switch (notificationType.toLowerCase()) {
            case "inventory":
                return userRepository.findByRoleAndCompanyAndEnabledTrue(Role.WAREHOUSE_STAFF, company);
            case "production":
                return userRepository.findByRoleAndCompanyAndEnabledTrue(Role.PRODUCTION_STAFF, company);
            case "purchasing":
                return userRepository.findByRoleAndCompanyAndEnabledTrue(Role.WAREHOUSE_STAFF, company);
            case "sale":
                return userRepository.findByRoleAndCompanyAndEnabledTrue(Role.SALE_STAFF, company);
            case "general":
                return userRepository.findByCompanyAndEnabledTrue(company);
            default:
                return List.of();
        }
    }
}
