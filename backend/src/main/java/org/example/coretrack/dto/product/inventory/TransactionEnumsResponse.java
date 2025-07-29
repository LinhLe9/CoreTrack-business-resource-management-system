package org.example.coretrack.dto.product.inventory;

import java.util.List;

public class TransactionEnumsResponse {
    private List<InventoryEnumsResponse.EnumValue> inventoryTransactionTypes;
    private List<InventoryEnumsResponse.EnumValue> productInventoryReferenceDocumentTypes;
    private List<InventoryEnumsResponse.EnumValue> productInventoryTransactionSourceTypes;

    public TransactionEnumsResponse() {}

    public TransactionEnumsResponse(List<InventoryEnumsResponse.EnumValue> inventoryTransactionTypes,
            List<InventoryEnumsResponse.EnumValue> productInventoryReferenceDocumentTypes,
            List<InventoryEnumsResponse.EnumValue> productInventoryTransactionSourceTypes) {
        this.inventoryTransactionTypes = inventoryTransactionTypes;
        this.productInventoryReferenceDocumentTypes = productInventoryReferenceDocumentTypes;
        this.productInventoryTransactionSourceTypes = productInventoryTransactionSourceTypes;
    }

    public List<InventoryEnumsResponse.EnumValue> getInventoryTransactionTypes() {
        return inventoryTransactionTypes;
    }

    public void setInventoryTransactionTypes(List<InventoryEnumsResponse.EnumValue> inventoryTransactionTypes) {
        this.inventoryTransactionTypes = inventoryTransactionTypes;
    }

    public List<InventoryEnumsResponse.EnumValue> getProductInventoryReferenceDocumentTypes() {
        return productInventoryReferenceDocumentTypes;
    }

    public void setProductInventoryReferenceDocumentTypes(List<InventoryEnumsResponse.EnumValue> productInventoryReferenceDocumentTypes) {
        this.productInventoryReferenceDocumentTypes = productInventoryReferenceDocumentTypes;
    }

    public List<InventoryEnumsResponse.EnumValue> getProductInventoryTransactionSourceTypes() {
        return productInventoryTransactionSourceTypes;
    }

    public void setProductInventoryTransactionSourceTypes(List<InventoryEnumsResponse.EnumValue> productInventoryTransactionSourceTypes) {
        this.productInventoryTransactionSourceTypes = productInventoryTransactionSourceTypes;
    }
} 