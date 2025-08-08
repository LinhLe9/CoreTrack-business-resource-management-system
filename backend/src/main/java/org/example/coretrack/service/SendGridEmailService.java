package org.example.coretrack.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    public void sendInventoryAlert(String toEmail, String subject, String htmlContent) {
        // Validate inputs
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.err.println("Error: Recipient email is null or empty");
            return;
        }
        
        if (subject == null || subject.trim().isEmpty()) {
            System.err.println("Error: Email subject is null or empty");
            return;
        }
        
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            System.err.println("Error: Email content is null or empty");
            return;
        }
        
        System.out.println("=== SENDGRID EMAIL DEBUG ===");
        System.out.println("To Email: " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("From Email: " + fromEmail);
        System.out.println("From Name: " + fromName);
        System.out.println("API Key (first 10 chars): " + (sendGridApiKey != null ? sendGridApiKey.substring(0, Math.min(10, sendGridApiKey.length())) : "NULL"));
        
        try {
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            Mail mail = new Mail();
            mail.setFrom(new Email(fromEmail, fromName));
            mail.setSubject(subject);
            
            Personalization personalization = new Personalization();
            personalization.addTo(new Email(toEmail));
            mail.addPersonalization(personalization);
            
            Content content = new Content("text/html", htmlContent);
            mail.addContent(content);
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            System.out.println("SendGrid Response Status Code: " + response.getStatusCode());
            System.out.println("SendGrid Response Body: " + response.getBody());
            System.out.println("SendGrid Response Headers: " + response.getHeaders());
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Email sent successfully to " + toEmail + " with status code: " + response.getStatusCode());
            } else {
                System.err.println("Email failed to send to " + toEmail + " with status code: " + response.getStatusCode());
                System.err.println("Error response: " + response.getBody());
            }
            
        } catch (IOException e) {
            System.err.println("Error sending email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error sending email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== END SENDGRID EMAIL DEBUG ===");
    }

    public void sendLowStockAlert(String toEmail, String productName, String sku, int currentStock, int minStockLevel) {
        String subject = "LOW STOCK ALERT - " + productName;
        String htmlContent = generateLowStockEmailHtml(productName, sku, currentStock, minStockLevel);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendLowStockAlert(String toEmail, String materialName, String sku, int currentStock, int minStockLevel, boolean isMaterial) {
        String subject = "LOW STOCK ALERT - " + materialName;
        String htmlContent = generateLowStockEmailHtml(materialName, sku, currentStock, minStockLevel, true);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendOverStockAlert(String toEmail, String productName, String sku, int currentStock, int maxStockLevel) {
        String subject = "OVER STOCK ALERT - " + productName;
        String htmlContent = generateOverStockEmailHtml(productName, sku, currentStock, maxStockLevel);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendOverStockAlert(String toEmail, String materialName, String sku, int currentStock, int maxStockLevel, boolean isMaterial) {
        String subject = "OVER STOCK ALERT - " + materialName;
        String htmlContent = generateOverStockEmailHtml(materialName, sku, currentStock, maxStockLevel, true);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendOutOfStockAlert(String toEmail, String productName, String sku) {
        String subject = "OUT OF STOCK ALERT - " + productName;
        String htmlContent = generateOutOfStockEmailHtml(productName, sku);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendOutOfStockAlert(String toEmail, String materialName, String sku, boolean isMaterial) {
        String subject = "OUT OF STOCK ALERT - " + materialName;
        String htmlContent = generateOutOfStockEmailHtml(materialName, sku, true);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendTicketStatusChangeAlert(String toEmail, String ticketType, String ticketId, String oldStatus, String newStatus) {
        String subject = "TICKET STATUS CHANGE - " + ticketType + " #" + ticketId;
        String htmlContent = generateTicketStatusChangeEmailHtml(ticketType, ticketId, oldStatus, newStatus);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    public void sendTicketDetailStatusChangeAlert(String toEmail, String ticketType, String detailId, String ticketId, String productName, String oldStatus, String newStatus) {
        String subject = "TICKET DETAIL STATUS CHANGE - " + ticketType + " #" + detailId;
        String htmlContent = generateTicketDetailStatusChangeEmailHtml(ticketType, detailId, ticketId, productName, oldStatus, newStatus);
        sendInventoryAlert(toEmail, subject, htmlContent);
    }

    private String generateLowStockEmailHtml(String productName, String sku, int currentStock, int minStockLevel) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; }
                    .product-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #856404;">⚠️ LOW STOCK ALERT</h2>
                    <p>The following product is running low on stock:</p>
                    <div class="product-info">
                        <strong>Product:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> %d<br>
                        <strong>Minimum Stock Level:</strong> %d<br>
                        <strong>Shortage:</strong> %d units
                    </div>
                    <p>Please take immediate action to replenish stock.</p>
                </div>
            </body>
            </html>
            """.formatted(productName, sku, currentStock, minStockLevel, minStockLevel - currentStock);
    }

    private String generateLowStockEmailHtml(String materialName, String sku, int currentStock, int minStockLevel, boolean isMaterial) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; }
                    .material-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #856404;">⚠️ LOW STOCK ALERT</h2>
                    <p>The following material is running low on stock:</p>
                    <div class="material-info">
                        <strong>Material:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> %d<br>
                        <strong>Minimum Stock Level:</strong> %d<br>
                        <strong>Shortage:</strong> %d units
                    </div>
                    <p>Please take immediate action to replenish stock.</p>
                </div>
            </body>
            </html>
            """.formatted(materialName, sku, currentStock, minStockLevel, minStockLevel - currentStock);
    }

    private String generateOverStockEmailHtml(String productName, String sku, int currentStock, int maxStockLevel) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #d1ecf1; border: 1px solid #bee5eb; padding: 15px; border-radius: 5px; }
                    .product-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #0c5460;">📦 OVER STOCK ALERT</h2>
                    <p>The following product has exceeded maximum stock level:</p>
                    <div class="product-info">
                        <strong>Product:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> %d<br>
                        <strong>Maximum Stock Level:</strong> %d<br>
                        <strong>Excess:</strong> %d units
                    </div>
                    <p>Consider reducing stock levels or adjusting maximum stock settings.</p>
                </div>
            </body>
            </html>
            """.formatted(productName, sku, currentStock, maxStockLevel, currentStock - maxStockLevel);
    }

    private String generateOverStockEmailHtml(String materialName, String sku, int currentStock, int maxStockLevel, boolean isMaterial) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #d1ecf1; border: 1px solid #bee5eb; padding: 15px; border-radius: 5px; }
                    .material-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #0c5460;">📦 OVER STOCK ALERT</h2>
                    <p>The following material has exceeded maximum stock level:</p>
                    <div class="material-info">
                        <strong>Material:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> %d<br>
                        <strong>Maximum Stock Level:</strong> %d<br>
                        <strong>Excess:</strong> %d units
                    </div>
                    <p>Consider reducing stock levels or adjusting maximum stock settings.</p>
                </div>
            </body>
            </html>
            """.formatted(materialName, sku, currentStock, maxStockLevel, currentStock - maxStockLevel);
    }

    private String generateOutOfStockEmailHtml(String productName, String sku) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 5px; }
                    .product-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #721c24;">🚨 OUT OF STOCK ALERT</h2>
                    <p>The following product is completely out of stock:</p>
                    <div class="product-info">
                        <strong>Product:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> 0
                    </div>
                    <p><strong>URGENT:</strong> Immediate action required to restock this product.</p>
                </div>
            </body>
            </html>
            """.formatted(productName, sku);
    }

    private String generateOutOfStockEmailHtml(String materialName, String sku, boolean isMaterial) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 5px; }
                    .material-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #721c24;">🚨 OUT OF STOCK ALERT</h2>
                    <p>The following material is completely out of stock:</p>
                    <div class="material-info">
                        <strong>Material:</strong> %s<br>
                        <strong>SKU:</strong> %s<br>
                        <strong>Current Stock:</strong> 0
                    </div>
                    <p><strong>URGENT:</strong> Immediate action required to restock this material.</p>
                </div>
            </body>
            </html>
            """.formatted(materialName, sku);
    }

    private String generateTicketStatusChangeEmailHtml(String ticketType, String ticketId, String oldStatus, String newStatus) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #e2e3e5; border: 1px solid #d6d8db; padding: 15px; border-radius: 5px; }
                    .ticket-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #383d41;">📋 TICKET STATUS CHANGE</h2>
                    <p>A ticket status has been updated:</p>
                    <div class="ticket-info">
                        <strong>Ticket Type:</strong> %s<br>
                        <strong>Ticket ID:</strong> %s<br>
                        <strong>Previous Status:</strong> %s<br>
                        <strong>New Status:</strong> %s
                    </div>
                    <p>Please review the ticket for any required actions.</p>
                </div>
            </body>
            </html>
            """.formatted(ticketType, ticketId, oldStatus, newStatus);
    }

    private String generateTicketDetailStatusChangeEmailHtml(String ticketType, String detailId, String ticketId, String productName, String oldStatus, String newStatus) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .alert { background-color: #e2e3e5; border: 1px solid #d6d8db; padding: 15px; border-radius: 5px; }
                    .detail-info { background-color: #f8f9fa; padding: 10px; margin: 10px 0; border-radius: 3px; }
                </style>
            </head>
            <body>
                <div class="alert">
                    <h2 style="color: #383d41;">📋 TICKET DETAIL STATUS CHANGE</h2>
                    <p>A ticket detail status has been updated:</p>
                    <div class="detail-info">
                        <strong>Ticket Type:</strong> %s<br>
                        <strong>Detail ID:</strong> %s<br>
                        <strong>Ticket ID:</strong> %s<br>
                        <strong>Product:</strong> %s<br>
                        <strong>Previous Status:</strong> %s<br>
                        <strong>New Status:</strong> %s
                    </div>
                    <p>Please review the ticket detail for any required actions.</p>
                </div>
            </body>
            </html>
            """.formatted(ticketType, detailId, ticketId, productName, oldStatus, newStatus);
    }
} 