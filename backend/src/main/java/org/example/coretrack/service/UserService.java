package org.example.coretrack.service;

import org.example.coretrack.dto.auth.AuthResponse;
import org.example.coretrack.dto.auth.CreateUserRequest;
import org.example.coretrack.dto.auth.LoginRequest;
import org.example.coretrack.dto.auth.RegistrationRequest;
import org.example.coretrack.dto.auth.UserDetailResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;
import org.example.coretrack.model.auth.Role;
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
    
    // method to get all users with details (only users created by current owner and the owner itself)
    List<UserDetailResponse> getAllUsers(User currentUser);
    
    // method to get current user details
    UserDetailResponse getCurrentUserDetails(String email);
    
    // ===== NEW METHODS FOR USER TARGETS =====
    
    /**
     * Get current user's owner User from SecurityContext
     * If current user's createdBy is null, return current user (original owner)
     * If current user's createdBy is not null, return the createdBy user
     */
    User getCurrentUserOwner();
    
    /**
     * Get all users with OWNER role created by the current owner
     */
    List<User> getOwners();
    
    /**
     * Get all users with WAREHOUSE_STAFF role created by the current owner
     */
    List<User> getWarehouseStaff();
    
    /**
     * Get all users with SALE_STAFF role created by the current owner
     */
    List<User> getSaleStaff();
    
    /**
     * Get all users with PRODUCTION_STAFF role created by the current owner
     */
    List<User> getProductionStaff();
    
    /**
     * Get all enabled users created by the current owner OR the owner itself
     */
    List<User> getAllEnabledUsers();
    
    /**
     * Get all users for inventory notifications (OWNER + WAREHOUSE_STAFF)
     */
    List<User> getInventoryNotificationTargets();
    
    /**
     * Get all users for production notifications (OWNER + PRODUCTION_STAFF)
     */
    List<User> getProductionNotificationTargets();
    
    /**
     * Get all users for purchasing notifications (OWNER + WAREHOUSE_STAFF)
     */
    List<User> getPurchasingNotificationTargets();
    
    /**
     * Get all users for sale notifications (OWNER + SALE_STAFF)
     */
    List<User> getSaleNotificationTargets();
    
    /**
     * Get all users for general notifications (OWNER + all staff)
     */
    List<User> getGeneralNotificationTargets();
    
    // ===== PASSWORD RESET METHODS =====
    
    /**
     * Send password reset email to user
     */
    String forgotPassword(String email);
    
    /**
     * Validate password reset token
     */
    String validatePasswordResetToken(String token);
    
    /**
     * Reset password with token
     */
    String resetPassword(String token, String newPassword);
    
    /**
     * Create password reset token for user
     */
    void createPasswordResetTokenForUser(User user, String token);
    
    /**
     * Get user by password reset token
     */
    User getUserByPasswordResetToken(String token);
    
    /**
     * Change user password
     */
    void changeUserPassword(User user, String password);
    
    // ===== COMPANY-BASED METHODS =====
    
    /**
     * Get current user's company
     */
    Company getCurrentUserCompany();
    
    /**
     * Get all users in current user's company
     */
    List<UserDetailResponse> getAllUsersInCompany(User currentUser);
    
    /**
     * Get all users with specific role in current user's company
     */
    List<User> getUsersByRoleInCompany(Role role);
    
    /**
     * Get all enabled users in current user's company
     */
    List<User> getAllEnabledUsersInCompany();
    
    /**
     * Get notification targets in current user's company
     */
    List<User> getNotificationTargetsInCompany(String notificationType);
}
