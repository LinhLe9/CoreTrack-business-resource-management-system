package org.example.coretrack.exception;

public class ProductInventoryAlreadyExistsException extends RuntimeException {
    
    public ProductInventoryAlreadyExistsException(String message) {
        super(message);
    }
    
    public ProductInventoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 