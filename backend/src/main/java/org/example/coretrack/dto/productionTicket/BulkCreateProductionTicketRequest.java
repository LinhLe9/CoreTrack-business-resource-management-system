package org.example.coretrack.dto.productionTicket;

import java.util.List;

public class BulkCreateProductionTicketRequest {
    private String name;
    private List<ProductVariantBomRequest> productVariants;

    public BulkCreateProductionTicketRequest() {}

    public BulkCreateProductionTicketRequest(String name, List<ProductVariantBomRequest> productVariants) {
        this.name = name;
        this.productVariants = productVariants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductVariantBomRequest> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariantBomRequest> productVariants) {
        this.productVariants = productVariants;
    }
} 