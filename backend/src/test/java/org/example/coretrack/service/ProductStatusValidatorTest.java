package org.example.coretrack.service;

import org.example.coretrack.model.product.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class ProductStatusValidatorTest {
    private ProductStatusValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductStatusValidator();
    }

    @Test
    void testValidTransitions() {
        // ACTIVE can transition to INACTIVE, DISCONTINUED, DELETED
        assertTrue(validator.isValidTransition(ProductStatus.ACTIVE, ProductStatus.INACTIVE));
        assertTrue(validator.isValidTransition(ProductStatus.ACTIVE, ProductStatus.DISCONTINUED));
        assertTrue(validator.isValidTransition(ProductStatus.ACTIVE, ProductStatus.DELETED));

        // INACTIVE can transition to ACTIVE, DISCONTINUED, DELETED
        assertTrue(validator.isValidTransition(ProductStatus.INACTIVE, ProductStatus.ACTIVE));
        assertTrue(validator.isValidTransition(ProductStatus.INACTIVE, ProductStatus.DISCONTINUED));
        assertTrue(validator.isValidTransition(ProductStatus.INACTIVE, ProductStatus.DELETED));

        // DISCONTINUED can transition to ACTIVE, INACTIVE, DELETED
        assertTrue(validator.isValidTransition(ProductStatus.DISCONTINUED, ProductStatus.ACTIVE));
        assertTrue(validator.isValidTransition(ProductStatus.DISCONTINUED, ProductStatus.INACTIVE));
        assertTrue(validator.isValidTransition(ProductStatus.DISCONTINUED, ProductStatus.DELETED));
    }

    @Test
    void testInvalidTransitions() {
        // DELETED cannot transition to any status
        assertFalse(validator.isValidTransition(ProductStatus.DELETED, ProductStatus.ACTIVE));
        assertFalse(validator.isValidTransition(ProductStatus.DELETED, ProductStatus.INACTIVE));
        assertFalse(validator.isValidTransition(ProductStatus.DELETED, ProductStatus.DISCONTINUED));
    }

    @Test
    void testSameStatusTransition() {
        // Cannot transition to the same status
        assertFalse(validator.isValidTransition(ProductStatus.ACTIVE, ProductStatus.ACTIVE));
        assertFalse(validator.isValidTransition(ProductStatus.INACTIVE, ProductStatus.INACTIVE));
        assertFalse(validator.isValidTransition(ProductStatus.DISCONTINUED, ProductStatus.DISCONTINUED));
        assertFalse(validator.isValidTransition(ProductStatus.DELETED, ProductStatus.DELETED));
    }

    @Test
    void testNullStatus() {
        assertFalse(validator.isValidTransition(null, ProductStatus.ACTIVE));
        assertFalse(validator.isValidTransition(ProductStatus.ACTIVE, null));
        assertFalse(validator.isValidTransition(null, null));
    }

    @Test
    void testGetValidTransitions() {
        Set<ProductStatus> activeTransitions = validator.getValidTransitions(ProductStatus.ACTIVE);
        assertTrue(activeTransitions.contains(ProductStatus.INACTIVE));
        assertTrue(activeTransitions.contains(ProductStatus.DISCONTINUED));
        assertTrue(activeTransitions.contains(ProductStatus.DELETED));
        assertFalse(activeTransitions.contains(ProductStatus.ACTIVE));

        Set<ProductStatus> deletedTransitions = validator.getValidTransitions(ProductStatus.DELETED);
        assertTrue(deletedTransitions.isEmpty());
    }

    @Test
    void testGetInvalidTransitionMessage() {
        String deletedMessage = validator.getInvalidTransitionMessage(ProductStatus.DELETED, ProductStatus.ACTIVE);
        assertTrue(deletedMessage.contains("Cannot change status of deleted product"));

        String sameStatusMessage = validator.getInvalidTransitionMessage(ProductStatus.ACTIVE, ProductStatus.ACTIVE);
        assertTrue(sameStatusMessage.contains("already in ACTIVE status"));

        String nullMessage = validator.getInvalidTransitionMessage(null, ProductStatus.ACTIVE);
        assertTrue(nullMessage.contains("null status provided"));
    }
} 