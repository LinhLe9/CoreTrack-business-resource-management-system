package org.example.coretrack.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import java.util.List;

public class UpdateProductVariantRequest {
    private Long id; // For existing variants, null for new variants
    
    @Size(max = 255, message = "Variant name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Variant description must not exceed 1000 characters")
    private String description;

    @Size(max = 10000, message = "Image URL must not exceed 10000 characters")
    private String imageUrl;

    @Valid
    private List<BOMItemRequest> bomItems;

    public UpdateProductVariantRequest() {
    }

    public UpdateProductVariantRequest(Long id, String name, String description, String imageUrl, List<BOMItemRequest> bomItems) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.bomItems = bomItems;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<BOMItemRequest> getBomItems() {
        return bomItems;
    }

    public void setBomItems(List<BOMItemRequest> bomItems) {
        this.bomItems = bomItems;
    }
} 