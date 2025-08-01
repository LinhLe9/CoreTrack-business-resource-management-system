package org.example.coretrack.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.example.coretrack.dto.productionTicket.BomItemProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.BomItemProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.BulkCreateProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.BulkCreateProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.CreateProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.CreateProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.ProductTicketDetailShortResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketCardResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketDetailResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketStatusesResponse;
import org.example.coretrack.dto.productionTicket.StatusTransitionRule;
import org.example.coretrack.dto.productionTicket.UpdateDetailStatusRequest;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.MaterialVariant;
import org.example.coretrack.model.product.ProductVariant;
import org.example.coretrack.model.productionTicket.BomItemProductionTicketDetail;
import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketDetail;
import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatus;
import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatusLog;
import org.example.coretrack.model.productionTicket.ProductionTicketStatus;
import org.example.coretrack.model.productionTicket.ProductionTicketStatusLog;
import org.example.coretrack.repository.MaterialVariantRepository;
import org.example.coretrack.repository.ProductVariantRepository;
import org.example.coretrack.repository.ProductionTicketDetailRepository;
import org.example.coretrack.repository.ProductionTicketRepository;
import org.example.coretrack.repository.ProductionTicketDetailStatusLogRepository;
import org.example.coretrack.repository.ProductionTicketStatusLogRepository;
import org.example.coretrack.repository.ProductInventoryRepository;
import org.example.coretrack.repository.ProductInventoryLogRepository;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.InventoryTransactionType;
import org.example.coretrack.model.product.inventory.ProductInventoryTransactionSourceType;
import org.example.coretrack.model.product.inventory.ProductInventoryReferenceDocumentType;
import org.example.coretrack.model.product.inventory.ProductInventoryLog;
import org.example.coretrack.dto.productionTicket.ProductVariantBomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ProductionTicketServiceImpl implements ProductionTicketService{
    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private MaterialVariantRepository materialVariantRepository;

    @Autowired
    private ProductionTicketRepository productionTicketRepository;

    @Autowired
    private ProductionTicketDetailRepository productionTicketDetailRepository;

    @Autowired
    private ProductionTicketStatusLogRepository productionTicketStatusLogRepository;

    @Autowired
    private ProductionTicketDetailStatusLogRepository productionTicketDetailStatusLogRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private ProductInventoryLogRepository productInventoryLogRepository;

    @Autowired
    private ProductInventoryService productInventoryService;

    /*
     * This use for user to create a production ticket for one productVariant by choose directly its sku from alarm
     */
    @Override
    public CreateProductionTicketResponse createProductionTicket(CreateProductionTicketRequest request, User user) {
        // Validate request
        if (request.getVariantSku() == null || request.getVariantSku().trim().isEmpty()) {
            throw new RuntimeException("Product Variant SKU is required");
        }
        
        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        
        if (request.getExpected_complete_date() == null) {
            throw new RuntimeException("Expected complete date is required");
        }

        if (request.getName() == null){
            throw new RuntimeException("Name is required");
        }
        
        ProductVariant variant = productVariantRepository.findBySku(request.getVariantSku())
                                .orElseThrow(() -> new RuntimeException("Product Variant is not found with SKU: " + request.getVariantSku()));
        
        // Check if variant is active
        if (!variant.isActive()) {
            throw new RuntimeException("Product Variant is not active");
        }

        // Check if ProductInventory exists, if not create default one
        ProductInventory inventory = productInventoryService.getByProductVariantId(variant.getId());
        if (inventory == null) {
            // We'll create the ProductInventory after the ProductionTicket is saved
            // so we can use the ticket ID as referenceDocumentId
        }

        // Create production ticket
        ProductionTicket ticket = new ProductionTicket();
        ticket.setName(request.getName());
        ticket.setStatus(ProductionTicketStatus.NEW);
        ticket.setActive(true);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setCreatedBy(user);
        ticket.setUpdatedBy(user);

        // Create Production Ticket Detail
        ProductionTicketDetail detail = new ProductionTicketDetail();
        detail.setProductVariant(variant);
        detail.setQuantity(request.getQuantity());
        detail.setStatus(ProductionTicketDetailStatus.NEW);
        detail.setExpected_complete_date(request.getExpected_complete_date());
        detail.setCompleted_date(null);
        detail.setCreatedAt(LocalDateTime.now());
        detail.setUpdatedAt(LocalDateTime.now());
        detail.setCreatedBy(user);
        detail.setUpdatedBy(user);
        detail.setProductionTicket(ticket);

        // Add BOM items if provided
        if (request.getBoms() != null && !request.getBoms().isEmpty()) {
            for (BomItemProductionTicketRequest bomRequest : request.getBoms()) {
                MaterialVariant materialVariant = materialVariantRepository.findBySku(bomRequest.getMaterialVariantSku())
                    .orElseThrow(() -> new RuntimeException("Material Variant not found with SKU: " + bomRequest.getMaterialVariantSku()));
                
                if (!materialVariant.isActive()) {
                    throw new RuntimeException("Material Variant is not active");
                }

                BomItemProductionTicketDetail bomItem = new BomItemProductionTicketDetail();
                bomItem.setMaterialVariant(materialVariant);
                bomItem.setPlannedQuantity(bomRequest.getPlannedQuantity());
                bomItem.setActualQuantity(bomRequest.getActualQuantity());
                bomItem.setProductionTicketDetail(detail);
                detail.getBomItem().add(bomItem);
            }
        }

        // Add detail to ticket
        ticket.getTicketDetail().add(detail);

        // Save the ticket with cascade (this will save ticket, detail, and bom items)
        ticket = productionTicketRepository.save(ticket);

        // Now create ProductInventory if it doesn't exist, using the ticket ID
        if (inventory == null) {
            inventory = createDefaultProductInventory(variant, user, ticket.getId());
        }

        // Get the saved detail from the ticket
        ProductionTicketDetail savedDetail = ticket.getTicketDetail().get(0);

        // Create initial status logs
        createInitialTicketStatusLog(ticket, user); // Only one ticket status log
        createInitialDetailStatusLog(savedDetail, user); // One log for the detail

        // Create response for BOM items after saving
        List<BomItemProductionTicketResponse> bomResponse = new ArrayList<>();
        if (request.getBoms() != null && !request.getBoms().isEmpty()) {
            for (BomItemProductionTicketDetail savedBomItem : savedDetail.getBomItem()) {
                BomItemProductionTicketResponse bomItemResponse = new BomItemProductionTicketResponse(
                    savedBomItem.getId(),
                    savedBomItem.getMaterialVariant().getSku(),
                    savedBomItem.getActualQuantity(),
                    savedBomItem.getPlannedQuantity()
                );
                bomResponse.add(bomItemResponse);
            }
        }

        CreateProductionTicketResponse response = new CreateProductionTicketResponse(
            ticket.getId(),
            ticket.getName(),
            savedDetail.getProductVariant().getSku(),
            savedDetail.getProductVariant().getId(),
            savedDetail.getQuantity(),
            savedDetail.getStatus().getDisplayName(),
            savedDetail.getExpected_complete_date(),
            savedDetail.getCompleted_date(),
            savedDetail.getCreatedAt(),
            savedDetail.getCreatedBy().getUsername(),
            savedDetail.getCreatedBy().getRole().toString()
        );
        return response;
    }


    /*
     * Used when user create a production ticket including a list of production ticket detail
     */
    @Override
    public BulkCreateProductionTicketResponse bulkCreateProductionTicket(BulkCreateProductionTicketRequest request, User user) {
        List<CreateProductionTicketResponse> createdTickets = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalRequested = request.getProductVariants().size();
        int totalCreated = 0;
        int totalFailed = 0;

        try {
            // Create one ProductionTicket
            ProductionTicket ticket = new ProductionTicket();
            ticket.setName(request.getName());
            ticket.setStatus(ProductionTicketStatus.NEW);
            ticket.setActive(true);
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setCreatedBy(user);
            ticket.setUpdatedBy(user);

            // Process each product variant to create ProductionTicketDetails
            for (ProductVariantBomRequest productVariantRequest : request.getProductVariants()) {
                try {
                    String sku = productVariantRequest.getProductVariantSku();
                    BigDecimal quantity = productVariantRequest.getQuantity();
                    LocalDateTime expectedCompleteDate = productVariantRequest.getExpectedCompleteDate();
                    List<BomItemProductionTicketRequest> boms = productVariantRequest.getBoms();

                    // Get ProductVariant
                    ProductVariant variant = productVariantRepository.findBySku(sku)
                        .orElseThrow(() -> new RuntimeException("Product Variant not found with SKU: " + sku));

                    // Check if ProductInventory exists, if not we'll create it after ticket creation
                    ProductInventory inventory = productInventoryService.getByProductVariantId(variant.getId());
                    boolean needToCreateInventory = (inventory == null);

                    // Create ProductionTicketDetail
                    ProductionTicketDetail detail = new ProductionTicketDetail();
                    detail.setProductVariant(variant);
                    detail.setQuantity(quantity);
                    detail.setStatus(ProductionTicketDetailStatus.NEW);
                    detail.setExpected_complete_date(expectedCompleteDate);
                    detail.setCompleted_date(null);
                    detail.setCreatedAt(LocalDateTime.now());
                    detail.setUpdatedAt(LocalDateTime.now());
                    detail.setCreatedBy(user);
                    detail.setUpdatedBy(user);
                    detail.setProductionTicket(ticket);

                    // Add BOM items if provided
                    if (boms != null && !boms.isEmpty()) {
                        for (BomItemProductionTicketRequest bomRequest : boms) {
                            MaterialVariant materialVariant = materialVariantRepository.findBySku(bomRequest.getMaterialVariantSku())
                                .orElseThrow(() -> new RuntimeException("Material Variant not found with SKU: " + bomRequest.getMaterialVariantSku()));
                            
                            if (!materialVariant.isActive()) {
                                throw new RuntimeException("Material Variant is not active");
                            }

                            BomItemProductionTicketDetail bomItem = new BomItemProductionTicketDetail();
                            bomItem.setMaterialVariant(materialVariant);
                            bomItem.setPlannedQuantity(bomRequest.getPlannedQuantity());
                            bomItem.setActualQuantity(bomRequest.getActualQuantity());
                            bomItem.setProductionTicketDetail(detail);
                            detail.getBomItem().add(bomItem);
                        }
                    }

                    // Add detail to ticket
                    ticket.getTicketDetail().add(detail);

                    // If we need to create ProductInventory, do it now
                    if (needToCreateInventory) {
                        createDefaultProductInventory(variant, user, ticket.getId());
                    }

                    totalCreated++;

                } catch (Exception e) {
                    String error = "Failed to create production ticket detail for SKU " + 
                                 productVariantRequest.getProductVariantSku() + ": " + e.getMessage();
                    errors.add(error);
                    totalFailed++;
                }
            }

            // Save the ticket with all details
            ticket = productionTicketRepository.save(ticket);

            // Create initial status logs for the ticket and all details
            createInitialTicketStatusLog(ticket, user); // Only one ticket status log
            for (ProductionTicketDetail detail : ticket.getTicketDetail()) {
                createInitialDetailStatusLog(detail, user); // One log per detail
            }

            // Create response for each detail
            for (ProductionTicketDetail detail : ticket.getTicketDetail()) {
                CreateProductionTicketResponse response = new CreateProductionTicketResponse(
                    ticket.getId(),
                    ticket.getName(),
                    detail.getProductVariant().getSku(),
                    detail.getProductVariant().getId(),
                    detail.getQuantity(),
                    detail.getStatus().getDisplayName(),
                    detail.getExpected_complete_date(),
                    detail.getCompleted_date(),
                    detail.getCreatedAt(),
                    detail.getCreatedBy().getUsername(),
                    detail.getCreatedBy().getRole().toString()
                );
                createdTickets.add(response);
            }

        } catch (Exception e) {
            String error = "Failed to create bulk production ticket: " + e.getMessage();
            errors.add(error);
            totalFailed = totalRequested;
        }

        // Determine overall success
        boolean overallSuccess = totalFailed == 0;
        String message = overallSuccess ? 
            "Successfully created production ticket with " + totalCreated + " details" :
            "Created production ticket with " + totalCreated + " details, " + totalFailed + " failed";

        return new BulkCreateProductionTicketResponse(
            overallSuccess, message, createdTickets, errors, 
            totalRequested, totalCreated, totalFailed
        );
    }

    private void createInitialTicketStatusLog(ProductionTicket ticket, User user) {
        // Create initial ProductionTicket status log (only once per ticket)
        ProductionTicketStatusLog ticketStatusLog = new ProductionTicketStatusLog();
        ticketStatusLog.setProductionTicket(ticket);
        ticketStatusLog.setOld_status(null); // No previous status for new ticket
        ticketStatusLog.setNew_status(ProductionTicketStatus.NEW);
        ticketStatusLog.setNote("Production ticket created");
        ticketStatusLog.setUpdatedAt(LocalDateTime.now());
        ticketStatusLog.setUpdatedBy(user);
        productionTicketStatusLogRepository.save(ticketStatusLog);
    }

    private void createInitialDetailStatusLog(ProductionTicketDetail detail, User user) {
        // Create initial ProductionTicketDetail status log (one per detail)
        ProductionTicketDetailStatusLog detailStatusLog = new ProductionTicketDetailStatusLog();
        detailStatusLog.setProductionTicketDetail(detail);
        detailStatusLog.setOld_status(null); // No previous status for new detail
        detailStatusLog.setNew_status(ProductionTicketDetailStatus.NEW);
        detailStatusLog.setNote("Production ticket detail created");
        detailStatusLog.setUpdatedAt(LocalDateTime.now());
        detailStatusLog.setUpdatedBy(user);
        productionTicketDetailStatusLogRepository.save(detailStatusLog);
    }

    @Override
    public ProductionTicketResponse getProductionTicketById(Long id) {
        ProductionTicket ticket = productionTicketRepository.findByIdAndIsActive(id, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket not found with id: " + id));

        List<ProductTicketDetailShortResponse> response = new ArrayList<>();

        if (ticket.getTicketDetail() != null && !ticket.getTicketDetail().isEmpty()) {
            response = ticket.getTicketDetail().stream()
                .map(detail -> new ProductTicketDetailShortResponse(
                        detail.getId(),
                        detail.getProductVariant().getSku(),
                        detail.getQuantity(),
                        detail.getExpected_complete_date(),
                        detail.getCompleted_date(),
                        detail.getStatus().getDisplayName(),
                        detail.getCreatedAt(),
                        detail.getUpdatedAt(),
                        detail.getCreatedBy().getUsername(),
                        detail.getCreatedBy().getRole().name(),
                        detail.getUpdatedBy().getUsername(),
                        detail.getUpdatedBy().getRole().name()
                    )
                )
                .collect(Collectors.toList());
        }

        return new ProductionTicketResponse(
            ticket.getId(),
            ticket.getName(),
            ticket.getCompleted_date(),
            ticket.getStatus().getDisplayName(),
            ticket.getCreatedAt(),
            ticket.getCreatedBy().getUsername(),
            ticket.getCreatedBy().getRole().name(),
            ticket.getUpdatedAt(),
            ticket.getUpdatedBy().getUsername(),
            ticket.getUpdatedBy().getRole().name(),
            response
        );
    }


    @Override
    public Page<ProductionTicketCardResponse> getProductionTickets(String search, List<String> ticketStatus,Pageable pageable) {
        // handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // convert status from String -> Enum
        List<ProductionTicketStatus> processedStatuses = null;
        if (!CollectionUtils.isEmpty(ticketStatus)) {
            processedStatuses = ticketStatus.stream()
                .map(s -> {
                    try {
                        return ProductionTicketStatus.valueOf(s.toUpperCase());
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

        Page<ProductionTicket> response = productionTicketRepository.findAllActiveByCriteria(processedSearch,processedStatuses,pageable);
        return response.map(ProductionTicketCardResponse::new);
    }

    @Override
    public List<ProductionTicketCardResponse> getAutoComplete(String search){
        // Handle null or empty search
        if (search == null || search.trim().isEmpty()) {
            // Return empty list for null/empty search to avoid showing all tickets
            return new ArrayList<>();
        }
        
        String processedSearch = search.trim();
        
        try {
            List<ProductionTicket> tickets = productionTicketRepository.findAllBySearch(processedSearch);
            return tickets.stream()
                .map(ProductionTicketCardResponse::new)
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error in autocomplete search: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /*
     * To return the detailed info of a production ticket detail
     */
    @Override
    public ProductionTicketDetailResponse getProductionTicketDetails(Long id, Long detailId) {
        // First verify the production ticket exists
        ProductionTicket ticket = productionTicketRepository.findByIdAndIsActive(id, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket not found with id: " + id));
        
        // Find the specific detail
        ProductionTicketDetail detail = productionTicketDetailRepository.findByIdAndIsActive(detailId, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket Detail not found with id: " + detailId));
        
        // Verify the detail belongs to the ticket
        if (!detail.getProductionTicket().getId().equals(id)) {
            throw new RuntimeException("Production Ticket Detail does not belong to the specified Production Ticket");
        }
        
        // Get BOM items for this detail
        List<BomItemProductionTicketResponse> bomList = new ArrayList<>();
        if (detail.getBomItem() != null && !detail.getBomItem().isEmpty()) {
            bomList = detail.getBomItem().stream()
                .map(bom -> new BomItemProductionTicketResponse(
                    bom.getId(),
                    bom.getMaterialVariant().getSku(),
                    bom.getActualQuantity(),
                    bom.getPlannedQuantity()))
                .collect(Collectors.toList());
        }
        
        return new ProductionTicketDetailResponse(
            detail.getId(),
            detail.getProductVariant().getSku(),
            detail.getQuantity(),
            bomList,
            detail.getStatus().getDisplayName(),
            detail.getExpected_complete_date(),
            detail.getCompleted_date(),
            detail.getCreatedAt(),
            detail.getUpdatedAt(),
            detail.getCreatedBy().getUsername(),
            detail.getCreatedBy().getRole().name(),
            detail.getUpdatedBy().getUsername(),
            detail.getUpdatedBy().getRole().name()
        );
    }

    // Test method to verify cascade relationships
    public boolean testCascadeRelationships(Long ticketId) {
        try {
            ProductionTicket ticket = productionTicketRepository.findById(ticketId).orElse(null);
            if (ticket == null) {
                return false;
            }
            
            // Check if ticket has details
            if (ticket.getTicketDetail().isEmpty()) {
                return false;
            }
            
            ProductionTicketDetail detail = ticket.getTicketDetail().get(0);
            
            // Check if detail has bom items
            if (detail.getBomItem().isEmpty()) {
                return false;
            }
            
            // Check if relationships are properly established
            for (BomItemProductionTicketDetail bomItem : detail.getBomItem()) {
                if (bomItem.getProductionTicketDetail() == null || bomItem.getMaterialVariant() == null) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<StatusTransitionRule> getStatusTransitionRules() {
        return List.of(
            new StatusTransitionRule(
                ProductionTicketDetailStatus.NEW,
                List.of(ProductionTicketDetailStatus.APPROVAL, ProductionTicketDetailStatus.CANCELLED),
                "New tickets can be approved or cancelled"
            ),
            new StatusTransitionRule(
                ProductionTicketDetailStatus.APPROVAL,
                List.of(ProductionTicketDetailStatus.COMPLETE, ProductionTicketDetailStatus.CANCELLED),
                "Approved tickets can be completed or cancelled"
            ),
            new StatusTransitionRule(
                ProductionTicketDetailStatus.COMPLETE,
                List.of(ProductionTicketDetailStatus.READY, ProductionTicketDetailStatus.CANCELLED),
                "Completed tickets can be made ready or cancelled"
            ),
            new StatusTransitionRule(
                ProductionTicketDetailStatus.READY,
                List.of(ProductionTicketDetailStatus.CLOSED),
                "Ready tickets can only be closed"
            ),
            new StatusTransitionRule(
                ProductionTicketDetailStatus.CLOSED,
                List.of(),
                "Closed tickets cannot be changed"
            ),
            new StatusTransitionRule(
                ProductionTicketDetailStatus.CANCELLED,
                List.of(),
                "Cancelled tickets cannot be changed"
            )
        );
    }

    /*
     * To update the status of each production ticket detail
     * User only can change the status of a production ticket detail
     * User only can cancel the production ticket 
     * The status of a production ticket is change automatically by the list of belonging production
     * ticket detail status
     */
    @Override
    public ProductionTicketDetailResponse updateDetailStatus(Long ticketId, Long detailId, UpdateDetailStatusRequest request, User user) {
        // Verify the production ticket exists
        ProductionTicket ticket = productionTicketRepository.findByIdAndIsActive(ticketId, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket not found with id: " + ticketId));
        
        // Find the specific detail
        ProductionTicketDetail detail = productionTicketDetailRepository.findByIdAndIsActive(detailId, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket Detail not found with id: " + detailId));
        
        // Verify the detail belongs to the ticket
        if (!detail.getProductionTicket().getId().equals(ticketId)) {
            throw new RuntimeException("Production Ticket Detail does not belong to the specified Production Ticket");
        }
        
        // call helper method to validate status transition
        // if break the rule will throw an exception
        validateStatusTransition(detail.getStatus(), request.getNewStatus());
        
        // Update detail status
        ProductionTicketDetailStatus oldStatus = detail.getStatus();
        detail.setStatus(request.getNewStatus());
        detail.setUpdatedAt(LocalDateTime.now());
        detail.setUpdatedBy(user);
        
        // Save the detail
        detail = productionTicketDetailRepository.save(detail);
        
        // Log the status change
        logDetailStatusChange(detail, oldStatus, request.getNewStatus(), request.getNote(), user);
        
        // Update inventory based on status change
        updateProductInventory(detail, oldStatus, request.getNewStatus(), user);
        
        // Update production ticket status based on all details
        updateProductionTicketStatusBasedOnDetails(ticket, user);
        
        // Return updated detail response
        return getProductionTicketDetails(ticketId, detailId);
    }

    @Override
    public ProductionTicket cancelProductionTicket(Long ticketId, String reason, User user) {
        ProductionTicket ticket = productionTicketRepository.findByIdAndIsActive(ticketId, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket not found with id: " + ticketId));
        
        // Store old status for logging
        ProductionTicketStatus oldTicketStatus = ticket.getStatus();
        
        // Cancel all details
        for (ProductionTicketDetail detail : ticket.getTicketDetail()) {
            if (detail.getStatus() != ProductionTicketDetailStatus.CANCELLED) {
                // Store old status for logging
                ProductionTicketDetailStatus oldDetailStatus = detail.getStatus();
                
                detail.setStatus(ProductionTicketDetailStatus.CANCELLED);
                detail.setUpdatedAt(LocalDateTime.now());
                detail.setUpdatedBy(user);
                productionTicketDetailRepository.save(detail);
                
                // Update inventory based on status change to CANCELLED
                updateProductInventory(detail, oldDetailStatus, ProductionTicketDetailStatus.CANCELLED, user);
                
                // Log the detail status change
                logDetailStatusChange(detail, oldDetailStatus, ProductionTicketDetailStatus.CANCELLED, 
                    "Cancelled due to production ticket cancellation: " + reason, user);
            }
        }
        
        // Set ticket status to cancelled
        ticket.setStatus(ProductionTicketStatus.CANCELLED);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setUpdatedBy(user);
        
        ticket = productionTicketRepository.save(ticket);
        
        // Log the ticket status change
        logTicketStatusChange(ticket, oldTicketStatus, ProductionTicketStatus.CANCELLED, 
            "Production ticket cancelled: " + reason, user);
        
        return ticket;
    }

    @Override
    public ProductionTicketDetailResponse cancelProductionTicketDetail(Long ticketId, Long detailId, String reason, User user) {
        // Verify the production ticket exists
        ProductionTicket ticket = productionTicketRepository.findByIdAndIsActive(ticketId, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket not found with id: " + ticketId));
        
        // Find the specific detail
        ProductionTicketDetail detail = productionTicketDetailRepository.findByIdAndIsActive(detailId, true)
            .orElseThrow(() -> new RuntimeException("Production Ticket Detail not found with id: " + detailId));
        
        // Verify the detail belongs to the ticket
        if (!detail.getProductionTicket().getId().equals(ticketId)) {
            throw new RuntimeException("Production Ticket Detail does not belong to the specified Production Ticket");
        }
        
        // Store old status for logging
        ProductionTicketDetailStatus oldDetailStatus = detail.getStatus();
        
        // Cancel the detail
        detail.setStatus(ProductionTicketDetailStatus.CANCELLED);
        detail.setUpdatedAt(LocalDateTime.now());
        detail.setUpdatedBy(user);
        
        // Save the detail
        detail = productionTicketDetailRepository.save(detail);
        
        // Update inventory based on status change to CANCELLED
        updateProductInventory(detail, oldDetailStatus, ProductionTicketDetailStatus.CANCELLED, user);
        
        // Log the detail status change
        logDetailStatusChange(detail, oldDetailStatus, ProductionTicketDetailStatus.CANCELLED, 
            "Detail cancelled: " + reason, user);
        
        // Update production ticket status based on all details
        updateProductionTicketStatusBasedOnDetails(ticket, user);
        
        // Return updated detail response
        return getProductionTicketDetails(ticketId, detailId);
    }

    // helper method to validate the status change 
    private void validateStatusTransition(ProductionTicketDetailStatus currentStatus, ProductionTicketDetailStatus newStatus) {
        List<StatusTransitionRule> rules = getStatusTransitionRules();
        
        StatusTransitionRule currentRule = rules.stream()
            .filter(rule -> rule.getCurrentStatus() == currentStatus)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Invalid current status: " + currentStatus));
        
        if (!currentRule.getAllowedTransitions().contains(newStatus)) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    // helper method to change the production ticket status because of the change of production ticket detail
    private void updateProductionTicketStatusBasedOnDetails(ProductionTicket ticket, User user) {
        List<ProductionTicketDetail> details = ticket.getTicketDetail();
        
        if (details.isEmpty()) {
            return;
        }
        
        // Count statuses
        long totalDetails = details.size();
        long approvedCount = details.stream()
            .filter(d -> d.getStatus() == ProductionTicketDetailStatus.APPROVAL)
            .count();
        long completeCount = details.stream()
            .filter(d -> d.getStatus() == ProductionTicketDetailStatus.COMPLETE)
            .count();
        long cancelledCount = details.stream()
            .filter(d -> d.getStatus() == ProductionTicketDetailStatus.CANCELLED)
            .count();
        
        ProductionTicketStatus oldStatus = ticket.getStatus();
        ProductionTicketStatus newTicketStatus = ticket.getStatus();
        
        // Determine new ticket status based on detail statuses
        if (cancelledCount == totalDetails) {
            // All details cancelled
            newTicketStatus = ProductionTicketStatus.CANCELLED;
        } else if (cancelledCount > 0) {
            // Some details cancelled
            newTicketStatus = ProductionTicketStatus.PARTIAL_COMPLETE; // Assuming this exists, otherwise use appropriate status
        } else if (completeCount == totalDetails) {
            // All details complete
            newTicketStatus = ProductionTicketStatus.COMPLETE;
        } else if (completeCount > 0) {
            // Some details complete
            newTicketStatus = ProductionTicketStatus.PARTIAL_COMPLETE;
        } else if (approvedCount > 0) {
            // Some details approved
            newTicketStatus = ProductionTicketStatus.PARTIAL_COMPLETE; // In Progress equivalent
        } else {
            // All details new
            newTicketStatus = ProductionTicketStatus.NEW;
        }
        
        // Update ticket status if changed
        if (ticket.getStatus() != newTicketStatus) {
            ticket.setStatus(newTicketStatus);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setUpdatedBy(user);
            productionTicketRepository.save(ticket);
            
            // Log the ticket status change
            logTicketStatusChange(ticket, oldStatus, newTicketStatus, "Status updated based on detail changes", user);
        }
    }

    private void logTicketStatusChange(ProductionTicket ticket, ProductionTicketStatus oldStatus, 
                                     ProductionTicketStatus newStatus, String note, User user) {
        ProductionTicketStatusLog statusLog = new ProductionTicketStatusLog();
        statusLog.setProductionTicket(ticket);
        statusLog.setOld_status(oldStatus);
        statusLog.setNew_status(newStatus);
        statusLog.setNote(note);
        statusLog.setUpdatedAt(LocalDateTime.now());
        statusLog.setUpdatedBy(user);
        productionTicketStatusLogRepository.save(statusLog);
    }

    private void logDetailStatusChange(ProductionTicketDetail detail, ProductionTicketDetailStatus oldStatus,
                                     ProductionTicketDetailStatus newStatus, String note, User user) {
        ProductionTicketDetailStatusLog statusLog = new ProductionTicketDetailStatusLog();
        statusLog.setProductionTicketDetail(detail);
        statusLog.setOld_status(oldStatus);
        statusLog.setNew_status(newStatus);
        statusLog.setNote(note);
        statusLog.setUpdatedAt(LocalDateTime.now());
        statusLog.setUpdatedBy(user);
        productionTicketDetailStatusLogRepository.save(statusLog);
    }

    private void updateProductInventory(ProductionTicketDetail detail, ProductionTicketDetailStatus oldStatus, ProductionTicketDetailStatus newStatus, User user) {
        ProductVariant productVariant = detail.getProductVariant();
        if (productVariant == null) {
            return;
        }

        BigDecimal quantity = detail.getQuantity();
        Long variantId = productVariant.getId();
        Long ticketId = detail.getProductionTicket().getId();

        // ProductInventory is guaranteed to exist at this point since it's created during ticket creation
        if (oldStatus == ProductionTicketDetailStatus.NEW && newStatus == ProductionTicketDetailStatus.APPROVAL) {
            // When status changes from NEW to APPROVAL, add to futureStock
            // This indicates the product is planned for production
            productInventoryService.addToFutureStock(variantId, quantity, user, ticketId);
            
        } else if (oldStatus == ProductionTicketDetailStatus.APPROVAL && newStatus == ProductionTicketDetailStatus.READY) {
            // When status changes from APPROVAL to READY, move from futureStock to currentStock
            // This indicates the product is now ready and available
            productInventoryService.moveFromFutureToCurrentStock(variantId, quantity, user, ticketId);
            
        } else if (oldStatus == ProductionTicketDetailStatus.APPROVAL && newStatus == ProductionTicketDetailStatus.CANCELLED) {
            // When status changes from APPROVAL to CANCELLED, remove from futureStock
            // This indicates the production is cancelled
            productInventoryService.removeFromFutureStock(variantId, quantity, user, ticketId);
            
        } else if (oldStatus == ProductionTicketDetailStatus.READY && newStatus == ProductionTicketDetailStatus.CANCELLED) {
            // When status changes from READY to CANCELLED, remove from currentStock
            // This indicates the ready product is cancelled
            productInventoryService.removeFromCurrentStock(variantId, quantity, user, ticketId);
        }
    }

    private ProductInventory createDefaultProductInventory(ProductVariant productVariant, User user, Long productionTicketId) {
        // Create initial log for default inventory creation
        ProductInventoryLog initialLog = new ProductInventoryLog(
            LocalDateTime.now(),
            null, // Will be set after inventory is created
            InventoryTransactionType.SET,
            ProductInventoryTransactionSourceType.SET_INVENTORY,
            BigDecimal.ZERO, // quantity = 0
            BigDecimal.ZERO, // beforeQuantity = 0
            BigDecimal.ZERO, // afterQuantity = 0
            "Default ProductInventory created for Production Ticket", // note
            ProductInventoryReferenceDocumentType.PRODUCTION_TICKET, // referenceDocumentType
            productionTicketId, // referenceDocumentId - ID of the production ticket
            user
        );

        List<ProductInventoryLog> logs = new ArrayList<>();
        logs.add(initialLog);

        ProductInventory defaultInventory = new ProductInventory(
            productVariant,
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
        defaultInventory = productInventoryRepository.save(defaultInventory);
        
        // Update the log with the saved inventory reference
        initialLog.setProductInventory(defaultInventory);
        productInventoryLogRepository.save(initialLog);
        
        return defaultInventory;
    }

    @Override
    public ProductionTicketStatusesResponse getAllProductionTicketStatuses() {
        List<ProductionTicketStatusesResponse.StatusInfo> productionTicketStatuses = new ArrayList<>();
        List<ProductionTicketStatusesResponse.StatusInfo> productionTicketDetailStatuses = new ArrayList<>();

        // Add ProductionTicketStatus values
        for (ProductionTicketStatus status : ProductionTicketStatus.values()) {
            productionTicketStatuses.add(new ProductionTicketStatusesResponse.StatusInfo(
                status.name(),
                status.getDisplayName(),
                getProductionTicketStatusDescription(status)
            ));
        }

        // Add ProductionTicketDetailStatus values
        for (ProductionTicketDetailStatus status : ProductionTicketDetailStatus.values()) {
            productionTicketDetailStatuses.add(new ProductionTicketStatusesResponse.StatusInfo(
                status.name(),
                status.getDisplayName(),
                getProductionTicketDetailStatusDescription(status)
            ));
        }

        return new ProductionTicketStatusesResponse(productionTicketStatuses, productionTicketDetailStatuses);
    }

    private String getProductionTicketStatusDescription(ProductionTicketStatus status) {
        switch (status) {
            case NEW:
                return "Production ticket is newly created and waiting for approval";
            case PARTIAL_COMPLETE:
                return "Some production ticket details are in progress or completed";
            case COMPLETE:
                return "All production ticket details are completed";
            case CANCELLED:
                return "Production ticket has been cancelled";
            default:
                return "Unknown production ticket status";
        }
    }

    private String getProductionTicketDetailStatusDescription(ProductionTicketDetailStatus status) {
        switch (status) {
            case NEW:
                return "Production detail is newly created and waiting for approval";
            case APPROVAL:
                return "Production detail is approved and in progress";
            case COMPLETE:
                return "Production detail is completed";
            case READY:
                return "Production detail is ready for shipment";
            case CLOSED:
                return "Production detail is closed and finalized";
            case CANCELLED:
                return "Production detail has been cancelled";
            default:
                return "Unknown production detail status";
        }
    }
}
    

