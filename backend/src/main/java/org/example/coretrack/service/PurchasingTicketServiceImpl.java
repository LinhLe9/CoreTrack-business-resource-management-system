package org.example.coretrack.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.coretrack.dto.purchasingTicket.BulkCreatePurchasingTicketRequest;
import org.example.coretrack.dto.purchasingTicket.BulkCreatePurchasingTicketResponse;
import org.example.coretrack.dto.purchasingTicket.CreatePurchasingTicketResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketDetailShortResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketDetailStatusLogResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketCardResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketDetailResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketStatusLogResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingTicketStatusesResponse;
import org.example.coretrack.dto.purchasingTicket.PurchasingStatusTransitionRule;
import org.example.coretrack.dto.purchasingTicket.UpdatePurchasingDetailStatusRequest;
import org.example.coretrack.dto.material.MaterialSupplierResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.material.inventory.MaterialInventory;
import org.example.coretrack.model.material.inventory.MaterialInventoryLog;
import org.example.coretrack.model.material.inventory.materialInventoryReferenceDocumentType;
import org.example.coretrack.model.material.inventory.materialInventoryTransactionSourceType;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.InventoryTransactionType;
import org.example.coretrack.model.product.inventory.StockType;
import org.example.coretrack.model.purchasingTicket.PurchasingTicket;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetail;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetailStatus;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetailStatusLog;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketStatus;
import org.example.coretrack.model.purchasingTicket.PurchasingTicketStatusLog;
import org.example.coretrack.model.supplier.MaterialSupplier;
import org.example.coretrack.repository.MaterialInventoryLogRepository;
import org.example.coretrack.repository.MaterialInventoryRepository;
import org.example.coretrack.repository.MaterialVariantRepository;
import org.example.coretrack.repository.MaterialSupplierRepository;
import org.example.coretrack.repository.PurchasingTicketDetailRepository;
import org.example.coretrack.repository.PurchasingTicketRepository;
import org.example.coretrack.repository.PurchasingTicketDetailStatusLogRepository;
import org.example.coretrack.repository.PurchasingTicketStatusLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.transaction.Transactional;

import org.example.coretrack.model.notification.Notification;
import org.example.coretrack.model.notification.NotificationType;
import org.example.coretrack.service.NotificationService;
import org.example.coretrack.service.NotificationTargetService;
import org.example.coretrack.service.EmailSendingService;

@Service
public class PurchasingTicketServiceImpl implements PurchasingTicketService {

    @Autowired
    private MaterialVariantRepository materialVariantRepository;

    @Autowired
    private MaterialInventoryRepository materialInventoryRepository;

    @Autowired
    private PurchasingTicketRepository purchasingTicketRepository;

    @Autowired
    private PurchasingTicketDetailRepository purchasingTicketDetailRepository;

    @Autowired
    private PurchasingTicketStatusLogRepository purchasingTicketStatusLogRepository;

    @Autowired
    private PurchasingTicketDetailStatusLogRepository purchasingTicketDetailStatusLogRepository;

    @Autowired
    private MaterialInventoryLogRepository materialInventoryLogRepository;

    @Autowired
    private MaterialInventoryService materialInventoryService;

    @Autowired
    private MaterialSupplierRepository materialSupplierRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationTargetService notificationTargetService;

    @Autowired
    private EmailSendingService emailSendingService;

    @Override
    public BulkCreatePurchasingTicketResponse bulkCreatePurchasingTicket(BulkCreatePurchasingTicketRequest request, User user) {
        System.out.println("=== Starting bulkCreatePurchasingTicket ===");
        System.out.println("Request: " + request);
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Ticket name is required");
        }
        
        if (CollectionUtils.isEmpty(request.getSingleTicket())) {
            throw new RuntimeException("At least one purchasing ticket is required");
        }
        
        PurchasingTicket ticket = new PurchasingTicket();
        ticket.setName(request.getName().trim());
        ticket.setStatus(PurchasingTicketStatus.NEW);
        ticket.setActive(true);
        ticket.setCreatedBy(user);
        ticket.setUpdatedBy(user);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        List<CreatePurchasingTicketResponse> createdTickets = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalRequested = request.getSingleTicket().size();
        int totalCreated = 0;
        int totalFailed = 0;

        for (int i = 0; i < request.getSingleTicket().size(); i++) {
            var purchasingTicketRequest = request.getSingleTicket().get(i);
            System.out.println("Processing purchasing ticket " + (i + 1) + ": " + purchasingTicketRequest.getMaterialVariantSku());
            String sku = purchasingTicketRequest.getMaterialVariantSku();
            try {
                // Validate material variant
                MaterialVariant materialVariant = materialVariantRepository.findBySkuAndCompany(sku, user.getCompany())
                    .orElseThrow(() -> new RuntimeException("Material Variant not found with SKU: " + sku));

                if (!materialVariant.isActive()) {
                    throw new RuntimeException("Material Variant is not active: " + sku);
                }

                Optional<MaterialInventory> inventory = materialInventoryRepository.findByMaterialVariant_IdAndMaterialVariant_Material_Company(materialVariant.getId(), user.getCompany());
                boolean needToCreateInventory = (inventory.isEmpty());

                // Convert LocalDate to LocalDateTime with default time 00:00:00
                LocalDateTime expectedCompleteDateTime = purchasingTicketRequest.getExpectedCompleteDate().atStartOfDay();
                // Create purchasing ticket detail
                PurchasingTicketDetail detail = new PurchasingTicketDetail();
                detail.setMaterialVariant(materialVariant);
                detail.setQuantity(purchasingTicketRequest.getQuantity());
                detail.setStatus(PurchasingTicketDetailStatus.NEW);
                detail.setExpected_ready_date(expectedCompleteDateTime);
                detail.setReady_date(null);
                detail.setActive(true);
                detail.setCreatedBy(user);
                detail.setUpdatedBy(user);
                detail.setCreatedAt(LocalDateTime.now());
                detail.setUpdatedAt(LocalDateTime.now());
                detail.setPurchasingTicket(ticket); // Set the purchasing ticket reference

                // Add detail to ticket
                ticket.getTicketDetail().add(detail);
                // If we need to create ProductInventory, do it now
                    if (needToCreateInventory) {
                        System.out.println("Creating default material inventory for SKU: " + sku);
                        createDefaultMaterialInventory(materialVariant, user, ticket.getId());
                        System.out.println("Created product inventory for SKU: " + sku);
                    }
                totalCreated++;

                // Create response
                CreatePurchasingTicketResponse response = new CreatePurchasingTicketResponse();
                response.setId(detail.getId());
                response.setName(ticket.getName());
                response.setMaterialVariantSku(materialVariant.getSku());
                response.setMaterialVariantId(materialVariant.getId());
                response.setQuantity(detail.getQuantity());
                response.setStatus(detail.getStatus().name());
                response.setExpected_ready_date(detail.getExpected_ready_date());
                response.setCreatedAt(detail.getCreatedAt());
                response.setCreatedBy(user.getUsername());
                response.setRole(user.getRole().name());
                
                createdTickets.add(response);
                System.out.println("Successfully created detail for material variant: " + sku);

            } catch (Exception e) {
                String error = "Failed to create detail for material variant " + sku + ": " + e.getMessage();
                errors.add(error);
                totalFailed++;
                System.out.println("Error creating detail: " + error);
            }
        }

        // Only save ticket if at least one detail was created successfully
        if (totalCreated > 0) {
            try {
                // Save ticket
                ticket = purchasingTicketRepository.save(ticket);
                System.out.println("Saved purchasing ticket with ID: " + ticket.getId());

                // Create initial status log for ticket
                createInitialTicketStatusLog(ticket, user);
                System.out.println("Created initial ticket status log");

                // Create initial status logs for all details after ticket is saved
                for (PurchasingTicketDetail detail : ticket.getTicketDetail()) {
                    createInitialDetailStatusLog(detail, user);
                }
                System.out.println("Created initial detail status logs for all details");

            } catch (Exception e) {
                String error = "Failed to save purchasing ticket: " + e.getMessage();
                errors.add(error);
                totalFailed++;
                System.out.println("Error saving ticket: " + error);
            }
        }

        boolean overallSuccess = totalFailed == 0;
        String message = overallSuccess ? 
            "Successfully created purchasing ticket with " + totalCreated + " details" :
            "Created purchasing ticket with " + totalCreated + " details, " + totalFailed + " failed";

        BulkCreatePurchasingTicketResponse response = new BulkCreatePurchasingTicketResponse(overallSuccess, message, createdTickets, errors, 
            totalRequested, totalCreated, totalFailed);

        System.out.println("=== Completed bulkCreatePurchasingTicket ===");
        System.out.println("Total created: " + totalCreated + ", Total failed: " + totalFailed);
        
        return response;
    }

    private void createInitialTicketStatusLog(PurchasingTicket ticket, User user) {
        try {
            System.out.println("Creating initial ticket status log for ticket ID: " + ticket.getId());
            
            PurchasingTicketStatusLog log = new PurchasingTicketStatusLog();
            log.setPurchasingTicket(ticket);
            log.setNew_status(PurchasingTicketStatus.NEW);
            log.setOld_status(PurchasingTicketStatus.NEW); // Set to NEW instead of null
            log.setNote("Initial status log");
            log.setUpdatedBy(user);
            log.setUpdatedAt(LocalDateTime.now());
            
            purchasingTicketStatusLogRepository.save(log);
            System.out.println("Successfully created initial ticket status log");
            
        } catch (Exception e) {
            System.out.println("Error creating initial ticket status log: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to make the error visible
        }
    }

    private void createInitialDetailStatusLog(PurchasingTicketDetail detail, User user) {
        try {
            System.out.println("Creating initial detail status log for detail ID: " + detail.getId());
            
            PurchasingTicketDetailStatusLog log = new PurchasingTicketDetailStatusLog();
            log.setPurchasingTicketDetail(detail);
            log.setNew_status(PurchasingTicketDetailStatus.NEW);
            log.setOld_status(PurchasingTicketDetailStatus.NEW); // Set to NEW instead of null
            log.setNote("Initial status log");
            log.setUpdatedBy(user);
            log.setUpdatedAt(LocalDateTime.now());
            
            purchasingTicketDetailStatusLogRepository.save(log);
            System.out.println("Successfully created initial detail status log");
            
        } catch (Exception e) {
            System.out.println("Error creating initial detail status log: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to make the error visible
        }
    }

    private MaterialInventory createDefaultMaterialInventory(MaterialVariant materialVariant, User user, Long purchasingTicketId){
        // Create initial log for default inventory creation
        MaterialInventoryLog initialLog = new MaterialInventoryLog(
            LocalDateTime.now(),
            null, // Will be set after inventory is created
            StockType.CURRENT,
            InventoryTransactionType.SET,
            materialInventoryTransactionSourceType.SET_INVENTORY,
            BigDecimal.ZERO, // quantity = 0
            BigDecimal.ZERO, // beforeQuantity = 0
            BigDecimal.ZERO, // afterQuantity = 0
            "Default MaterialInventory created for Production Ticket", // note
            materialInventoryReferenceDocumentType.PURCHASING_TICKET, // referenceDocumentType
            purchasingTicketId, // referenceDocumentId - ID of the production ticket
            user
        );

        List<MaterialInventoryLog> logs = new ArrayList<>();
        logs.add(initialLog);

        MaterialInventory defaultInventory = new MaterialInventory(
            materialVariant,
            BigDecimal.ZERO,  // currentStock = 0
            BigDecimal.ZERO,  // minAlertStock = 0
            BigDecimal.valueOf(100),  // maxStockLevel = 100
            logs,  // logs list with initial log
            InventoryStatus.OUT_OF_STOCK,  // default status
            user
        );
        
        // Set default values
        defaultInventory.setFutureStock(BigDecimal.ZERO);
        defaultInventory.setAllocatedStock(BigDecimal.ZERO);
        
        // Save inventory first
        defaultInventory = materialInventoryRepository.save(defaultInventory);
        
        // Update the log with the saved inventory reference
        initialLog.setMaterialInventory(defaultInventory);
        materialInventoryLogRepository.save(initialLog);
        
        return defaultInventory;
    }

    @Override
    public PurchasingTicketResponse getPurchasingTicketById(Long id, User user) {
        PurchasingTicket ticket = purchasingTicketRepository.findByIdAndIsActiveTrueAndCompany(id, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Purchasing ticket not found with id: " + id));

        User createdBy = ticket.getCreatedBy();
        User updatedBy = ticket.getUpdatedBy();

        List<PurchasingTicketDetailShortResponse> detailResponses = ticket.getTicketDetail().stream()
            .filter(detail -> detail.isActive())
            .map(detail -> {
                PurchasingTicketDetailShortResponse response = new PurchasingTicketDetailShortResponse();
                response.setId(detail.getId());
                response.setMaterialVariantSku(detail.getMaterialVariant().getSku());
                response.setQuantity(detail.getQuantity());
                response.setExpected_ready_date(detail.getExpected_ready_date());
                response.setReady_date(detail.getReady_date());
                response.setStatus(detail.getStatus().name());
                response.setCreatedAt(detail.getCreatedAt());
                response.setLastUpdatedAt(detail.getUpdatedAt());
                response.setCreatedBy_name(detail.getCreatedBy().getUsername());
                response.setCreatedBy_role(detail.getCreatedBy().getRole().name());
                response.setLastUpdatedAt_name(detail.getCreatedBy().getUsername());
                response.setLastUpdatedAt_role(detail.getCreatedBy().getRole().name());
                return response;
            })
            .collect(Collectors.toList());

        List<PurchasingTicketStatusLogResponse> logResponses = ticket.getStatusLogs().stream()
            .map(log -> {
                PurchasingTicketStatusLogResponse response = new PurchasingTicketStatusLogResponse();
                response.setId(log.getId());
                response.setPurchasingTicketId(log.getPurchasingTicket().getId());
                response.setPurchasingTicketName(ticket.getName());
                response.setNew_status(log.getNew_status().name());
                response.setOld_status(log.getOld_status() != null ? log.getOld_status().name() : null);
                response.setNote(log.getNote());
                response.setUpdatedAt(log.getUpdatedAt());
                response.setUpdatedByName(log.getUpdatedBy().getUsername());
                response.setUpdatedByRole(log.getUpdatedBy().getRole().name());
                return response;
            })
            .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
            .collect(Collectors.toList());

        PurchasingTicketResponse response = new PurchasingTicketResponse();
        response.setId(ticket.getId());
        response.setName(ticket.getName());
        response.setCompleted_date(ticket.getCompleted_date());
        response.setStatus(ticket.getStatus().name());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setCreatedBy(createdBy.getUsername());
        response.setCreatedBy_role(createdBy.getRole().name());
        response.setLastUpdatedAt(ticket.getUpdatedAt());
        response.setLastUpdateBy(updatedBy.getUsername());
        response.setLastUpdateBy_role(updatedBy.getRole().name());
        response.setDetail(detailResponses);
        response.setLogs(logResponses);

        return response;
    }

    @Override
    public Page<PurchasingTicketCardResponse> getPurchasingTickets(String search, List<String> ticketStatus, Pageable pageable, User user) {
        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        List<PurchasingTicketStatus> processedStatuses = null;
        if (!CollectionUtils.isEmpty(ticketStatus)) {
            processedStatuses = ticketStatus.stream()
                .map(s -> {
                    try {
                        return PurchasingTicketStatus.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid product status: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (processedStatuses.isEmpty()) {
                processedStatuses = null;
            }
        }
        Page<PurchasingTicket> tickets = purchasingTicketRepository.findBySearchAndStatusAndCompany(processedSearch, processedStatuses, user.getCompany(), pageable);
        
        return tickets.map(PurchasingTicketCardResponse::new);
    }

    @Override
    public List<PurchasingTicketCardResponse> getAutoComplete(String search, User user) {
        // Handle null or empty search
        if (search == null || search.trim().isEmpty()) {
            // Return empty list for null/empty search to avoid showing all tickets
            return new ArrayList<>();
        }
        
        String processedSearch = search.trim();
        try {
            // Use company-based query for multi-tenancy
            List<PurchasingTicket> tickets = purchasingTicketRepository.findBySearchForAutoCompleteAndCompany(
                processedSearch, user.getCompany());
            
            return tickets.stream()
                .map(PurchasingTicketCardResponse::new)
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error in autocomplete search for user " + user.getId() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<PurchasingStatusTransitionRule> getStatusTransitionRules() {
        List<PurchasingStatusTransitionRule> rules = new ArrayList<>();
        
        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.NEW,
            List.of(PurchasingTicketDetailStatus.APPROVAL),
            "New tickets can be approved"
        ));
        
        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.APPROVAL,
            List.of(PurchasingTicketDetailStatus.SUCCESSFUL),
            "Approved tickets can be purchased successfully"
        ));
        
        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.SUCCESSFUL,
            List.of(PurchasingTicketDetailStatus.SHIPPING),
            "Succeed tickets can be shipped by supplier"
        ));

        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.SHIPPING,
            List.of(PurchasingTicketDetailStatus.READY),
            "Shipped tickets can be ready to use"
        ));
        
        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.READY,
            List.of(PurchasingTicketDetailStatus.CLOSED),
            "Ready tickets can only be closed"
        ));
        
        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.CLOSED,
            List.of(),
            "Closed tickets cannot be changed"
        ));
        
        rules.add(new PurchasingStatusTransitionRule(
            PurchasingTicketDetailStatus.CANCELLED,
            List.of(),
            "Cancelled tickets cannot be changed"
        ));
        
        return rules;
    }

    @Override
    public PurchasingTicketDetailResponse updateDetailStatus(Long ticketId, Long detailId, UpdatePurchasingDetailStatusRequest request, User user) {
        System.out.println("=== Starting updateDetailStatus ===");
        System.out.println("Ticket ID: " + ticketId + ", Detail ID: " + detailId);
        System.out.println("Request: " + request);
        
        PurchasingTicket ticket = purchasingTicketRepository.findByIdAndIsActiveTrueAndCompany(ticketId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Purchasing ticket not found with id: " + ticketId));

        PurchasingTicketDetail detail = purchasingTicketDetailRepository.findByIdAndIsActiveTrue(detailId)
            .orElseThrow(() -> new RuntimeException("Purchasing ticket detail not found with id: " + detailId));

        // Validate that detail belongs to ticket
        if (!detail.getPurchasingTicket().getId().equals(ticketId)) {
            throw new RuntimeException("Detail does not belong to the specified ticket");
        }

        PurchasingTicketDetailStatus oldStatus = detail.getStatus();
        PurchasingTicketDetailStatus newStatus = request.getNewStatus();

        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);

        // Update detail status
        detail.setStatus(newStatus);
        detail.setUpdatedBy(user);
        detail.setUpdatedAt(LocalDateTime.now());

        // Update ready date if status is READY
        if (newStatus == PurchasingTicketDetailStatus.READY) {
            detail.setReady_date(LocalDateTime.now());
        }

        // save the detail
        detail = purchasingTicketDetailRepository.save(detail);

        // Log status change
        logDetailStatusChange(detail, oldStatus, newStatus, request.getNote(), user);

        // Update ticket status based on details
        PurchasingTicketStatus oldTicketStatus = ticket.getStatus();
        updatePurchasingTicketStatusBasedOnDetails(ticket, user);

        // Update inventory based on status change
        updateMaterialInventory(detail, oldStatus, request.getNewStatus(), user);
        
        // Create notification for status changes
        if (!oldStatus.equals(request.getNewStatus())) {
            createPurchasingTicketDetailNotification(user, detail, oldStatus, request.getNewStatus());
        }
        if (!oldTicketStatus.equals(ticket.getStatus())) {
            createPurchasingTicketNotification(user, ticket, oldTicketStatus, ticket.getStatus());
        }

        // Send email alerts for status changes
        try {
            if (!oldStatus.equals(request.getNewStatus())) {
                emailSendingService.sendPurchasingTicketDetailStatusChangeAlert(detail, oldStatus.name(), request.getNewStatus().name());
            }
            if (!oldTicketStatus.equals(ticket.getStatus())) {
                emailSendingService.sendPurchasingTicketStatusChangeAlert(ticket, oldTicketStatus.name(), ticket.getStatus().name());
            }
        } catch (Exception emailError) {
            System.err.println("=== WARNING: Failed to send email alert ===");
            System.err.println("Email error: " + emailError.getMessage());
            System.err.println("But status update was successful");
            System.err.println("===========================================");
            // Don't re-throw the email error to avoid rolling back the status update
        }

        // Create response
        return createPurchasingTicketDetailResponse(detail);
    }

    /*
     * Helper method to validate status change
     */
    private void validateStatusTransition(PurchasingTicketDetailStatus currentStatus, PurchasingTicketDetailStatus newStatus) {
        List<PurchasingStatusTransitionRule> rules = getStatusTransitionRules();
        
        PurchasingStatusTransitionRule rule = rules.stream()
            .filter(r -> r.getCurrentStatus() == currentStatus)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No transition rule found for status: " + currentStatus));

        if (!rule.getAllowedTransitions().contains(newStatus)) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    /*
     * Method to update the overall purchasing ticket base on the detail ticket
     */
    private void updatePurchasingTicketStatusBasedOnDetails(PurchasingTicket ticket, User user) {
        List<PurchasingTicketDetail> activeDetails = ticket.getTicketDetail().stream()
            .filter(detail -> detail.isActive())
            .collect(Collectors.toList());

        if (activeDetails.isEmpty()) {
            return;
        }

        PurchasingTicketStatus newTicketStatus = null;
        boolean allReady = activeDetails.stream().allMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.READY);
        boolean allCancelled = activeDetails.stream().allMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.CANCELLED);
        boolean allApproval = activeDetails.stream().allMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.APPROVAL);
        boolean allSuccessful = activeDetails.stream().allMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.SUCCESSFUL);
        boolean allShipping = activeDetails.stream().allMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.SHIPPING);
        boolean anyCancelled = activeDetails.stream().anyMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.CANCELLED);
        boolean anyApproval = activeDetails.stream().anyMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.APPROVAL);
        boolean anySuccessful = activeDetails.stream().anyMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.SUCCESSFUL);
        boolean anyShipping = activeDetails.stream().anyMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.SHIPPING);
        boolean anyReady = activeDetails.stream().anyMatch(detail -> detail.getStatus() == PurchasingTicketDetailStatus.READY);

        if (allCancelled) {
            newTicketStatus = PurchasingTicketStatus.CANCELLED;
        } else if (allReady) {
            newTicketStatus = PurchasingTicketStatus.READY;
        } else if (allApproval){
            newTicketStatus = PurchasingTicketStatus.APPROVAL;
        } else if (allSuccessful){
            newTicketStatus = PurchasingTicketStatus.SUCCESSFUL;
        } else if(allShipping){
            newTicketStatus = PurchasingTicketStatus.SHIPPING;
        } else if (anyApproval){
            newTicketStatus = PurchasingTicketStatus.PARTIAL_APPROVAL;
        } else if (anySuccessful){
            newTicketStatus = PurchasingTicketStatus.PARTIAL_SUCCESSFUL;
        } else if (anyShipping){
            newTicketStatus = PurchasingTicketStatus.PARTIAL_SHIPPING;
        } else if (anyReady){
            newTicketStatus = PurchasingTicketStatus.PARTIAL_READY;
        } else if (anyCancelled) {
            newTicketStatus = PurchasingTicketStatus.PARTIAL_CANCELLED;
        } 
        if (newTicketStatus != ticket.getStatus()) {
            PurchasingTicketStatus oldStatus = ticket.getStatus();
            ticket.setStatus(newTicketStatus);
            ticket.setUpdatedBy(user);
            ticket.setUpdatedAt(LocalDateTime.now());
            purchasingTicketRepository.save(ticket);

            // Log ticket status change
            logTicketStatusChange(ticket, oldStatus, newTicketStatus, "Status updated based on details", user);
        }
    }

    /*
     * Helper method to save the Purchasing ticket status change log
     */
    private void logTicketStatusChange(PurchasingTicket ticket, PurchasingTicketStatus oldStatus, 
                                     PurchasingTicketStatus newStatus, String note, User user) {
        PurchasingTicketStatusLog log = new PurchasingTicketStatusLog();
        log.setPurchasingTicket(ticket);
        log.setOld_status(oldStatus);
        log.setNew_status(newStatus);
        log.setNote(note);
        log.setUpdatedBy(user);
        log.setUpdatedAt(LocalDateTime.now());
        purchasingTicketStatusLogRepository.save(log);
    }

    /*
     * Helper method to save the Purchasing ticket detail status change log
     */
    private void logDetailStatusChange(PurchasingTicketDetail detail, PurchasingTicketDetailStatus oldStatus,
                                     PurchasingTicketDetailStatus newStatus, String note, User user) {
        PurchasingTicketDetailStatusLog log = new PurchasingTicketDetailStatusLog();
        log.setPurchasingTicketDetail(detail);
        log.setOld_status(oldStatus);
        log.setNew_status(newStatus);
        log.setNote(note);
        log.setUpdatedBy(user);
        log.setUpdatedAt(LocalDateTime.now());
        purchasingTicketDetailStatusLogRepository.save(log);
    }

    /*
     * Helper method to update the material inventory
     */
    private void updateMaterialInventory(PurchasingTicketDetail detail, PurchasingTicketDetailStatus oldStatus, PurchasingTicketDetailStatus newStatus, User user) {
        MaterialVariant materialVariant = detail.getMaterialVariant();
        if (materialVariant == null) {
            return;
        }

        BigDecimal quantity = detail.getQuantity();
        Long variantId = materialVariant.getId();
        Long ticketId = detail.getPurchasingTicket().getId();

        // MaterialInventory is guaranteed to exist at this point since it's created during ticket creation
        if (oldStatus == PurchasingTicketDetailStatus.NEW && newStatus == PurchasingTicketDetailStatus.APPROVAL) {
            // When status changes from NEW to APPROVAL, add to futureStock
            // This indicates the material is planned for purchasing
            materialInventoryService.addToFutureStock(variantId, quantity, user, ticketId);
            
        } else if (oldStatus == PurchasingTicketDetailStatus.SHIPPING && newStatus == PurchasingTicketDetailStatus.READY) {
            // When status changes from SHIPPING to READY, move from futureStock to currentStock
            // This indicates the material is now ready and available
            materialInventoryService.moveFromFutureToCurrentStock(variantId, quantity, user, ticketId);
            
        } else if ((oldStatus == PurchasingTicketDetailStatus.APPROVAL && newStatus == PurchasingTicketDetailStatus.CANCELLED) ||
                    (oldStatus == PurchasingTicketDetailStatus.SUCCESSFUL && newStatus == PurchasingTicketDetailStatus.CANCELLED) ||
                    (oldStatus == PurchasingTicketDetailStatus.SHIPPING && newStatus == PurchasingTicketDetailStatus.CANCELLED)) {
            // When status changes from APPROVAL to CANCELLED, remove from futureStock
            // This indicates the purchasing is cancelled
            materialInventoryService.removeFromFutureStock(variantId, quantity, user, ticketId);
            
        } else if (oldStatus == PurchasingTicketDetailStatus.READY && newStatus == PurchasingTicketDetailStatus.CANCELLED) {
            // When status changes from READY to CANCELLED, remove from currentStock
            // This indicates the ready material is cancelled
            materialInventoryService.removeFromCurrentStock(variantId, quantity, user, ticketId);
        }
    }

    @Override
    public PurchasingTicket cancelPurchasingTicket(Long ticketId, String reason, User user) {
        System.out.println("=== Starting cancelPurchasingTicket ===");
        System.out.println("Ticket ID: " + ticketId + ", Reason: " + reason);
        
        try {
            PurchasingTicket ticket = purchasingTicketRepository.findByIdAndIsActiveTrueAndCompany(ticketId, user.getCompany())
                .orElseThrow(() -> new RuntimeException("Purchasing ticket not found with id: " + ticketId));

            // Cancel all active details
            for (PurchasingTicketDetail detail : ticket.getTicketDetail()) {
                if (detail.isActive() && detail.getStatus() != PurchasingTicketDetailStatus.CANCELLED) {
                    // Store old status for logging
                    PurchasingTicketDetailStatus oldDetailStatus = detail.getStatus();

                    detail.setStatus(PurchasingTicketDetailStatus.CANCELLED);
                    detail.setUpdatedBy(user);
                    detail.setUpdatedAt(LocalDateTime.now());
                    purchasingTicketDetailRepository.save(detail);
                    System.out.println("Cancelled detail ID: " + detail.getId());

                    // Update inventory based on status change to CANCELLED
                    updateMaterialInventory(detail, oldDetailStatus, PurchasingTicketDetailStatus.CANCELLED, user);

                    // Log detail status change
                    logDetailStatusChange(detail, oldDetailStatus, PurchasingTicketDetailStatus.CANCELLED, 
                                        "Cancelled due to purchasing ticket cancellation: " + reason, user);
                } else {
                    System.out.println("Detail ID: " + detail.getId() + " is already cancelled, skipping...");
                }
            }

            // Update ticket status
            PurchasingTicketStatus oldStatus = ticket.getStatus();
            ticket.setStatus(PurchasingTicketStatus.CANCELLED);
            ticket.setUpdatedBy(user);
            ticket.setUpdatedAt(LocalDateTime.now());

            ticket = purchasingTicketRepository.save(ticket);
            System.out.println("Updated ticket status to: " + ticket.getStatus());

            // Log ticket status change
            logTicketStatusChange(ticket, oldStatus, PurchasingTicketStatus.CANCELLED, "Purchasing ticket cancelled: " + reason, user);
            
            // Create notification for ticket cancellation
            createPurchasingTicketNotification(user, ticket, oldStatus, PurchasingTicketStatus.CANCELLED);
            
            // Send email alert for ticket cancellation
            try {
                emailSendingService.sendPurchasingTicketStatusChangeAlert(ticket, oldStatus.name(), PurchasingTicketStatus.CANCELLED.name());
            } catch (Exception emailError) {
                System.err.println("=== WARNING: Failed to send email alert ===");
                System.err.println("Email error: " + emailError.getMessage());
                System.err.println("But ticket cancellation was successful");
                System.err.println("===========================================");
                // Don't re-throw the email error to avoid rolling back the ticket cancellation
            }

            System.out.println("=== Completed cancelPurchasingTicket ===");
            return ticket;
        } catch (Exception e) {
            System.err.println("=== ERROR in cancelPurchasingTicket ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            throw e;
        }
    }

    @Override
    public PurchasingTicketDetailResponse cancelPurchasingTicketDetail(Long ticketId, Long detailId, String reason, User user) {
        System.out.println("=== Starting cancelPurchasingTicketDetail ===");
        System.out.println("Ticket ID: " + ticketId + ", Detail ID: " + detailId + ", Reason: " + reason);
        
        PurchasingTicket ticket = purchasingTicketRepository.findByIdAndIsActiveTrueAndCompany(ticketId, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Purchasing ticket not found with id: " + ticketId));

        PurchasingTicketDetail detail = purchasingTicketDetailRepository.findByIdAndIsActiveTrue(detailId)
            .orElseThrow(() -> new RuntimeException("Purchasing ticket detail not found with id: " + detailId));

        // Validate that detail belongs to ticket
        if (!detail.getPurchasingTicket().getId().equals(ticketId)) {
            throw new RuntimeException("Detail does not belong to the specified ticket");
        }

        PurchasingTicketDetailStatus oldStatus = detail.getStatus();
        detail.setStatus(PurchasingTicketDetailStatus.CANCELLED);
        detail.setUpdatedBy(user);
        detail.setUpdatedAt(LocalDateTime.now());
        detail = purchasingTicketDetailRepository.save(detail);

        // Log status change
        logDetailStatusChange(detail, oldStatus, PurchasingTicketDetailStatus.CANCELLED, reason, user);

        // Update inventory based on status change to CANCELLED
        updateMaterialInventory(detail, oldStatus, PurchasingTicketDetailStatus.CANCELLED, user);
        
        // Update ticket status based on details
        PurchasingTicketStatus oldTicketStatus = ticket.getStatus();
        updatePurchasingTicketStatusBasedOnDetails(ticket, user);
        
        // Create notification for status changes
        createPurchasingTicketDetailNotification(user, detail, oldStatus, PurchasingTicketDetailStatus.CANCELLED);
        if (!oldTicketStatus.equals(ticket.getStatus())) {
            createPurchasingTicketNotification(user, ticket, oldTicketStatus, ticket.getStatus());
        }

        // Send email alerts for status changes
        try {
            emailSendingService.sendPurchasingTicketDetailStatusChangeAlert(detail, oldStatus.name(), PurchasingTicketDetailStatus.CANCELLED.name());
            if (!oldTicketStatus.equals(ticket.getStatus())) {
                emailSendingService.sendPurchasingTicketStatusChangeAlert(ticket, oldTicketStatus.name(), ticket.getStatus().name());
            }
        } catch (Exception emailError) {
            System.err.println("=== WARNING: Failed to send email alert ===");
            System.err.println("Email error: " + emailError.getMessage());
            System.err.println("But detail cancellation was successful");
            System.err.println("===========================================");
            // Don't re-throw the email error to avoid rolling back the detail cancellation
        }

        System.out.println("=== Completed cancelPurchasingTicketDetail ===");
        return getPurchasingTicketDetails(ticketId, detailId, user);
    }

    @Override
    public PurchasingTicketDetailResponse getPurchasingTicketDetails(Long id, Long detailId, User user) {
        System.out.println("=== Starting getPurchasingTicketDetails ===");
        System.out.println("Ticket ID: " + id + ", Detail ID: " + detailId);
        
        PurchasingTicket ticket = purchasingTicketRepository.findByIdAndIsActiveTrueAndCompany(id, user.getCompany())
            .orElseThrow(() -> new RuntimeException("Purchasing ticket not found with id: " + id));

        PurchasingTicketDetail detail = purchasingTicketDetailRepository.findByIdAndIsActiveTrue(detailId)
            .orElseThrow(() -> new RuntimeException("Purchasing ticket detail not found with id: " + detailId));

        // Validate that detail belongs to ticket
        if (!detail.getPurchasingTicket().getId().equals(id)) {
            throw new RuntimeException("Detail does not belong to the specified ticket");
        }

        System.out.println("Found detail: " + detail.getId() + ", Status: " + detail.getStatus());
        
        return createPurchasingTicketDetailResponse(detail);
    }

    private PurchasingTicketDetailResponse createPurchasingTicketDetailResponse(PurchasingTicketDetail detail) {
        PurchasingTicketDetailResponse response = new PurchasingTicketDetailResponse();
        response.setId(detail.getId());
        response.setMaterialVariantSku(detail.getMaterialVariant().getSku());
        response.setQuantity(detail.getQuantity());
        response.setStatus(detail.getStatus().name());
        response.setExpected_ready_date(detail.getExpected_ready_date());
        response.setReady_date(detail.getReady_date());
        response.setCreatedAt(detail.getCreatedAt());
        response.setLastUpdatedAt(detail.getUpdatedAt());
        response.setCreatedBy_name(detail.getCreatedBy().getUsername());
        response.setCreatedBy_role(detail.getCreatedBy().getRole().name());
        response.setLastUpdatedAt_name(detail.getUpdatedBy().getUsername());
        response.setLastUpdatedAt_role(detail.getUpdatedBy().getRole().name());

        // Get material suppliers for this material variant
        List<MaterialSupplierResponse> materialSuppliers = new ArrayList<>();
        try {
            MaterialVariant materialVariant = detail.getMaterialVariant();
            if (materialVariant != null && materialVariant.getMaterial() != null) {
                List<MaterialSupplier> suppliers = materialSupplierRepository.findByMaterial(materialVariant.getMaterial());
                materialSuppliers = suppliers.stream()
                    .filter(supplier -> supplier != null && supplier.getSupplier() != null)
                    .map(supplier -> new MaterialSupplierResponse(supplier))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Error fetching material suppliers for detail " + detail.getId() + ": " + e.getMessage());
            // Continue without suppliers - this is not a critical error
        }
        response.setMaterialSuppliers(materialSuppliers);

        // Add status logs
        List<PurchasingTicketDetailStatusLogResponse> logResponses = detail.getStatusLogs().stream()
            .map(log -> {
                PurchasingTicketDetailStatusLogResponse logResponse = new PurchasingTicketDetailStatusLogResponse();
                logResponse.setId(log.getId());
                logResponse.setPurchasingDetailTicketId(log.getPurchasingTicketDetail().getId());
                logResponse.setNew_status(log.getNew_status().name());
                logResponse.setOld_status(log.getOld_status() != null ? log.getOld_status().name() : null);
                logResponse.setNote(log.getNote());
                logResponse.setUpdatedAt(log.getUpdatedAt());
                logResponse.setUpdatedByName(log.getUpdatedBy().getUsername());
                logResponse.setUpdatedByRole(log.getUpdatedBy().getRole().name());
                return logResponse;
            })
            .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
            .collect(Collectors.toList());

        response.setLogs(logResponses);
        return response;
    }

    @Override
    public PurchasingTicketStatusesResponse getAllPurchasingTicketStatuses() {
        List<PurchasingTicketStatusesResponse.StatusInfo> purchasingTicketStatuses = new ArrayList<>();
        List<PurchasingTicketStatusesResponse.StatusInfo> purchasingTicketDetailStatuses = new ArrayList<>();

        // Add ProductionTicketStatus values
        for (PurchasingTicketStatus status : PurchasingTicketStatus.values()) {
            purchasingTicketStatuses.add(new PurchasingTicketStatusesResponse.StatusInfo(
                status.name(),
                status.getDisplayName(),
                getPurchasingTicketStatusDescription(status)
            ));
        }

        // Add ProductionTicketDetailStatus values
        for (PurchasingTicketDetailStatus status : PurchasingTicketDetailStatus.values()) {
            purchasingTicketDetailStatuses.add(new PurchasingTicketStatusesResponse.StatusInfo(
                status.name(),
                status.getDisplayName(),
                getPurchasingTicketDetailStatusDescription(status)
            ));
        }

        return new PurchasingTicketStatusesResponse(purchasingTicketStatuses, purchasingTicketDetailStatuses);
    }

    private String getPurchasingTicketStatusDescription(PurchasingTicketStatus status) {
        switch (status) {
            case NEW:
                return "New purchasing ticket";
            case PARTIAL_APPROVAL:
                return "Purchasing ticket partially approved";
            case APPROVAL:
                return "Purchasing ticket is approved";
            case PARTIAL_SUCCESSFUL:
                return "Purchasing ticket is partially purchased successfully";
            case SUCCESSFUL:
                return "Purchasing ticket is successfully purchased";
            case PARTIAL_SHIPPING:
                return "Purchasing ticket is partially shipped";
            case SHIPPING:
                return "Purchasing ticket is shipped";
            case PARTIAL_READY:
                return "Purchasing ticket is partially ready to use";
            case READY:
                return "Purchasing ticket is ready to use";
            case CANCELLED:
                return "Purchasing ticket cancelled";
            case PARTIAL_CANCELLED:
                return "Purchasing ticket partially cancelled";
            default:
                return "Unknown status";
        }
    }

    private String getPurchasingTicketDetailStatusDescription(PurchasingTicketDetailStatus status) {
        switch (status) {
            case NEW:
                return "Purchasing detail is newly created and waiting for approval";
            case APPROVAL:
                return "Purchasing detail is approved and in progress";
            case READY:
                return "Purchasing detail is ready for shipment";
            case SHIPPING:
                return "Purchasing detail is in shipping";
            case SUCCESSFUL:
                return "Purchasing detail is successfully completed";
            case CANCELLED:
                return "Purchasing detail has been cancelled";
            default:
                return "Unknown purchasing detail status";
        }
    }
    
    private void createPurchasingTicketNotification(User user, PurchasingTicket ticket, 
                                                 PurchasingTicketStatus oldStatus, PurchasingTicketStatus newStatus) {
        if (!oldStatus.equals(newStatus)) {
            String title = getPurchasingTicketNotificationTitle(newStatus);
            String message = getPurchasingTicketNotificationMessage(ticket, oldStatus, newStatus);
            
            Notification notification = new Notification(NotificationType.PURCHASING_TICKET_STATUS_CHANGE, title, message);
            List<User> notificationTargets = notificationTargetService.getPurchasingNotificationTargets();
            notificationService.createTicketNotification(notification, notificationTargets);
        }
    }
    
    private void createPurchasingTicketDetailNotification(User user, PurchasingTicketDetail detail, 
                                                       PurchasingTicketDetailStatus oldStatus, PurchasingTicketDetailStatus newStatus) {
        if (!oldStatus.equals(newStatus)) {
            String title = getPurchasingTicketDetailNotificationTitle(newStatus);
            String message = getPurchasingTicketDetailNotificationMessage(detail, oldStatus, newStatus);
            
            Notification notification = new Notification(NotificationType.PURCHASING_TICKET_DETAIL_STATUS_CHANGE, title, message);
            List<User> notificationTargets = notificationTargetService.getPurchasingNotificationTargets();
            notificationService.createTicketNotification(notification, notificationTargets);
        }
    }
    
    private String getPurchasingTicketNotificationTitle(PurchasingTicketStatus status) {
        switch (status) {
            case NEW:
                return "Purchasing Ticket Created";
            case APPROVAL:
                return "Purchasing Ticket Approved";
            case READY:
                return "Purchasing Ticket Ready";
            case SHIPPING:
                return "Purchasing Ticket Shipping";
            case SUCCESSFUL:
                return "Purchasing Ticket Successful";
            case CANCELLED:
                return "Purchasing Ticket Cancelled";
            case PARTIAL_APPROVAL:
                return "Purchasing Ticket Partially Approved";
            case PARTIAL_READY:
                return "Purchasing Ticket Partially Ready";
            case PARTIAL_SHIPPING:
                return "Purchasing Ticket Partially Shipping";
            case PARTIAL_SUCCESSFUL:
                return "Purchasing Ticket Partially Successful";
            case PARTIAL_CANCELLED:
                return "Purchasing Ticket Partially Cancelled";
            default:
                return "Purchasing Ticket Status Changed";
        }
    }
    
    private String getPurchasingTicketNotificationMessage(PurchasingTicket ticket, PurchasingTicketStatus oldStatus, PurchasingTicketStatus newStatus) {
        String ticketName = ticket.getName();
        String ticketId = ticket.getId().toString();
        
        return String.format("Purchasing Ticket '%s' (ID: %s) status changed from %s to %s", 
                           ticketName, ticketId, oldStatus.name(), newStatus.name());
    }
    
    private String getPurchasingTicketDetailNotificationTitle(PurchasingTicketDetailStatus status) {
        switch (status) {
            case NEW:
                return "Purchasing Detail Created";
            case APPROVAL:
                return "Purchasing Detail Approved";
            case READY:
                return "Purchasing Detail Ready";
            case SHIPPING:
                return "Purchasing Detail Shipping";
            case SUCCESSFUL:
                return "Purchasing Detail Successful";
            case CANCELLED:
                return "Purchasing Detail Cancelled";
            default:
                return "Purchasing Detail Status Changed";
        }
    }
    
    private String getPurchasingTicketDetailNotificationMessage(PurchasingTicketDetail detail, 
                                                             PurchasingTicketDetailStatus oldStatus, PurchasingTicketDetailStatus newStatus) {
        String materialName = detail.getMaterialVariant().getName();
        String detailId = detail.getId().toString();
        String ticketName = detail.getPurchasingTicket().getName();
        
        return String.format("Purchasing Detail for material '%s' (Detail ID: %s) in ticket '%s' status changed from %s to %s", 
                           materialName, detailId, ticketName, oldStatus.name(), newStatus.name());
    }

}
