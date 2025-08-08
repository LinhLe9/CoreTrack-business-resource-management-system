package org.example.coretrack.dto.product.inventory;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

public class SetMinMaxRequest {
    
    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Value must be greater than or equal to 0")
    private BigDecimal value;

    public SetMinMaxRequest() {}

    public SetMinMaxRequest(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
} 