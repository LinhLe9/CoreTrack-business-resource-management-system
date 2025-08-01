package org.example.coretrack.dto.product.inventory;

import java.util.List;

public class TransactionEnumsResponse {
    private List<InventoryEnumsResponse.EnumValue> inventoryTransactionTypes;
    private List<InventoryEnumsResponse.EnumValue> inventoryReferenceDocumentTypes;
    private List<InventoryEnumsResponse.EnumValue> inventoryTransactionSourceTypes;

    public TransactionEnumsResponse() {}

    public TransactionEnumsResponse(List<InventoryEnumsResponse.EnumValue> inventoryTransactionTypes,
            List<InventoryEnumsResponse.EnumValue> inventoryReferenceDocumentTypes,
            List<InventoryEnumsResponse.EnumValue> inventoryTransactionSourceTypes) {
        this.inventoryTransactionTypes = inventoryTransactionTypes;
        this.inventoryReferenceDocumentTypes = inventoryReferenceDocumentTypes;
        this.inventoryTransactionSourceTypes = inventoryTransactionSourceTypes;
    }

    public List<InventoryEnumsResponse.EnumValue> getInventoryTransactionTypes() {
        return inventoryTransactionTypes;
    }

    public void setInventoryTransactionTypes(List<InventoryEnumsResponse.EnumValue> inventoryTransactionTypes) {
        this.inventoryTransactionTypes = inventoryTransactionTypes;
    }

    public List<InventoryEnumsResponse.EnumValue> getInventoryReferenceDocumentTypes() {
        return inventoryReferenceDocumentTypes;
    }

    public void setInventoryReferenceDocumentTypes(List<InventoryEnumsResponse.EnumValue> inventoryReferenceDocumentTypes) {
        this.inventoryReferenceDocumentTypes = inventoryReferenceDocumentTypes;
    }

    public List<InventoryEnumsResponse.EnumValue> getInventoryTransactionSourceTypes() {
        return inventoryTransactionSourceTypes;
    }

    public void setInventoryTransactionSourceTypes(List<InventoryEnumsResponse.EnumValue> inventoryTransactionSourceTypes) {
        this.inventoryTransactionSourceTypes = inventoryTransactionSourceTypes;
    }
} 