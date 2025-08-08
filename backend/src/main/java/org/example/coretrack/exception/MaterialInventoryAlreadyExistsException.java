package org.example.coretrack.exception;

public class MaterialInventoryAlreadyExistsException extends RuntimeException {
    
    public MaterialInventoryAlreadyExistsException(String message) {
        super(message);
    }
    
    public MaterialInventoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 