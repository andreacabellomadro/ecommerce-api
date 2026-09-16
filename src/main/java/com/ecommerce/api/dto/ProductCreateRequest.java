package com.ecommerce.api.dto;

import java.math.BigDecimal;

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

    private String name;
    private BigDecimal price;
    private int stock;

}
