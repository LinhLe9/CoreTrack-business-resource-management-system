package org.example.coretrack.service;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailTestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSendingService emailSendingService;

    @Autowired
    private UserService userService;

    /**
     * Test email recipient logic for different scenarios
     */
    public Map<String, Object> testEmailRecipients(String ownerEmail) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Find owner by email
            User owner = userRepository.findByEmail(ownerEmail).orElse(null);
            if (owner == null) {
                result.put("success", false);
                result.put("error", "Owner not found with email: " + ownerEmail);
                return result;
            }

            // Get all staff users for this owner
            List<User> warehouseStaffUsers = userService.getWarehouseStaff();
            List<User> saleStaffUsers = userService.getSaleStaff();
            List<User> productionStaffUsers = userService.getProductionStaff();

            // Convert to emails for backward compatibility
            List<String> warehouseEmails = warehouseStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            List<String> saleEmails = saleStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            List<String> productionEmails = productionStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            result.put("ownerEmail", ownerEmail);
            result.put("warehouseStaffEmails", warehouseEmails);
            result.put("saleStaffEmails", saleEmails);
            result.put("productionStaffEmails", productionEmails);
            result.put("totalStaff", warehouseEmails.size() + saleEmails.size() + productionEmails.size());

            // Simulate email recipients for different scenarios
            Map<String, Object> scenarios = new HashMap<>();

            // Product Inventory & Production Tickets: OWNER + PRODUCTION_STAFF
            List<String> productInventoryRecipients = new java.util.ArrayList<>();
            productInventoryRecipients.add(ownerEmail);
            productInventoryRecipients.addAll(productionEmails);
            scenarios.put("productInventoryRecipients", productInventoryRecipients);

            // Material Inventory & Purchasing Tickets: OWNER + WAREHOUSE_STAFF
            List<String> materialInventoryRecipients = new java.util.ArrayList<>();
            materialInventoryRecipients.add(ownerEmail);
            materialInventoryRecipients.addAll(warehouseEmails);
            scenarios.put("materialInventoryRecipients", materialInventoryRecipients);

            // Sale Orders: OWNER + SALE_STAFF + Customer (if available)
            List<String> saleOrderRecipients = new java.util.ArrayList<>();
            saleOrderRecipients.add(ownerEmail);
            saleOrderRecipients.addAll(saleEmails);
            // Note: Customer email would be added dynamically based on order
            scenarios.put("saleOrderRecipients", saleOrderRecipients);

            result.put("emailScenarios", scenarios);
            result.put("success", true);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * Test sending a sample email to verify the logic
     */
    public Map<String, Object> testSampleEmail(String ownerEmail) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Find owner by email
            User owner = userRepository.findByEmail(ownerEmail).orElse(null);
            if (owner == null) {
                result.put("success", false);
                result.put("error", "Owner not found with email: " + ownerEmail);
                return result;
            }

            // Get recipients
            List<User> warehouseStaffUsers = userService.getWarehouseStaff();
            List<User> productionStaffUsers = userService.getProductionStaff();

            List<String> warehouseEmails = warehouseStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            List<String> productionEmails = productionStaffUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            result.put("ownerEmail", ownerEmail);
            result.put("warehouseStaffCount", warehouseEmails.size());
            result.put("productionStaffCount", productionEmails.size());

            // Simulate what would happen in real scenarios
            Map<String, Object> simulation = new HashMap<>();

            // Product inventory alert would go to: OWNER + PRODUCTION_STAFF
            List<String> productAlertRecipients = new java.util.ArrayList<>();
            productAlertRecipients.add(ownerEmail);
            productAlertRecipients.addAll(productionEmails);
            simulation.put("productInventoryAlert", productAlertRecipients);

            // Material inventory alert would go to: OWNER + WAREHOUSE_STAFF
            List<String> materialAlertRecipients = new java.util.ArrayList<>();
            materialAlertRecipients.add(ownerEmail);
            materialAlertRecipients.addAll(warehouseEmails);
            simulation.put("materialInventoryAlert", materialAlertRecipients);

            result.put("simulation", simulation);
            result.put("success", true);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }
} 