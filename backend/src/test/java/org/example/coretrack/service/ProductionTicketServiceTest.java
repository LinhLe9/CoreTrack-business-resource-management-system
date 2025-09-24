package org.example.coretrack.service;

import org.example.coretrack.dto.productionTicket.*;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.productionTicket.ProductionTicket;
import org.example.coretrack.model.productionTicket.ProductionTicketStatus;
import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatus;
import org.example.coretrack.service.ProductionTicketService;
import org.example.coretrack.service.ProductionTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionTicketServiceTest {

    @Mock
    private ProductionTicketService productionTicketService;

    @InjectMocks
    private ProductionTicketServiceImpl productionTicketServiceImpl;

    private User testUser;
    private CreateProductionTicketRequest createRequest;
    private BulkCreateProductionTicketRequest bulkCreateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        // Setup create request
        createRequest = new CreateProductionTicketRequest();
        createRequest.setName("Test Production Ticket");
        createRequest.setExpected_complete_date(LocalDateTime.now().plusDays(7));

        // Setup bulk create request
        bulkCreateRequest = new BulkCreateProductionTicketRequest();
        bulkCreateRequest.setName("Bulk Test Production Ticket");
        
        ProductVariantBomRequest productVariantRequest = new ProductVariantBomRequest();
        productVariantRequest.setProductVariantSku("TEST-SKU-001");
        productVariantRequest.setQuantity(BigDecimal.valueOf(10));
        productVariantRequest.setExpectedCompleteDate(LocalDate.now().plusDays(5));
        
        bulkCreateRequest.setProductVariants(Arrays.asList(productVariantRequest));
    }

    @Test
    void testCreateProductionTicket_Success() {
        // Arrange
        CreateProductionTicketResponse expectedResponse = new CreateProductionTicketResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName("Test Production Ticket");
        expectedResponse.setStatus("NEW");

        when(productionTicketService.createProductionTicket(any(CreateProductionTicketRequest.class), any(User.class)))
                .thenReturn(expectedResponse);

        // Act
        CreateProductionTicketResponse result = productionTicketService.createProductionTicket(createRequest, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Production Ticket", result.getName());
        assertEquals("NEW", result.getStatus());
        verify(productionTicketService, times(1)).createProductionTicket(createRequest, testUser);
    }

    @Test
    void testCreateProductionTicket_ThrowsException() {
        // Arrange
        when(productionTicketService.createProductionTicket(any(CreateProductionTicketRequest.class), any(User.class)))
                .thenThrow(new RuntimeException("Failed to create production ticket"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productionTicketService.createProductionTicket(createRequest, testUser);
        });
    }

    @Test
    void testBulkCreateProductionTicket_Success() {
        // Arrange
        BulkCreateProductionTicketResponse expectedResponse = new BulkCreateProductionTicketResponse();
        expectedResponse.setTotalRequested(1);
        expectedResponse.setTotalCreated(1);
        expectedResponse.setTotalFailed(0);
        expectedResponse.setErrors(Arrays.asList());

        when(productionTicketService.bulkCreateProductionTicket(any(BulkCreateProductionTicketRequest.class), any(User.class)))
                .thenReturn(expectedResponse);

        // Act
        BulkCreateProductionTicketResponse result = productionTicketService.bulkCreateProductionTicket(bulkCreateRequest, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalRequested());
        assertEquals(1, result.getTotalCreated());
        assertEquals(0, result.getTotalFailed());
        assertTrue(result.getErrors().isEmpty());
        verify(productionTicketService, times(1)).bulkCreateProductionTicket(bulkCreateRequest, testUser);
    }

    @Test
    void testBulkCreateProductionTicket_PartialFailure() {
        // Arrange
        BulkCreateProductionTicketResponse expectedResponse = new BulkCreateProductionTicketResponse();
        expectedResponse.setTotalRequested(2);
        expectedResponse.setTotalCreated(1);
        expectedResponse.setTotalFailed(1);
        expectedResponse.setErrors(Arrays.asList("Insufficient material stock for product variant"));

        when(productionTicketService.bulkCreateProductionTicket(any(BulkCreateProductionTicketRequest.class), any(User.class)))
                .thenReturn(expectedResponse);

        // Act
        BulkCreateProductionTicketResponse result = productionTicketService.bulkCreateProductionTicket(bulkCreateRequest, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalRequested());
        assertEquals(1, result.getTotalCreated());
        assertEquals(1, result.getTotalFailed());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains("Insufficient material stock for product variant"));
    }

    @Test
    void testGetProductionTicketById_Success() {
        // Arrange
        ProductionTicketResponse expectedResponse = new ProductionTicketResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName("Test Production Ticket");

        when(productionTicketService.getProductionTicketById(1L, testUser))
                .thenReturn(expectedResponse);

        // Act
        ProductionTicketResponse result = productionTicketService.getProductionTicketById(1L, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Production Ticket", result.getName());
        verify(productionTicketService, times(1)).getProductionTicketById(1L, testUser);
    }

    @Test
    void testGetProductionTicketById_NotFound() {
        // Arrange
        when(productionTicketService.getProductionTicketById(999L, testUser))
                .thenThrow(new RuntimeException("Production ticket not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productionTicketService.getProductionTicketById(999L, testUser);
        });
    }

    @Test
    void testGetProductionTickets_Success() {
        // Arrange
        ProductionTicketCardResponse ticket1 = new ProductionTicketCardResponse();
        ticket1.setId(1L);
        ticket1.setName("Ticket 1");

        ProductionTicketCardResponse ticket2 = new ProductionTicketCardResponse();
        ticket2.setId(2L);
        ticket2.setName("Ticket 2");

        List<ProductionTicketCardResponse> tickets = Arrays.asList(ticket1, ticket2);
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductionTicketCardResponse> expectedPage = new PageImpl<>(tickets, pageable, 2);

        when(productionTicketService.getProductionTickets(anyString(), anyList(), any(Pageable.class), any(User.class)))
                .thenReturn(expectedPage);

        // Act
        Page<ProductionTicketCardResponse> result = productionTicketService.getProductionTickets("test", Arrays.asList("NEW"), pageable, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        verify(productionTicketService, times(1)).getProductionTickets("test", Arrays.asList("NEW"), pageable, testUser);
    }

    @Test
    void testGetAutoComplete_Success() {
        // Arrange
        ProductionTicketCardResponse ticket1 = new ProductionTicketCardResponse();
        ticket1.setId(1L);
        ticket1.setName("Test Ticket 1");

        ProductionTicketCardResponse ticket2 = new ProductionTicketCardResponse();
        ticket2.setId(2L);
        ticket2.setName("Test Ticket 2");

        List<ProductionTicketCardResponse> expectedTickets = Arrays.asList(ticket1, ticket2);

        when(productionTicketService.getAutoComplete("test", testUser))
                .thenReturn(expectedTickets);

        // Act
        List<ProductionTicketCardResponse> result = productionTicketService.getAutoComplete("test", testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Ticket 1", result.get(0).getName());
        assertEquals("Test Ticket 2", result.get(1).getName());
        verify(productionTicketService, times(1)).getAutoComplete("test", testUser);
    }

    @Test
    void testGetStatusTransitionRules_Success() {
        // Arrange
        StatusTransitionRule rule1 = new StatusTransitionRule();
        rule1.setCurrentStatus(ProductionTicketDetailStatus.NEW);
        rule1.setAllowedTransitions(Arrays.asList(ProductionTicketDetailStatus.APPROVAL));

        StatusTransitionRule rule2 = new StatusTransitionRule();
        rule2.setCurrentStatus(ProductionTicketDetailStatus.APPROVAL);
        rule2.setAllowedTransitions(Arrays.asList(ProductionTicketDetailStatus.COMPLETE));

        List<StatusTransitionRule> expectedRules = Arrays.asList(rule1, rule2);

        when(productionTicketService.getStatusTransitionRules())
                .thenReturn(expectedRules);

        // Act
        List<StatusTransitionRule> result = productionTicketService.getStatusTransitionRules();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ProductionTicketDetailStatus.NEW, result.get(0).getCurrentStatus());
        assertEquals(ProductionTicketDetailStatus.APPROVAL, result.get(0).getAllowedTransitions().get(0));
        assertEquals(ProductionTicketDetailStatus.APPROVAL, result.get(1).getCurrentStatus());
        assertEquals(ProductionTicketDetailStatus.COMPLETE, result.get(1).getAllowedTransitions().get(0));
        verify(productionTicketService, times(1)).getStatusTransitionRules();
    }

    @Test
    void testUpdateDetailStatus_Success() {
        // Arrange
        UpdateDetailStatusRequest updateRequest = new UpdateDetailStatusRequest();
        updateRequest.setNewStatus(ProductionTicketDetailStatus.APPROVAL);

        ProductionTicketDetailResponse expectedResponse = new ProductionTicketDetailResponse();
        expectedResponse.setId(1L);
        expectedResponse.setStatus("APPROVAL");

        when(productionTicketService.updateDetailStatus(1L, 1L, updateRequest, testUser))
                .thenReturn(expectedResponse);

        // Act
        ProductionTicketDetailResponse result = productionTicketService.updateDetailStatus(1L, 1L, updateRequest, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("APPROVAL", result.getStatus());
        verify(productionTicketService, times(1)).updateDetailStatus(1L, 1L, updateRequest, testUser);
    }

    @Test
    void testCancelProductionTicket_Success() {
        // Arrange
        ProductionTicket expectedTicket = new ProductionTicket();
        expectedTicket.setId(1L);
        expectedTicket.setStatus(ProductionTicketStatus.CANCELLED);

        when(productionTicketService.cancelProductionTicket(1L, "User cancelled", testUser))
                .thenReturn(expectedTicket);

        // Act
        ProductionTicket result = productionTicketService.cancelProductionTicket(1L, "User cancelled", testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ProductionTicketStatus.CANCELLED, result.getStatus());
        verify(productionTicketService, times(1)).cancelProductionTicket(1L, "User cancelled", testUser);
    }

    @Test
    void testGetAllProductionTicketStatuses_Success() {
        // Arrange
        ProductionTicketStatusesResponse expectedResponse = new ProductionTicketStatusesResponse();
        
        List<ProductionTicketStatusesResponse.StatusInfo> ticketStatuses = Arrays.asList(
            new ProductionTicketStatusesResponse.StatusInfo("NEW", "New", "New ticket"),
            new ProductionTicketStatusesResponse.StatusInfo("IN_PROGRESS", "In Progress", "In progress ticket"),
            new ProductionTicketStatusesResponse.StatusInfo("COMPLETE", "Complete", "Completed ticket"),
            new ProductionTicketStatusesResponse.StatusInfo("CANCELLED", "Cancelled", "Cancelled ticket")
        );
        
        List<ProductionTicketStatusesResponse.StatusInfo> detailStatuses = Arrays.asList(
            new ProductionTicketStatusesResponse.StatusInfo("NEW", "New", "New detail"),
            new ProductionTicketStatusesResponse.StatusInfo("IN_PROGRESS", "In Progress", "In progress detail"),
            new ProductionTicketStatusesResponse.StatusInfo("COMPLETE", "Complete", "Completed detail"),
            new ProductionTicketStatusesResponse.StatusInfo("CANCELLED", "Cancelled", "Cancelled detail")
        );
        
        expectedResponse.setProductionTicketStatuses(ticketStatuses);
        expectedResponse.setProductionTicketDetailStatuses(detailStatuses);

        when(productionTicketService.getAllProductionTicketStatuses())
                .thenReturn(expectedResponse);

        // Act
        ProductionTicketStatusesResponse result = productionTicketService.getAllProductionTicketStatuses();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getProductionTicketStatuses().size());
        assertEquals(4, result.getProductionTicketDetailStatuses().size());
        assertTrue(result.getProductionTicketStatuses().stream().anyMatch(s -> s.getName().equals("NEW")));
        assertTrue(result.getProductionTicketDetailStatuses().stream().anyMatch(s -> s.getName().equals("NEW")));
        verify(productionTicketService, times(1)).getAllProductionTicketStatuses();
    }

    @Test
    void testTestCascadeRelationships_Success() {
        // Arrange
        when(productionTicketService.testCascadeRelationships(1L))
                .thenReturn(true);

        // Act
        boolean result = productionTicketService.testCascadeRelationships(1L);

        // Assert
        assertTrue(result);
        verify(productionTicketService, times(1)).testCascadeRelationships(1L);
    }

    @Test
    void testTestCascadeRelationships_Failure() {
        // Arrange
        when(productionTicketService.testCascadeRelationships(999L))
                .thenReturn(false);

        // Act
        boolean result = productionTicketService.testCascadeRelationships(999L);

        // Assert
        assertFalse(result);
        verify(productionTicketService, times(1)).testCascadeRelationships(999L);
    }
}
