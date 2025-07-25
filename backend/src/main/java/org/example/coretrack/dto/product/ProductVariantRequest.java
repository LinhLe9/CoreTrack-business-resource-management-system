package org.example.coretrack.dto.product;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductVariantRequest {

    @NotBlank(message = "Variant name cannot be empty.")
    @Size(max = 255, message = "Variant name cannot exceed 255 characters.")
    private String name;

    @Size(max = 500, message = "Variant Short Description cannot exceed 500 characters.")
    private String shortDescription; // Optional

    private String imageUrl; // Optional, URL to the variant image
    // Variant SKU is system-generated, so not in the request.
    
    @Valid // Validate each BOM item in the list
    private List<BOMItemRequest> bomItems;

    // Constructors, Getters, Setters

    public ProductVariantRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<BOMItemRequest> getBomItems() {
        return bomItems;
    }

    public void setBomItems(List<BOMItemRequest> bomItems) {
        this.bomItems = bomItems;
    }
}

