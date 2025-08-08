package org.example.coretrack.service;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Role;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketDetail;
import org.example.coretrack.model.purchasingTicket.PurchasingTicket;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetail;
import org.example.coretrack.model.Sale.Order;
import org.example.coretrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSendingService {

    @Autowired
    private SendGridEmailService sendGridEmailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailConfigService emailConfigService;

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
     * Get warehouse staff users created by the current owner
     */
    private List<User> getWarehouseStaffUsers() {
        return userService.getWarehouseStaff();
    }

    /**
     * Get sale staff users created by the current owner
     */
    private List<User> getSaleStaffUsers() {
        return userService.getSaleStaff();
    }

    /**
     * Get production staff users created by the current owner
     */
    private List<User> getProductionStaffUsers() {
        return userService.getProductionStaff();
    }

    /**
     * Helper method to convert List<User> to List<String> of emails
     */
    private List<String> getUserEmails(List<User> users) {
        return users.stream()
                .map(User::getEmail)
                .toList();
    }

    // ========== INVENTORY EMAIL ALERTS ==========

    /**
     * Send email alert for product inventory status changes
     */
    public void sendProductInventoryStatusEmailAlert(ProductInventory inventory, InventoryStatus oldStatus, InventoryStatus newStatus) {
        if (oldStatus != newStatus) {
            // Check if email alerts are enabled for this status
            var config = emailConfigService.getEmailAlertConfig();
            boolean shouldSendEmail = false;
            
            switch (newStatus) {
                case LOW_STOCK:
                    shouldSendEmail = config.isLowStockEnabled();
                    break;
                case OVER_STOCK:
                    shouldSendEmail = config.isOverStockEnabled();
                    break;
                case OUT_OF_STOCK:
                    shouldSendEmail = config.isOutOfStockEnabled();
                    break;
                default:
                    // For other status changes, send if any inventory alert is enabled
                    shouldSendEmail = config.isLowStockEnabled() || config.isOverStockEnabled() || config.isOutOfStockEnabled();
                    break;
            }
            
            if (!shouldSendEmail) {
                System.out.println("Email alert disabled for product inventory status change: " + oldStatus + " -> " + newStatus + " for product: " + inventory.getProductVariant().getSku());
                return;
            }
            
            String productName = inventory.getProductVariant().getProduct().getName();
            String productSku = inventory.getProductVariant().getSku();
            int currentStock = inventory.getCurrentStock().intValue();
            int minAlertStock = inventory.getMinAlertStock() != null ? inventory.getMinAlertStock().intValue() : 0;
            int maxStockLevel = inventory.getMaxStockLevel() != null ? inventory.getMaxStockLevel().intValue() : 0;

            // Get owner email and production staff emails
            User owner = getCurrentUserOwner();
            List<User> productionStaffUsers = getProductionStaffUsers();
            
            // Combine owner email with production staff emails
            List<String> recipientEmails = new ArrayList<>();
            if (owner != null) {
                recipientEmails.add(owner.getEmail());
            }
            recipientEmails.addAll(getUserEmails(productionStaffUsers));
            
            // Fallback if no emails found
            if (recipientEmails.isEmpty()) {
                recipientEmails.add("production@coretrack.com");
            }

            switch (newStatus) {
                case LOW_STOCK:
                    if (currentStock > 0) {
                        for (String email : recipientEmails) {
                            sendGridEmailService.sendLowStockAlert(email, productName, productSku, currentStock, minAlertStock);
                        }
                    }
                    break;
                case OVER_STOCK:
                    if (maxStockLevel > 0) {
                        for (String email : recipientEmails) {
                            sendGridEmailService.sendOverStockAlert(email, productName, productSku, currentStock, maxStockLevel);
                        }
                    }
                    break;
                case OUT_OF_STOCK:
                    for (String email : recipientEmails) {
                        sendGridEmailService.sendOutOfStockAlert(email, productName, productSku);
                    }
                    break;
                default:
                    // For other status changes, send a general notification
                    for (String email : recipientEmails) {
                        sendGridEmailService.sendInventoryAlert(email, 
                            "Inventory Status Change - " + productName,
                            generateProductStatusChangeEmailHtml(productName, productSku, oldStatus, newStatus, currentStock));
                    }
                    break;
            }
            
            System.out.println("Email alert sent for product inventory status change: " + oldStatus + " -> " + newStatus + " for product: " + productSku + " to: " + recipientEmails);
        }
    }

    /**
     * Send email alert for material inventory status changes
     */
    public void sendMaterialInventoryStatusEmailAlert(MaterialInventory inventory, InventoryStatus oldStatus, InventoryStatus newStatus) {
        System.out.println("=== MATERIAL INVENTORY EMAIL ALERT DEBUG ===");
        System.out.println("Old Status: " + oldStatus);
        System.out.println("New Status: " + newStatus);
        System.out.println("Status Changed: " + (oldStatus != newStatus));
        
        if (oldStatus != newStatus) {
            // Check if email alerts are enabled for this status
            var config = emailConfigService.getEmailAlertConfig();
            boolean shouldSendEmail = false;
            
            switch (newStatus) {
                case LOW_STOCK:
                    shouldSendEmail = config.isLowStockEnabled();
                    System.out.println("LOW_STOCK email enabled: " + shouldSendEmail);
                    break;
                case OVER_STOCK:
                    shouldSendEmail = config.isOverStockEnabled();
                    System.out.println("OVER_STOCK email enabled: " + shouldSendEmail);
                    break;
                case OUT_OF_STOCK:
                    shouldSendEmail = config.isOutOfStockEnabled();
                    System.out.println("OUT_OF_STOCK email enabled: " + shouldSendEmail);
                    break;
                default:
                    // For other status changes, send if any inventory alert is enabled
                    shouldSendEmail = config.isLowStockEnabled() || config.isOverStockEnabled() || config.isOutOfStockEnabled();
                    System.out.println("DEFAULT email enabled: " + shouldSendEmail);
                    break;
            }
            
            if (!shouldSendEmail) {
                System.out.println("Email alert disabled for material inventory status change: " + oldStatus + " -> " + newStatus + " for material: " + inventory.getMaterialVariant().getSku());
                return;
            }
            
            String materialName = inventory.getMaterialVariant().getName();
            String materialSku = inventory.getMaterialVariant().getSku();
            int currentStock = inventory.getCurrentStock().intValue();
            int minAlertStock = inventory.getMinAlertStock() != null ? inventory.getMinAlertStock().intValue() : 0;
            int maxStockLevel = inventory.getMaxStockLevel() != null ? inventory.getMaxStockLevel().intValue() : 0;

            System.out.println("Material Name: " + materialName);
            System.out.println("Material SKU: " + materialSku);
            System.out.println("Current Stock: " + currentStock);
            System.out.println("Min Alert Stock: " + minAlertStock);
            System.out.println("Max Stock Level: " + maxStockLevel);

            // Get owner email and warehouse staff emails
            User owner = getCurrentUserOwner();
            List<User> warehouseStaffUsers = getWarehouseStaffUsers();
            
            System.out.println("Owner Email: " + owner.getEmail());
            System.out.println("Warehouse Emails: " + getUserEmails(warehouseStaffUsers));
            
            // Combine owner email with warehouse staff emails
            List<String> recipientEmails = new ArrayList<>();
            if (owner != null) {
                recipientEmails.add(owner.getEmail());
            }
            recipientEmails.addAll(getUserEmails(warehouseStaffUsers));
            
            // Fallback if no emails found
            if (recipientEmails.isEmpty()) {
                recipientEmails.add("warehouse@coretrack.com");
            }
            
            System.out.println("Final Recipient Emails: " + recipientEmails);

            switch (newStatus) {
                case LOW_STOCK:
                    if (currentStock > 0) {
                        System.out.println("Sending LOW_STOCK email to: " + recipientEmails);
                        for (String email : recipientEmails) {
                            sendGridEmailService.sendLowStockAlert(email, materialName, materialSku, currentStock, minAlertStock, true);
                        }
                    } else {
                        System.out.println("Not sending LOW_STOCK email because currentStock <= 0");
                    }
                    break;
                case OVER_STOCK:
                    if (maxStockLevel > 0) {
                        System.out.println("Sending OVER_STOCK email to: " + recipientEmails);
                        for (String email : recipientEmails) {
                            sendGridEmailService.sendOverStockAlert(email, materialName, materialSku, currentStock, maxStockLevel, true);
                        }
                    } else {
                        System.out.println("Not sending OVER_STOCK email because maxStockLevel <= 0");
                    }
                    break;
                case OUT_OF_STOCK:
                    System.out.println("Sending OUT_OF_STOCK email to: " + recipientEmails);
                    for (String email : recipientEmails) {
                        sendGridEmailService.sendOutOfStockAlert(email, materialName, materialSku, true);
                    }
                    break;
                default:
                    // For other status changes, send a general notification
                    System.out.println("Sending DEFAULT email to: " + recipientEmails);
                    for (String email : recipientEmails) {
                        sendGridEmailService.sendInventoryAlert(email, 
                            "Material Inventory Status Change - " + materialName,
                            generateMaterialStatusChangeEmailHtml(materialName, materialSku, oldStatus, newStatus, currentStock));
                    }
                    break;
            }
            
            System.out.println("Email alert sent for material inventory status change: " + oldStatus + " -> " + newStatus + " for material: " + materialSku + " to: " + recipientEmails);
        } else {
            System.out.println("Status not changed, no email sent");
        }
        System.out.println("=== END MATERIAL INVENTORY EMAIL ALERT DEBUG ===");
    }

    // ========== TICKET EMAIL ALERTS ==========

    /**
     * Send email alert for production ticket detail status changes
     */
    public void sendProductionTicketDetailStatusChangeAlert(ProductionTicketDetail detail, String oldStatus, String newStatus) {
        // Check if ticket status change alerts are enabled
        var config = emailConfigService.getEmailAlertConfig();
        if (!config.isTicketStatusChangeEnabled()) {
            System.out.println("Email alert disabled for production ticket detail status change: " + oldStatus + " -> " + newStatus + " for detail: " + detail.getId());
            return;
        }
        
        String ticketType = "Production Ticket Detail";
        String detailId = detail.getId().toString();
        String ticketId = detail.getProductionTicket().getId().toString();
        String productName = detail.getProductVariant().getName();
        
        // Get owner email and production staff emails
        User owner = getCurrentUserOwner();
        List<User> productionStaffUsers = getProductionStaffUsers();
        
        // Combine owner email with production staff emails
        List<String> recipientEmails = new ArrayList<>();
        if (owner != null) {
            recipientEmails.add(owner.getEmail());
        }
        recipientEmails.addAll(getUserEmails(productionStaffUsers));
        
        // Fallback if no emails found
        if (recipientEmails.isEmpty()) {
            recipientEmails.add("production@coretrack.com");
        }
        
        // Send email to all recipients
        for (String email : recipientEmails) {
            sendGridEmailService.sendTicketDetailStatusChangeAlert(email, ticketType, detailId, ticketId, productName, oldStatus, newStatus);
        }
        
        System.out.println("Email alert sent for production ticket detail status change: " + oldStatus + " -> " + newStatus + " for detail: " + detailId + " to: " + recipientEmails);
    }

    /**
     * Send email alert for production ticket status changes
     */
    public void sendProductionTicketStatusChangeAlert(ProductionTicket ticket, String oldStatus, String newStatus) {
        // Check if ticket status change alerts are enabled
        var config = emailConfigService.getEmailAlertConfig();
        if (!config.isTicketStatusChangeEnabled()) {
            System.out.println("Email alert disabled for production ticket status change: " + oldStatus + " -> " + newStatus + " for ticket: " + ticket.getId());
            return;
        }
        
        String ticketType = "Production Ticket";
        String ticketId = ticket.getId().toString();
        
        // Get owner email and production staff emails
        User owner = getCurrentUserOwner();
        List<User> productionStaffUsers = getProductionStaffUsers();
        
        // Combine owner email with production staff emails
        List<String> recipientEmails = new ArrayList<>();
        if (owner != null) {
            recipientEmails.add(owner.getEmail());
        }
        recipientEmails.addAll(getUserEmails(productionStaffUsers));
        
        // Fallback if no emails found
        if (recipientEmails.isEmpty()) {
            recipientEmails.add("production@coretrack.com");
        }
        
        // Send email to all recipients
        for (String email : recipientEmails) {
            sendGridEmailService.sendTicketStatusChangeAlert(email, ticketType, ticketId, oldStatus, newStatus);
        }
        
        System.out.println("Email alert sent for production ticket status change: " + oldStatus + " -> " + newStatus + " for ticket: " + ticketId + " to: " + recipientEmails);
    }

    /**
     * Send email alert for purchasing ticket detail status changes
     */
    public void sendPurchasingTicketDetailStatusChangeAlert(PurchasingTicketDetail detail, String oldStatus, String newStatus) {
        // Check if ticket status change alerts are enabled
        var config = emailConfigService.getEmailAlertConfig();
        if (!config.isTicketStatusChangeEnabled()) {
            System.out.println("Email alert disabled for purchasing ticket detail status change: " + oldStatus + " -> " + newStatus + " for detail: " + detail.getId());
            return;
        }
        
        String ticketType = "Purchasing Ticket Detail";
        String detailId = detail.getId().toString();
        String ticketId = detail.getPurchasingTicket().getId().toString();
        String materialName = detail.getMaterialVariant().getName();
        
        // Get owner email and warehouse staff emails
        User owner = getCurrentUserOwner();
        List<User> warehouseStaffUsers = getWarehouseStaffUsers();
        
        // Combine owner email with warehouse staff emails
        List<String> recipientEmails = new ArrayList<>();
        if (owner != null) {
            recipientEmails.add(owner.getEmail());
        }
        recipientEmails.addAll(getUserEmails(warehouseStaffUsers));
        
        // Fallback if no emails found
        if (recipientEmails.isEmpty()) {
            recipientEmails.add("purchasing@coretrack.com");
        }
        
        // Send email to all recipients
        for (String email : recipientEmails) {
            sendGridEmailService.sendTicketDetailStatusChangeAlert(email, ticketType, detailId, ticketId, materialName, oldStatus, newStatus);
        }
        
        System.out.println("Email alert sent for purchasing ticket detail status change: " + oldStatus + " -> " + newStatus + " for detail: " + detailId + " to: " + recipientEmails);
    }

    /**
     * Send email alert for purchasing ticket status changes
     */
    public void sendPurchasingTicketStatusChangeAlert(PurchasingTicket ticket, String oldStatus, String newStatus) {
        // Check if ticket status change alerts are enabled
        var config = emailConfigService.getEmailAlertConfig();
        if (!config.isTicketStatusChangeEnabled()) {
            System.out.println("Email alert disabled for purchasing ticket status change: " + oldStatus + " -> " + newStatus + " for ticket: " + ticket.getId());
            return;
        }
        
        String ticketType = "Purchasing Ticket";
        String ticketId = ticket.getId().toString();
        
        // Get owner email and warehouse staff emails
        User owner = getCurrentUserOwner();
        List<User> warehouseStaffUsers = getWarehouseStaffUsers();
        
        // Combine owner email with warehouse staff emails
        List<String> recipientEmails = new ArrayList<>();
        if (owner != null) {
            recipientEmails.add(owner.getEmail());
        }
        recipientEmails.addAll(getUserEmails(warehouseStaffUsers));
        
        // Fallback if no emails found
        if (recipientEmails.isEmpty()) {
            recipientEmails.add("purchasing@coretrack.com");
        }
        
        // Send email to all recipients
        for (String email : recipientEmails) {
            sendGridEmailService.sendTicketStatusChangeAlert(email, ticketType, ticketId, oldStatus, newStatus);
        }
        
        System.out.println("Email alert sent for purchasing ticket status change: " + oldStatus + " -> " + newStatus + " for ticket: " + ticketId + " to: " + recipientEmails);
    }

    /**
     * Send email alert for sale order status changes
     */
    public void sendSaleOrderStatusChangeAlert(Order order, String oldStatus, String newStatus) {
        // Check if ticket status change alerts are enabled
        var config = emailConfigService.getEmailAlertConfig();
        if (!config.isTicketStatusChangeEnabled()) {
            System.out.println("Email alert disabled for sale order status change: " + oldStatus + " -> " + newStatus + " for order: " + order.getId());
            return;
        }
        
        String ticketType = "Sale Order";
        String ticketId = order.getId().toString();
        
        // Get owner email and sale staff emails
        User owner = getCurrentUserOwner();
        List<User> saleStaffUsers = getSaleStaffUsers();
        
        // Combine owner email with sale staff emails
        List<String> recipientEmails = new ArrayList<>();
        if (owner != null) {
            recipientEmails.add(owner.getEmail());
        }
        recipientEmails.addAll(getUserEmails(saleStaffUsers));
        
        // Add customer email if available
        if (order.getCustomerEmail() != null) {
            recipientEmails.add(order.getCustomerEmail());
        }
        
        // Fallback if no emails found
        if (recipientEmails.isEmpty()) {
            recipientEmails.add("sales@coretrack.com");
        }
        
        // Send email to all recipients
        for (String email : recipientEmails) {
            sendGridEmailService.sendTicketStatusChangeAlert(email, ticketType, ticketId, oldStatus, newStatus);
        }
        
        System.out.println("Email alert sent for sale order status change: " + oldStatus + " -> " + newStatus + " for order: " + ticketId + " to: " + recipientEmails);
    }

    // ========== EMAIL TEMPLATE GENERATORS ==========

    private String generateProductStatusChangeEmailHtml(String productName, String sku, InventoryStatus oldStatus, InventoryStatus newStatus, int currentStock) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #e2e3e5; border: 1px solid #d6d8db; padding: 15px; border-radius: 5px; }
                    .product-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #383d41;">📋 INVENTORY STATUS CHANGE</h2>
                    <p>Product inventory status has been updated:</p>
                    <div class="product-info">
                        <strong>Product:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> %d<br>
                        <strong>Previous Status:</strong> %s<br>
                        <strong>New Status:</strong> %s
                    </div>
                    <p>Please review the inventory for any required actions.</p>
                </div>
            </body>
            </html>
            """.formatted(productName, sku, currentStock, oldStatus.getDisplayName(), newStatus.getDisplayName());
    }

    private String generateMaterialStatusChangeEmailHtml(String materialName, String sku, InventoryStatus oldStatus, InventoryStatus newStatus, int currentStock) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #e2e3e5; border: 1px solid #d6d8db; padding: 15px; border-radius: 5px; }
                    .material-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #383d41;">📋 MATERIAL INVENTORY STATUS CHANGE</h2>
                    <p>Material inventory status has been updated:</p>
                    <div class="material-info">
                        <strong>Material:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> %d<br>
                        <strong>Previous Status:</strong> %s<br>
                        <strong>New Status:</strong> %s
                    </div>
                    <p>Please review the inventory for any required actions.</p>
                </div>
            </body>
            </html>
            """.formatted(materialName, sku, currentStock, oldStatus.getDisplayName(), newStatus.getDisplayName());
    }

    /**
     * Send test email for testing purposes
     */
    public void sendTestEmail(String toEmail, String subject, String htmlContent) {
        sendGridEmailService.sendInventoryAlert(toEmail, subject, htmlContent);
    }
} 