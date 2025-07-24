package org.example.coretrack.dto.material;

import org.example.coretrack.dto.product.InventoryResponse;

public class MaterialVariantInventoryResponse {
    private MaterialVariantResponse materialVariantResponse;
    private InventoryResponse inventoryResponse;

    public MaterialVariantInventoryResponse(){
    }

    public MaterialVariantInventoryResponse(MaterialVariantResponse materialVariantResponse,
            InventoryResponse inventoryResponse) {
        this.materialVariantResponse = materialVariantResponse;
        this.inventoryResponse = inventoryResponse;
    }
    
    public MaterialVariantResponse getMaterialVariantResponse() {
        return materialVariantResponse;
    }
    public void setMaterialVariantResponse(MaterialVariantResponse materialVariantResponse) {
        this.materialVariantResponse = materialVariantResponse;
    }
    public InventoryResponse getInventoryResponse() {
        return inventoryResponse;
    }
    public void setInventoryResponse(InventoryResponse inventoryResponse) {
        this.inventoryResponse = inventoryResponse;
    }
}
