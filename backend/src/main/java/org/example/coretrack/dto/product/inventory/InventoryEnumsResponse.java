package org.example.coretrack.dto.product.inventory;

import java.util.List;
public class InventoryEnumsResponse {
    private List<EnumValue> inventoryStatuses;
    private List<EnumValue> inventoryTransactionTypes;
    private List<EnumValue> inventoryReferenceDocumentTypes;
    private List<EnumValue> inventoryTransactionSourceTypes;

    public InventoryEnumsResponse() {}

    public InventoryEnumsResponse(List<EnumValue> inventoryStatuses, List<EnumValue> inventoryTransactionTypes,
            List<EnumValue> inventoryReferenceDocumentTypes, List<EnumValue> inventoryTransactionSourceTypes) {
        this.inventoryStatuses = inventoryStatuses;
        this.inventoryTransactionTypes = inventoryTransactionTypes;
        this.inventoryReferenceDocumentTypes = inventoryReferenceDocumentTypes;
        this.inventoryTransactionSourceTypes = inventoryTransactionSourceTypes;
    }

    public List<EnumValue> getInventoryStatuses() {
        return inventoryStatuses;
    }

    public void setInventoryStatuses(List<EnumValue> inventoryStatuses) {
        this.inventoryStatuses = inventoryStatuses;
    }

    public List<EnumValue> getInventoryTransactionTypes() {
        return inventoryTransactionTypes;
    }

    public void setInventoryTransactionTypes(List<EnumValue> inventoryTransactionTypes) {
        this.inventoryTransactionTypes = inventoryTransactionTypes;
    }

    public List<EnumValue> getInventoryReferenceDocumentTypes() {
        return inventoryReferenceDocumentTypes;
    }

    public void setInventoryReferenceDocumentTypes(List<EnumValue> inventoryReferenceDocumentTypes) {
        this.inventoryReferenceDocumentTypes = inventoryReferenceDocumentTypes;
    }

    public List<EnumValue> getInventoryTransactionSourceTypes() {
        return inventoryTransactionSourceTypes;
    }

    public void setInventoryTransactionSourceTypes(List<EnumValue> inventoryTransactionSourceTypes) {
        this.inventoryTransactionSourceTypes = inventoryTransactionSourceTypes;
    }

    public static class EnumValue {
        private String value;
        private String displayName;
        private String description;

        public EnumValue() {}

        public EnumValue(String value, String displayName, String description) {
            this.value = value;
            this.displayName = displayName;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
} 