package com.ecommerce.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder 
public class ProductFilter {

    private String name;
    
    @PositiveOrZero(message = "minPrice cannot be negative")
    private BigDecimal minPrice;

    @PositiveOrZero(message = "maxPrice cannot be negative")
    private BigDecimal maxPrice;

    @PositiveOrZero(message = "minStock cannot be negative")
    private Integer minStock;

    @PositiveOrZero(message = "maxStock cannot be negative")
    private Integer maxStock;

}
