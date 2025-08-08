package org.example.coretrack.controller;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.example.coretrack.service.EmailTestService;
import org.example.coretrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/email-test")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class EmailTestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailTestService emailTestService;

    @Autowired
    private UserService userService;

    @GetMapping("/warehouse-emails")
    public ResponseEntity<Map<String, Object>> getWarehouseEmails() {
        try {
            User owner = userService.getCurrentUserOwner();
            if (owner == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Owner not found");
                return ResponseEntity.badRequest().body(error);
            }

            List<User> warehouseStaffUsers = userService.getWarehouseStaff();
            List<String> warehouseEmails = warehouseStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("ownerEmail", owner.getEmail());
            response.put("warehouseEmails", warehouseEmails);
            response.put("count", warehouseEmails.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/sale-emails")
    public ResponseEntity<Map<String, Object>> getSaleEmails() {
        try {
            User owner = userService.getCurrentUserOwner();
            if (owner == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Owner not found");
                return ResponseEntity.badRequest().body(error);
            }

            List<User> saleStaffUsers = userService.getSaleStaff();
            List<String> saleEmails = saleStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("ownerEmail", owner.getEmail());
            response.put("saleEmails", saleEmails);
            response.put("count", saleEmails.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/production-emails")
    public ResponseEntity<Map<String, Object>> getProductionEmails() {
        try {
            User owner = userService.getCurrentUserOwner();
            if (owner == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Owner not found");
                return ResponseEntity.badRequest().body(error);
            }

            List<User> productionStaffUsers = userService.getProductionStaff();
            List<String> productionEmails = productionStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("ownerEmail", owner.getEmail());
            response.put("productionEmails", productionEmails);
            response.put("count", productionEmails.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/all-staff-emails")
    public ResponseEntity<Map<String, Object>> getAllStaffEmails() {
        try {
            User owner = userService.getCurrentUserOwner();
            if (owner == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Owner not found");
                return ResponseEntity.badRequest().body(error);
            }

            List<User> warehouseStaffUsers = userService.getWarehouseStaff();
            List<User> saleStaffUsers = userService.getSaleStaff();
            List<User> productionStaffUsers = userService.getProductionStaff();

            List<String> warehouseEmails = warehouseStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            List<String> saleEmails = saleStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            List<String> productionEmails = productionStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("ownerEmail", owner.getEmail());
            response.put("warehouseEmails", warehouseEmails);
            response.put("saleEmails", saleEmails);
            response.put("productionEmails", productionEmails);
            response.put("totalStaff", warehouseEmails.size() + saleEmails.size() + productionEmails.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/test-email-recipients")
    public ResponseEntity<Map<String, Object>> testEmailRecipients() {
        try {
            User owner = userService.getCurrentUserOwner();
            if (owner == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Owner not found");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> result = emailTestService.testEmailRecipients(owner.getEmail());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/test-sample-email")
    public ResponseEntity<Map<String, Object>> testSampleEmail() {
        try {
            String ownerEmail = "owner@coretrack.com";
            Map<String, Object> result = emailTestService.testSampleEmail(ownerEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 