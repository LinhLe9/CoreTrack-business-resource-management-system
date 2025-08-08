package org.example.coretrack.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.coretrack.model.auth.Role;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class NotificationTargetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Get current user's owner User from SecurityContext
     * If current user's createdBy is null, return current user (original owner)
     * If current user's createdBy is not null, return the createdBy user
     */
    private User getCurrentUserOwner() {
        return userService.getCurrentUserOwner();
    }

    /**
     * Get current user's owner email from SecurityContext
     * If current user's createdBy is null, return current user email (original owner)
     * If current user's createdBy is not null, return the createdBy user email
     */
    private String getCurrentUserOwnerEmail() {
        User owner = getCurrentUserOwner();
        return owner != null ? owner.getEmail() : null;
    }
    
    /**
     * Get current user from SecurityContext
     */
    private User getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();    
            if (principal instanceof User) {
                return (User) principal;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get all users with OWNER role created by the current owner
     */
    public List<User> getOwners() {
        return userService.getOwners();
    }
    
    /**
     * Get all users with WAREHOUSE_STAFF role created by the current owner
     */
    public List<User> getWarehouseStaff() {
        return userService.getWarehouseStaff();
    }
    
    /**
     * Get all users with SALE_STAFF role created by the current owner
     */
    public List<User> getSaleStaff() {
        return userService.getSaleStaff();
    }
    
    /**
     * Get all users with PRODUCTION_STAFF role created by the current owner
     */
    public List<User> getProductionStaff() {
        return userService.getProductionStaff();
    }
    
    /**
     * Get all enabled users created by the current owner OR the owner itself
     */
    public List<User> getAllEnabledUsers() {
        return userService.getAllEnabledUsers();
    }
    
    /**
     * Get all users for inventory notifications (OWNER + WAREHOUSE_STAFF)
     */
    public List<User> getInventoryNotificationTargets() {
        return userService.getInventoryNotificationTargets();
    }
    
    /**
     * Get all users for production notifications (OWNER + PRODUCTION_STAFF)
     */
    public List<User> getProductionNotificationTargets() {
        return userService.getProductionNotificationTargets();
    }
    
    /**
     * Get all users for purchasing notifications (OWNER + WAREHOUSE_STAFF)
     */
    public List<User> getPurchasingNotificationTargets() {
        return userService.getPurchasingNotificationTargets();
    }
    
    /**
     * Get all users for sale notifications (OWNER + SALE_STAFF)
     */
    public List<User> getSaleNotificationTargets() {
        return userService.getSaleNotificationTargets();
    }
    
    /**
     * Get all users for general notifications (OWNER + all staff)
     */
    public List<User> getGeneralNotificationTargets() {
        return userService.getGeneralNotificationTargets();
    }
}
