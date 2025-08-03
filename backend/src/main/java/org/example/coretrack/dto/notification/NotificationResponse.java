package org.example.coretrack.dto.notification;

import java.time.LocalDateTime;

import org.example.coretrack.model.notification.NotificationType;

public class NotificationResponse {
    
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Long productInventoryId;
    private String productName;
    private String productSku;
    private String productImageUrl;
    private Long materialInventoryId;
    private String materialName;
    private String materialSku;
    private String materialImageUrl;
    
    public NotificationResponse(Long id, NotificationType type, String title, String message, 
                             boolean isRead, LocalDateTime createdAt, LocalDateTime readAt,
                             Long productInventoryId, String productName, String productSku, String productImageUrl,
                             Long materialInventoryId, String materialName, String materialSku, String materialImageUrl) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.productInventoryId = productInventoryId;
        this.productName = productName;
        this.productSku = productSku;
        this.productImageUrl = productImageUrl;
        this.materialInventoryId = materialInventoryId;
        this.materialName = materialName;
        this.materialSku = materialSku;
        this.materialImageUrl = materialImageUrl;
    }

    public NotificationResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Long getProductInventoryId() {
        return productInventoryId;
    }

    public void setProductInventoryId(Long productInventoryId) {
        this.productInventoryId = productInventoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Long getMaterialInventoryId() {
        return materialInventoryId;
    }

    public void setMaterialInventoryId(Long materialInventoryId) {
        this.materialInventoryId = materialInventoryId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialSku() {
        return materialSku;
    }

    public void setMaterialSku(String materialSku) {
        this.materialSku = materialSku;
    }

    public String getMaterialImageUrl() {
        return materialImageUrl;
    }

    public void setMaterialImageUrl(String materialImageUrl) {
        this.materialImageUrl = materialImageUrl;
    }

    
} 