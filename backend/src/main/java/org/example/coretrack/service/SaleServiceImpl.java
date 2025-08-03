package org.example.coretrack.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.example.coretrack.dto.productionTicket.BomItemProductionTicketRequest;
import org.example.coretrack.dto.productionTicket.ProductVariantBomRequest;
import org.example.coretrack.dto.productionTicket.ProductionTicketCardResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketDetailShortResponse;
import org.example.coretrack.dto.productionTicket.ProductionTicketResponse;
import org.example.coretrack.dto.sale.SaleCardResponse;
import org.example.coretrack.dto.sale.SaleCreateDetailRequest;
import org.example.coretrack.dto.sale.SaleCreateRequest;
import org.example.coretrack.dto.sale.SaleDetailResponse;
import org.example.coretrack.dto.sale.SaleOrderStatusLogResponse;
import org.example.coretrack.dto.sale.SaleStatusTransitionRule;
import org.example.coretrack.dto.sale.SaleTicketResponse;
import org.example.coretrack.dto.sale.UpdateSaleOrderStatusRequest;
import org.example.coretrack.model.Sale.Order;
import org.example.coretrack.model.Sale.OrderDetail;
import org.example.coretrack.model.Sale.OrderStatus;
import org.example.coretrack.model.Sale.OrderStatusLog;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.ProductVariant;
import org.example.coretrack.model.product.inventory.InventoryStatus;
import org.example.coretrack.model.product.inventory.InventoryTransactionType;
import org.example.coretrack.model.product.inventory.ProductInventory;
import org.example.coretrack.model.product.inventory.ProductInventoryLog;
import org.example.coretrack.model.product.inventory.ProductInventoryReferenceDocumentType;
import org.example.coretrack.model.product.inventory.ProductInventoryTransactionSourceType;
import org.example.coretrack.model.product.inventory.StockType;
import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketStatus;
import org.example.coretrack.repository.OrderDetailRepository;
import org.example.coretrack.repository.OrdersRepository;
import org.example.coretrack.repository.OrdersStatusLogRepository;
import org.example.coretrack.repository.ProductInventoryLogRepository;
import org.example.coretrack.repository.ProductInventoryRepository;
import org.example.coretrack.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class SaleServiceImpl implements SaleService{
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrdersStatusLogRepository ordersStatusLogRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private ProductInventoryLogRepository productInventoryLogRepository;

    @Override
    public SaleCardResponse createSaleTicket(SaleCreateRequest request, User user) {
        if (CollectionUtils.isEmpty(request.getDetails())) {
            throw new RuntimeException("At least one product Variant is required");
        }

        // Validate request details
        for (SaleCreateDetailRequest detail : request.getDetails()) {
            if (detail.getQuantity() == null || detail.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for all products");
            }
            if (!StringUtils.hasText(detail.getProductVariantSku())) {
                throw new IllegalArgumentException("Product variant SKU is required for all details");
            }
        }

        Order ticket = new Order();

        // Handle SKU 
        String orderSku = "";
        if (StringUtils.hasText(request.getSku())) {
            String manualSku = request.getSku().trim();
            // Validate SKU uniqueness
            if (ordersRepository.findBySku(manualSku).isPresent()) {
                throw new IllegalArgumentException("Order SKU already exists: " + manualSku);
            }
            // Validate SKU format - length (8-16 chars for tidy SKU)
            if (manualSku.length() < 8 || manualSku.length() > 16) {
                 throw new IllegalArgumentException("Manual SKU must be between 8 and 16 characters.");
            }
            orderSku = manualSku;
        } else {
            // Automatic SKU Generation
            orderSku = generateUniqueOrderSku(); 
        }

        // Handle expected_complete_date
        LocalDateTime expectedCompleteDateTime = null;
        if (request.getExpected_complete_date() != null) {
            LocalDate expectedCompleteDate = request.getExpected_complete_date();
            expectedCompleteDateTime = expectedCompleteDate.atStartOfDay();
        }

        // Set ticket properties
        ticket.setSku(orderSku);
        ticket.setTotal(request.getTotal());
        ticket.setPromotion(request.getPromotion());
        ticket.setNetTotal(request.getNetTotal());
        ticket.setCompleted_date(null);
        ticket.setExpected_complete_date(expectedCompleteDateTime);
        ticket.setStatus(OrderStatus.NEW);
        ticket.setActive(true);
        ticket.setCreatedBy(user);
        ticket.setUpdatedBy(user);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        // Initialize order details list
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        ticket.setOrderDetail(new ArrayList<>()); // Initialize the list

        // Process each product variant to create OrderDetails
        for (int i = 0; i < request.getDetails().size(); i++) {
            SaleCreateDetailRequest detailRequest = request.getDetails().get(i);
            System.out.println("=== Processing productVariant " + (i + 1) + " of " + request.getDetails().size() + " ===");
            
            try {
                String productVariantSku = detailRequest.getProductVariantSku();
                BigDecimal quantity = detailRequest.getQuantity();

                // Get ProductVariant
                System.out.println("Looking for ProductVariant with SKU: " + productVariantSku);
                ProductVariant variant = productVariantRepository.findBySku(productVariantSku)
                    .orElseThrow(() -> new RuntimeException("Product Variant not found with SKU: " + productVariantSku));
                System.out.println("Found ProductVariant: " + variant.getName() + " (ID: " + variant.getId() + ")");

                // Check if ProductInventory exists
                ProductInventory inventory = productInventoryService.getByProductVariantId(variant.getId());
                boolean needToCreateInventory = (inventory == null);
                System.out.println("ProductInventory exists: " + (inventory != null) + ", needToCreateInventory: " + needToCreateInventory);

                // Create OrderDetail
                String detailSku = generateVariantSku(orderSku, (i + 1));
                OrderDetail detail = new OrderDetail();
                detail.setSku(detailSku);
                detail.setProductVariant(variant);
                detail.setQuantity(quantity);
                detail.setOrder(ticket);
                detail.setStatus(OrderStatus.NEW);
                detail.setActive(true);
                
                orderDetails.add(detail);
                ticket.getOrderDetail().add(detail); // Add to ticket's detail list
                System.out.println("Successfully created detail for product variant: " + productVariantSku);

            } catch (Exception e) {
                String error = "Failed to create detail for product variant " + detailRequest.getProductVariantSku() + ": " + e.getMessage();
                errors.add(error);
                System.out.println("Error creating detail: " + error);
            }
        }

        // If there are errors, throw exception
        if (!errors.isEmpty()) {
            throw new RuntimeException("Failed to create sale ticket: " + String.join("; ", errors));
        }

        // Save ticket first
        ticket = ordersRepository.save(ticket);
        System.out.println("Saved order ticket with ID: " + ticket.getId());

        // Create initial status log for ticket
        createInitialTicketStatusLog(ticket, user);
        System.out.println("Created initial ticket status log");

        // Process inventory creation and allocation after ticket is saved
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);
            ProductVariant variant = detail.getProductVariant();
            
            try {
                // Check if ProductInventory exists, create if needed
                ProductInventory inventory = productInventoryService.getByProductVariantId(variant.getId());
                if (inventory == null) {
                    System.out.println("Creating default product inventory for SKU: " + variant.getSku());
                    createDefaultProductInventory(variant, user, ticket.getId());
                    System.out.println("Created product inventory for SKU: " + variant.getSku());
                } else {
                    // Try to allocate stock if not already allocated
                    if (detail.getStatus() == OrderStatus.ALLOCATED) {
                        productInventoryService.addToAllocatedStock(variant.getId(), detail.getQuantity(), user, ticket.getId());
                        System.out.println("Allocated stock for product: " + variant.getSku());
                    }
                }
            } catch (Exception e) {
                System.out.println("Warning: Failed to process inventory for product " + variant.getSku() + ": " + e.getMessage());
                // Don't fail the entire operation, just log the warning
            }
        }

        // Convert to response
        return convertToSaleCardResponse(ticket);
    }

    /**
     * Convert Order to SaleCardResponse
     */
    private SaleCardResponse convertToSaleCardResponse(Order order) {
        SaleCardResponse response = new SaleCardResponse();
        response.setId(order.getId());
        response.setSku(order.getSku());
        response.setCreatedAt(order.getCreatedAt());
        response.setStatus(order.getStatus().name());
        
        // Set detail number
        int detailNumber = order.getOrderDetail() != null ? order.getOrderDetail().size() : 0;
        response.setDetailNumber(detailNumber);
        
        // Set customer information (if available)
        // Note: Order model might not have customer fields, so we'll set them to null for now
        response.setCustomerName(null);
        response.setCustomerEmail(null);
        response.setCustomerPhone(null);
        response.setCustomerAddress(null);
        
        return response;
    }

    /*
     * Helper method to generate Sku
     */

    private String generateUniqueOrderSku() {
        String newSku;
        Random random = new Random(); // Initialize Random
        do {
            // Generate a 7-digit random number to keep within 16 char limit
            // ORD-1234567 = 11 chars (max 16)
            int randomNumber = 1000000 + random.nextInt(9000000); // Generates number between 1,000,000 and 9,999,999
            newSku = "ORD-" + randomNumber; // Example: ORD-1234567 (11 chars)
        } while (ordersRepository.findBySku(newSku).isPresent());
        return newSku;
    }

    /*
     * Helper method to generate SKU variant
     */

    private String generateVariantSku(String baseOrderSku, int variantIndex) {
        // Ensure SKU doesn't exceed 16 characters
        // Base format: ORD-1234567-1 (max 16 chars for Order detail)
        String suffix = "-" + variantIndex;
        String variantSku = baseOrderSku + suffix;

        // Ensure uniqueness for variant SKU as well
        String finalVariantSku;
        int attempt = 0;
        Random random = new Random(); // Initialize Random for unique part
        do {
            finalVariantSku = variantSku;
            if (attempt > 0) { // Add a unique identifier if initial SKU is not unique
                // Generate a 4-digit random number to keep within 16 char limit
                String uniquePart = "-" + (1000 + random.nextInt(9000)); // Generates number between 1000 and 9999
                finalVariantSku = variantSku + uniquePart;
                
                // If still too long, truncate the base SKU
                if (finalVariantSku.length() > 16) {
                    int maxBaseLength = 16 - uniquePart.length() - suffix.length();
                    String truncatedBase = baseOrderSku.substring(0, Math.min(maxBaseLength, baseOrderSku.length()));
                    finalVariantSku = truncatedBase + suffix + uniquePart;
                }
            }
            attempt++;
        } while (orderDetailRepository.findBySku(finalVariantSku).isPresent());

        return finalVariantSku;
    }
    /*
     * Helper method to create default inventory
     */
    private ProductInventory createDefaultProductInventory(ProductVariant productVariant, User user, Long productionTicketId) {
        // Create ProductInventory first without logs
        ProductInventory defaultInventory = new ProductInventory(
            productVariant,
            BigDecimal.ZERO,  // currentStock = 0
            BigDecimal.ZERO,  // minAlertStock = 0
            BigDecimal.valueOf(100),  // maxStockLevel = 100
            new ArrayList<>(),  // empty logs list initially
            InventoryStatus.OUT_OF_STOCK,  // default status
            user
        );
        
        // Set default values
        defaultInventory.setFutureStock(BigDecimal.ZERO);
        defaultInventory.setAllocatedStock(BigDecimal.ZERO);
        
        // Save inventory first
        defaultInventory = productInventoryRepository.save(defaultInventory);
        
        // Now create the initial log with the saved inventory reference
        ProductInventoryLog initialLog = new ProductInventoryLog(
            LocalDateTime.now(),
            defaultInventory, // Now we have the saved inventory
            StockType.CURRENT,
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
        
        // Save the log
        productInventoryLogRepository.save(initialLog);
        
        // Add the log to the inventory's logs list
        defaultInventory.getLogs().add(initialLog);
        
        return defaultInventory;
    }

    /* 
     * Helper method to initial order status log
     */
    private void createInitialTicketStatusLog(Order ticket, User user) {
        try {
            System.out.println("Creating initial ticket status log for ticket ID: " + ticket.getId());
            
            OrderStatusLog log = new OrderStatusLog();
            log.setOrder(ticket);
            log.setNew_status(OrderStatus.NEW);
            log.setOld_status(OrderStatus.NEW); // Set to NEW instead of null
            log.setNote("Initial status log");
            log.setUpdatedBy(user);
            log.setUpdatedAt(LocalDateTime.now());
            
            ordersStatusLogRepository.save(log);
            System.out.println("Successfully created initial ticket status log");
            
        } catch (Exception e) {
            System.out.println("Error creating initial ticket status log: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to make the error visible
        }
    }

    @Override
    public SaleTicketResponse getSaleTicketById(Long id) {
        Order ticket = ordersRepository.findByIdAndIsActive(id, true)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        List<SaleDetailResponse> details = new ArrayList<>();
        if(ticket.getOrderDetail() != null && !ticket.getOrderDetail().isEmpty()){
            for(OrderDetail detail : ticket.getOrderDetail()){
                ProductInventory inventory = productInventoryRepository.findByProductVariant_Id(detail.getProductVariant().getId())
                                            .orElseThrow(() -> new RuntimeException("ProductVariant is not found by id " + detail.getProductVariant().getId()));
                BigDecimal currentStock = inventory.getCurrentStock();
                BigDecimal allocatedStock = inventory.getAllocatedStock();
                BigDecimal futureStock = inventory.getFutureStock();
                BigDecimal availableStock = (currentStock.subtract(allocatedStock).compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : currentStock.subtract(allocatedStock));
                SaleDetailResponse response = new SaleDetailResponse(
                            detail.getId(),
                            detail.getSku(),
                            detail.getProductVariant().getSku(),
                            detail.getProductVariant().getName(),
                            detail.getQuantity(),
                            detail.getProductVariant().getPrice() != null ? detail.getProductVariant().getPrice() : null,
                            detail.getProductVariant().getPrice() != null ? detail.getProductVariant().getPrice().multiply(detail.getQuantity()) : null,
                            detail.getStatus().getDisplayName(),
                            currentStock,
                            allocatedStock,
                            futureStock,
                            availableStock
                );
                details.add(response);
            }
        }
        List<SaleOrderStatusLogResponse> logs = 
            (ticket.getStatusLogs() != null && !ticket.getStatusLogs().isEmpty())
                ? ticket.getStatusLogs().stream()
                    .map(log -> new SaleOrderStatusLogResponse(
                            log.getId(),
                            log.getNew_status().getDisplayName(),
                            log.getOld_status().getDisplayName(),
                            log.getNote(),
                            log.getUpdatedAt(),
                            log.getUpdatedBy().getUsername(),
                            log.getUpdatedBy().getRole().name()
                        )
                    )
                    .collect(Collectors.toList())
                : new ArrayList<>();

        return new SaleTicketResponse(
            ticket.getId(),
            ticket.getSku(),
            ticket.getTotal(),
            ticket.getPromotion(),
            ticket.getNetTotal(),
            ticket.getExpected_complete_date(),
            ticket.getCompleted_date(),
            ticket.getStatus().getDisplayName(),
            ticket.isActive(),
            ticket.getCustomerName(),
            ticket.getCustomerEmail(),
            ticket.getCustomerPhone(),
            ticket.getCustomerAddress(),
            ticket.getCreatedAt(),
            ticket.getCreatedBy().getUsername(),
            ticket.getCreatedBy().getRole().name(),
            ticket.getUpdatedAt(),
            ticket.getUpdatedBy().getUsername(),
            ticket.getUpdatedBy().getRole().name(),
            logs,
            details
        );
    }

    @Override
    public Page<SaleCardResponse> getSaleTickets(String search, List<String> ticketStatus, Pageable pageable) {
        // Handle search
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // Convert status from String -> Enum
        List<OrderStatus> processedStatuses = null;
        if (!CollectionUtils.isEmpty(ticketStatus)) {
            processedStatuses = ticketStatus.stream()
                .map(s -> {
                    try {
                        return OrderStatus.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid order status: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (processedStatuses.isEmpty()) {
                processedStatuses = null;
            }
        }

        Page<Order> response = ordersRepository.findAllActiveByCriteria(processedSearch, processedStatuses, pageable);
        return response.map(this::convertToSaleCardResponse);
    }

    @Override
    public List<SaleCardResponse> getAutoComplete(String search) {
        // Handle null or empty search
        if (search == null || search.trim().isEmpty()) {
            // Return empty list for null/empty search to avoid showing all orders
            return new ArrayList<>();
        }
        
        String processedSearch = search.trim();
        
        try {
            List<Order> orders = ordersRepository.findAllBySearch(processedSearch);
            return orders.stream()
                .map(this::convertToSaleCardResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error in autocomplete search: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<SaleStatusTransitionRule> getStatusTransitionRules() {
        return List.of(
            new SaleStatusTransitionRule(
                OrderStatus.NEW,
                List.of(OrderStatus.ALLOCATED, OrderStatus.CANCELLED),
                "New orders can be allocated or cancelled"
            ),
            new SaleStatusTransitionRule(
                OrderStatus.ALLOCATED,
                List.of(OrderStatus.PACKED, OrderStatus.CANCELLED),
                "Allocated orders can be shipped or cancelled"
            ),
            new SaleStatusTransitionRule(
                OrderStatus.PACKED,
                List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
                "Shipping orders can be completed or cancelled"
            ),
            new SaleStatusTransitionRule(
                OrderStatus.SHIPPED,
                List.of(OrderStatus.DONE),
                "Completed orders cannot be changed"
            ),
            new SaleStatusTransitionRule(
                OrderStatus.DONE,
                List.of(),
                "Completed orders cannot be changed"
            ),
            new SaleStatusTransitionRule(
                OrderStatus.CANCELLED,
                List.of(),
                "Cancelled orders cannot be changed"
            )
        );
    }

    @Override
    public Order cancelSaleTicket(Long ticketId, String reason, User user) {
        System.out.println("=== Starting cancelSaleTicket ===");
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Reason: " + reason);
        System.out.println("User: " + user.getUsername());
        
        try {
            Order ticket = ordersRepository.findByIdAndIsActive(ticketId, true)
                .orElseThrow(() -> new RuntimeException("Sale Ticket not found with id: " + ticketId));
            
            System.out.println("Found ticket: " + ticket.getSku() + " (ID: " + ticket.getId() + ")");
            System.out.println("Current ticket status: " + ticket.getStatus());
            
            // Store old status for logging
            OrderStatus oldTicketStatus = ticket.getStatus();
            
            // Cancel all details
            System.out.println("Cancelling " + ticket.getOrderDetail().size() + " details...");
            for (OrderDetail detail : ticket.getOrderDetail()) {
                System.out.println("Processing detail ID: " + detail.getId() + ", current status: " + detail.getStatus());
                
                if (detail.getStatus() != OrderStatus.CANCELLED) {
                    // Store old status for logging
                    OrderStatus oldDetailStatus = detail.getStatus();
                    
                    detail.setStatus(OrderStatus.CANCELLED);
                    orderDetailRepository.save(detail);
                    
                    System.out.println("Cancelled detail ID: " + detail.getId());
                    
                    // Update inventory based on status change to CANCELLED
                    updateProductInventoryForSale(detail, oldDetailStatus, OrderStatus.CANCELLED, user);
                } else {
                    System.out.println("Detail ID: " + detail.getId() + " is already cancelled, skipping...");
                }
            }
            
            // Set ticket status to cancelled
            ticket.setStatus(OrderStatus.CANCELLED);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setUpdatedBy(user);
            
            ticket = ordersRepository.save(ticket);
            System.out.println("Updated ticket status to: " + ticket.getStatus());
            
            // Log the ticket status change
            logTicketStatusChange(ticket, oldTicketStatus, OrderStatus.CANCELLED, 
                "Sale ticket cancelled: " + reason, user);
            
            System.out.println("=== Successfully cancelled sale ticket ===");
            return ticket;
            
        } catch (Exception e) {
            System.err.println("=== ERROR in cancelSaleTicket ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            throw e;
        }
    }

    @Override
    public SaleTicketResponse updateDetailStatus(Long id, UpdateSaleOrderStatusRequest request, User user) {
        // Verify the sale ticket exists
        Order ticket = ordersRepository.findByIdAndIsActive(id, true)
            .orElseThrow(() -> new RuntimeException("Sale Ticket not found with id: " + id));
        
        // Call helper method to validate status transition
        // If break the rule will throw an exception
        validateSaleStatusTransition(ticket.getStatus(), request.getNewStatus());
        
        OrderStatus oldStatus = ticket.getStatus();
        
        // Update all order details to match the new order status
        for (OrderDetail detail : ticket.getOrderDetail()) {
            detail.setStatus(request.getNewStatus());
            orderDetailRepository.save(detail);
            updateProductInventoryForSale(detail, oldStatus, request.getNewStatus(), user);
        }
        
        // Update order status
        ticket.setStatus(request.getNewStatus());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setUpdatedBy(user);
        ordersRepository.save(ticket);
        
        // Log the ticket status change
        logTicketStatusChange(ticket, oldStatus, request.getNewStatus(), 
                "Order status updated: " + request.getNote(), user);
        
        // Return updated ticket response
        return getSaleTicketById(id);
    }

    @Override
    public SaleTicketResponse allocateOrderDetail(Long ticketId, Long detailId, User user) {
        // Verify the sale ticket exists
        Order ticket = ordersRepository.findByIdAndIsActive(ticketId, true)
            .orElseThrow(() -> new RuntimeException("Sale Ticket not found with id: " + ticketId));
        
        // Find the specific detail
        OrderDetail detail = orderDetailRepository.findByIdAndIsActive(detailId, true)
            .orElseThrow(() -> new RuntimeException("Sale Ticket Detail not found with id: " + detailId));
        
        // Verify the detail belongs to the ticket
        if (!detail.getOrder().getId().equals(ticketId)) {
            throw new RuntimeException("Sale Ticket Detail does not belong to the specified Sale Ticket");
        }
        
        // Check if detail is already allocated
        if (detail.getStatus() == OrderStatus.ALLOCATED) {
            throw new RuntimeException("Order detail is already allocated");
        }
        
        // Check if detail can be allocated (should be NEW status)
        if (detail.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Order detail must be in NEW status to be allocated");
        }
        
        // Check if there's enough stock to allocate
        ProductVariant productVariant = detail.getProductVariant();
        ProductInventory inventory = productInventoryRepository.findByProductVariant_Id(productVariant.getId())
            .orElseThrow(() -> new RuntimeException("ProductInventory not found for product variant: " + productVariant.getSku()));
        
        BigDecimal availableStock = inventory.getCurrentStock().subtract(inventory.getAllocatedStock());
        if (availableStock.compareTo(detail.getQuantity()) < 0) {
            throw new RuntimeException("Insufficient stock to allocate. Available: " + availableStock + ", Required: " + detail.getQuantity());
        }
        
        // Update detail status to ALLOCATED
        OrderStatus oldStatus = detail.getStatus();
        detail.setStatus(OrderStatus.ALLOCATED);

        // Save the detail
        detail = orderDetailRepository.save(detail);
        
        // Update inventory - subtract from current stock
        updateProductInventoryForSale(detail, oldStatus, OrderStatus.ALLOCATED, user);
        
        // Check if all details are allocated, then update order status
        boolean allDetailsAllocated = ticket.getOrderDetail().stream()
            .allMatch(d -> d.getStatus() == OrderStatus.ALLOCATED);
        
        if (allDetailsAllocated) {
            ticket.setStatus(OrderStatus.ALLOCATED);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setUpdatedBy(user);
            ordersRepository.save(ticket);
            
            // Log the ticket status change
            logTicketStatusChange(ticket, oldStatus, OrderStatus.ALLOCATED, 
                "All order details allocated", user);
        }
        
        // Return updated ticket response
        return getSaleTicketById(ticketId);
    }

    // Helper methods for Sale Service
    private void validateSaleStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        List<SaleStatusTransitionRule> rules = getStatusTransitionRules();
        
        SaleStatusTransitionRule currentRule = rules.stream()
            .filter(rule -> rule.getCurrentStatus() == currentStatus)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Invalid current status: " + currentStatus));
        
        if (!currentRule.getAllowedTransitions().contains(newStatus)) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    private void updateSaleTicketStatusBasedOnDetails(Order ticket, User user) {
        List<OrderDetail> details = ticket.getOrderDetail();
        
        if (details.isEmpty()) {
            return;
        }
        
        // Count statuses
        long totalDetails = details.size();
        long allocatedCount = details.stream()
            .filter(d -> d.getStatus() == OrderStatus.ALLOCATED)
            .count();
        
        OrderStatus oldStatus = ticket.getStatus();
        OrderStatus newTicketStatus = ticket.getStatus();
        
        // Determine new ticket status based on detail statuses
        if (allocatedCount == totalDetails) {
            // All details allocated
            newTicketStatus = OrderStatus.ALLOCATED;
        } else if (allocatedCount > 0) {
            // Some details allocated - keep current status
            newTicketStatus = ticket.getStatus();
        } else {
            // No details allocated - keep current status
            newTicketStatus = ticket.getStatus();
        } 
        
        // Update ticket status if changed
        if (ticket.getStatus() != newTicketStatus) {
            ticket.setStatus(newTicketStatus);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setUpdatedBy(user);

            ordersRepository.save(ticket);
            
            // Log the ticket status change
            logTicketStatusChange(ticket, oldStatus, newTicketStatus, "Status updated based on detail changes", user);
        }
    }

    private void logTicketStatusChange(Order ticket, OrderStatus oldStatus, OrderStatus newStatus, String note, User user) {
        OrderStatusLog statusLog = new OrderStatusLog();
        statusLog.setOrder(ticket);
        statusLog.setOld_status(oldStatus);
        statusLog.setNew_status(newStatus);
        statusLog.setNote(note);
        statusLog.setUpdatedAt(LocalDateTime.now());
        statusLog.setUpdatedBy(user);
        ordersStatusLogRepository.save(statusLog);
    }

    private void updateProductInventoryForSale(OrderDetail detail, OrderStatus oldStatus, OrderStatus newStatus, User user) {
        ProductVariant productVariant = detail.getProductVariant();
        if (productVariant == null) {
            return;
        }

        BigDecimal quantity = detail.getQuantity();
        Long variantId = productVariant.getId();
        Long ticketId = detail.getOrder().getId();

        // ProductInventory is guaranteed to exist at this point since it's created during ticket creation
        if (oldStatus == OrderStatus.NEW && newStatus == OrderStatus.ALLOCATED) {
            // When status changes from NEW to ALLOCATED, subtract from current stock
            // This indicates the product is allocated for sale
            productInventoryService.addToAllocatedStock(variantId, quantity, user, ticketId);

        } else if (oldStatus == OrderStatus.ALLOCATED && newStatus == OrderStatus.PACKED) {
            // When status changes from ALLOCATED to PACKED
            // This indicates the product is no longer in warehouse, and can use
            productInventoryService.removeFromAllocatedAndCurrentStock(variantId, quantity, user, ticketId);

        } else if ((oldStatus == OrderStatus.PACKED && newStatus == OrderStatus.SHIPPED) ||
                    (oldStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.DONE)) {
            // When status changes from SHIPPING to COMPLETED, no inventory change needed
            // This indicates the sale is completed

        } else if (oldStatus == OrderStatus.ALLOCATED && newStatus == OrderStatus.CANCELLED) {
            // When status changes from ALLOCATED to CANCELLED, remove from allocated stock
            // This indicates the sale is cancelled
            productInventoryService.removeFromAllocatedStock(variantId, quantity, user, ticketId);

        } else if (oldStatus == OrderStatus.PACKED && newStatus == OrderStatus.CANCELLED) {
            // When status changes from SHIPPING to CANCELLED, add back to current stock
            // This indicates the sale is cancelled
            productInventoryService.addToCurrentStock(variantId, quantity, user, ticketId);

        } else if ((oldStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.CANCELLED ||
                    oldStatus == OrderStatus.NEW && newStatus == OrderStatus.CANCELLED)){
            // The product had already shipped, no longer in the warehouse 
            // product can not return to the warehouse so nothing change until they got the 
            // product back and add the stock manually

        }
    }
    
}
