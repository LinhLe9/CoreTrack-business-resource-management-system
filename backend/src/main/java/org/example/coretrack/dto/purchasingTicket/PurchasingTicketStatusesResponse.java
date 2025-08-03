package org.example.coretrack.dto.purchasingTicket;

import java.util.List;


public class PurchasingTicketStatusesResponse {
    private List<StatusInfo> purchasingTicketStatuses;
    private List<StatusInfo> purchasingTicketDetailStatuses;

    

    public PurchasingTicketStatusesResponse(List<StatusInfo> purchasingTicketStatuses,
            List<StatusInfo> purchasingTicketDetailStatuses) {
        this.purchasingTicketStatuses = purchasingTicketStatuses;
        this.purchasingTicketDetailStatuses = purchasingTicketDetailStatuses;
    }

    public List<StatusInfo> getPurchasingTicketStatuses() {
        return purchasingTicketStatuses;
    }

    public void setPurchasingTicketStatuses(List<StatusInfo> purchasingTicketStatuses) {
        this.purchasingTicketStatuses = purchasingTicketStatuses;
    }

    public List<StatusInfo> getPurchasingTicketDetailStatuses() {
        return purchasingTicketDetailStatuses;
    }

    public void setPurchasingTicketDetailStatuses(List<StatusInfo> purchasingTicketDetailStatuses) {
        this.purchasingTicketDetailStatuses = purchasingTicketDetailStatuses;
    }

    public static class StatusInfo {
        private String name;
        private String displayName;
        private String description;

        public StatusInfo() {}

        public StatusInfo(String name, String displayName, String description) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
