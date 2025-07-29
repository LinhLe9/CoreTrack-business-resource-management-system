package org.example.coretrack.dto.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters (ISO 4217)")
    private String currency;

    private String imageUrl;

    private String productGroupId;
    private String newProductGroupName;

    private List<UpdateProductVariantRequest> variants;

    public UpdateProductRequest() {
    }

    public UpdateProductRequest(String name, String description, BigDecimal price, 
                              String currency, String imageUrl, String productGroupId, 
                              String newProductGroupName, List<UpdateProductVariantRequest> variants) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.imageUrl = imageUrl;
        this.productGroupId = productGroupId;
        this.newProductGroupName = newProductGroupName;
        this.variants = variants;
    }

    // Getters and Setters
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductGroupId() {
        return productGroupId;
    }

    public void setProductGroupId(String productGroupId) {
        this.productGroupId = productGroupId;
    }

    public String getNewProductGroupName() {
        return newProductGroupName;
    }

    public void setNewProductGroupName(String newProductGroupName) {
        this.newProductGroupName = newProductGroupName;
    }

    public List<UpdateProductVariantRequest> getVariants() {
        return variants;
    }

    public void setVariants(List<UpdateProductVariantRequest> variants) {
        this.variants = variants;
    }
} 