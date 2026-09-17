package com.ecommerce.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class ProductCreateRequest {

    @NotBlank (message = "Product name must not be blank")
    private String name;

    @NotNull (message = "Product price must not be null")
    @Positive (message = "Product price must be a positive value")
    private BigDecimal price;

    @PositiveOrZero (message = "Product stock must be a non-negative value")
    private int stock;

}
