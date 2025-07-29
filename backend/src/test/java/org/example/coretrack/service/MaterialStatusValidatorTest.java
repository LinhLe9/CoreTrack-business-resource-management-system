package org.example.coretrack.service;

import org.example.coretrack.model.material.MaterialStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MaterialStatusValidatorTest {

    private MaterialStatusValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MaterialStatusValidator();
    }

    @Test
    void testValidTransitions() {
        // ACTIVE can transition to INACTIVE, DISCONTINUED, DELETED
        assertTrue(validator.isValidTransition(MaterialStatus.ACTIVE, MaterialStatus.INACTIVE));
        assertTrue(validator.isValidTransition(MaterialStatus.ACTIVE, MaterialStatus.DISCONTINUED));
        assertTrue(validator.isValidTransition(MaterialStatus.ACTIVE, MaterialStatus.DELETED));

        // INACTIVE can transition to ACTIVE, DISCONTINUED, DELETED
        assertTrue(validator.isValidTransition(MaterialStatus.INACTIVE, MaterialStatus.ACTIVE));
        assertTrue(validator.isValidTransition(MaterialStatus.INACTIVE, MaterialStatus.DISCONTINUED));
        assertTrue(validator.isValidTransition(MaterialStatus.INACTIVE, MaterialStatus.DELETED));

        // DISCONTINUED can transition to ACTIVE, INACTIVE, DELETED
        assertTrue(validator.isValidTransition(MaterialStatus.DISCONTINUED, MaterialStatus.ACTIVE));
        assertTrue(validator.isValidTransition(MaterialStatus.DISCONTINUED, MaterialStatus.INACTIVE));
        assertTrue(validator.isValidTransition(MaterialStatus.DISCONTINUED, MaterialStatus.DELETED));
    }

    @Test
    void testInvalidTransitions() {
        // DELETED cannot transition to any status
        assertFalse(validator.isValidTransition(MaterialStatus.DELETED, MaterialStatus.ACTIVE));
        assertFalse(validator.isValidTransition(MaterialStatus.DELETED, MaterialStatus.INACTIVE));
        assertFalse(validator.isValidTransition(MaterialStatus.DELETED, MaterialStatus.DISCONTINUED));
    }

    @Test
    void testSameStatusTransition() {
        // Cannot transition to the same status
        assertFalse(validator.isValidTransition(MaterialStatus.ACTIVE, MaterialStatus.ACTIVE));
        assertFalse(validator.isValidTransition(MaterialStatus.INACTIVE, MaterialStatus.INACTIVE));
        assertFalse(validator.isValidTransition(MaterialStatus.DISCONTINUED, MaterialStatus.DISCONTINUED));
    }

    @Test
    void testNullStatus() {
        assertFalse(validator.isValidTransition(null, MaterialStatus.ACTIVE));
        assertFalse(validator.isValidTransition(MaterialStatus.ACTIVE, null));
        assertFalse(validator.isValidTransition(null, null));
    }

    @Test
    void testGetValidTransitions() {
        Set<MaterialStatus> activeTransitions = validator.getValidTransitions(MaterialStatus.ACTIVE);
        assertEquals(3, activeTransitions.size());
        assertTrue(activeTransitions.contains(MaterialStatus.INACTIVE));
        assertTrue(activeTransitions.contains(MaterialStatus.DISCONTINUED));
        assertTrue(activeTransitions.contains(MaterialStatus.DELETED));

        Set<MaterialStatus> deletedTransitions = validator.getValidTransitions(MaterialStatus.DELETED);
        assertTrue(deletedTransitions.isEmpty());
    }

    @Test
    void testGetInvalidTransitionMessage() {
        String deletedMessage = validator.getInvalidTransitionMessage(MaterialStatus.DELETED, MaterialStatus.ACTIVE);
        assertTrue(deletedMessage.contains("Cannot change status of deleted material"));

        String sameStatusMessage = validator.getInvalidTransitionMessage(MaterialStatus.ACTIVE, MaterialStatus.ACTIVE);
        assertTrue(sameStatusMessage.contains("already in ACTIVE status"));
    }
} 