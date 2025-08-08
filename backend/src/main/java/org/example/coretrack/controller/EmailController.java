package org.example.coretrack.controller;

import org.example.coretrack.dto.email.EmailAlertConfigDTO;
import org.example.coretrack.dto.email.TestEmailRequestDTO;
import org.example.coretrack.service.EmailSendingService;
import org.example.coretrack.service.EmailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class EmailController {

    @Autowired
    private EmailSendingService emailSendingService;
    
    @Autowired
    private EmailConfigService emailConfigService;

    @GetMapping("/config")
    public ResponseEntity<EmailAlertConfigDTO> getEmailAlertConfig() {
        EmailAlertConfigDTO config = emailConfigService.getEmailAlertConfig();
        return ResponseEntity.ok(config);
    }

    @PutMapping("/config")
    public ResponseEntity<EmailAlertConfigDTO> updateEmailAlertConfig(@RequestBody EmailAlertConfigDTO config) {
        EmailAlertConfigDTO updatedConfig = emailConfigService.updateEmailAlertConfig(config);
        return ResponseEntity.ok(updatedConfig);
    }

    @PostMapping("/test")
    public ResponseEntity<String> sendTestEmail(@RequestBody TestEmailRequestDTO request) {
        try {
            String testSubject = "Test Email from CoreTrack";
            String testHtmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .test-email { background-color: #f8f9fa; padding: 20px; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="test-email">
                        <h2>🧪 Test Email</h2>
                        <p>This is a test email from CoreTrack Business Resource Management System.</p>
                        <p>If you received this email, the email service is working correctly!</p>
                        <p><strong>Sent to:</strong> %s</p>
                        <p><strong>Timestamp:</strong> %s</p>
                    </div>
                </body>
                </html>
                """.formatted(
                    request.getToEmail(),
                    java.time.LocalDateTime.now().toString()
                );

            emailSendingService.sendTestEmail(request.getToEmail(), testSubject, testHtmlContent);
            return ResponseEntity.ok("Test email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test email: " + e.getMessage());
        }
    }
} 