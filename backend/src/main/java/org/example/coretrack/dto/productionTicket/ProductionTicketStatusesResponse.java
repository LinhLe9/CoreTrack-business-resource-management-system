package org.example.coretrack.dto.productionTicket;

import java.util.List;

public class ProductionTicketStatusesResponse {
    private List<StatusInfo> productionTicketStatuses;
    private List<StatusInfo> productionTicketDetailStatuses;

    public ProductionTicketStatusesResponse() {}

    public ProductionTicketStatusesResponse(List<StatusInfo> productionTicketStatuses, List<StatusInfo> productionTicketDetailStatuses) {
        this.productionTicketStatuses = productionTicketStatuses;
        this.productionTicketDetailStatuses = productionTicketDetailStatuses;
    }

    public List<StatusInfo> getProductionTicketStatuses() {
        return productionTicketStatuses;
    }

    public void setProductionTicketStatuses(List<StatusInfo> productionTicketStatuses) {
        this.productionTicketStatuses = productionTicketStatuses;
    }

    public List<StatusInfo> getProductionTicketDetailStatuses() {
        return productionTicketDetailStatuses;
    }

    public void setProductionTicketDetailStatuses(List<StatusInfo> productionTicketDetailStatuses) {
        this.productionTicketDetailStatuses = productionTicketDetailStatuses;
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