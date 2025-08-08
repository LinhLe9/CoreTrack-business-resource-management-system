package org.example.coretrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.coretrack.exception.ProductInventoryAlreadyExistsException;
import org.example.coretrack.exception.MaterialInventoryAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ProductInventoryAlreadyExistsException.class)
    public ResponseEntity<?> handleProductInventoryAlreadyExists(ProductInventoryAlreadyExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("error", "PRODUCT_INVENTORY_ALREADY_EXISTS");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MaterialInventoryAlreadyExistsException.class)
    public ResponseEntity<?> handleMaterialInventoryAlreadyExists(MaterialInventoryAlreadyExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("error", "MATERIAL_INVENTORY_ALREADY_EXISTS");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
