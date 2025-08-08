package org.example.coretrack.service;

import org.example.coretrack.dto.email.EmailAlertConfigDTO;
import org.springframework.stereotype.Service;

@Service
public class EmailConfigService {

    private EmailAlertConfigDTO currentConfig;

    public EmailConfigService() {
        this.currentConfig = new EmailAlertConfigDTO();
        this.currentConfig.setLowStockEnabled(true);
        this.currentConfig.setOverStockEnabled(true);
        this.currentConfig.setOutOfStockEnabled(true);
        this.currentConfig.setTicketStatusChangeEnabled(true);
        this.currentConfig.setRecipientEmails(null);
    }

    public EmailAlertConfigDTO getEmailAlertConfig() {
        return currentConfig;
    }

    public EmailAlertConfigDTO updateEmailAlertConfig(EmailAlertConfigDTO config) {
        this.currentConfig = config;
        return this.currentConfig;
    }
} 