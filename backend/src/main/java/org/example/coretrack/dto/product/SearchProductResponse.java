package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.Product;
import java.math.BigDecimal;

public class SearchProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String groupProduct;
    private String status; 
    private BigDecimal price;
    private String imageUrl;

    // Constructors
    public SearchProductResponse() {}

    public SearchProductResponse(Long id, String sku, String name, String description, String groupProduct, String status, BigDecimal price, String imageUrl) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.groupProduct = groupProduct;
        this.status = status;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Constructor convert from Entity to DTO
    public SearchProductResponse(Product product) {
        this.id = product.getId();
        this.sku = product.getSku();
        this.name = product.getName();
        this.description = product.getDescription();
        this.groupProduct = (product.getGroup() != null) ? product.getGroup().getName() : null;
        this.status = (product.getStatus() != null) ? product.getStatus().name(): null;
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupProduct() {
        return groupProduct;
    }

    public void setGroupProduct(String groupProduct) {
        this.groupProduct = groupProduct;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    
}
