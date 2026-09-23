package com.ecommerce.api.controller;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.dto.ProductCreateRequest;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.dto.ProductUpdateRequest;
import com.ecommerce.api.exception.InvalidRequestParameterException;
import com.ecommerce.api.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private static final int MAX_PAGE_SIZE = 50;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price", "stock");

    @GetMapping
    public Page<ProductResponse> getAllProducts(
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        if (pageable.getPageSize() < 1 ||
                pageable.getPageSize() > MAX_PAGE_SIZE) {

            throw new InvalidRequestParameterException(
                    "Page size must be between 1 and " + MAX_PAGE_SIZE);
        }

        for (Sort.Order order : pageable.getSort()) {

            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestParameterException(
                        "Sorting by '" + order.getProperty() + "' is not allowed");
            }
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestParameterException(
                    "minPrice cannot be negative");
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestParameterException(
                    "maxPrice cannot be negative");
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new InvalidRequestParameterException(
                    "minPrice cannot be greater than maxPrice");
        }
        return productService.getAllProducts(pageable, name, minPrice, maxPrice);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest) {
        return productService.createProduct(productCreateRequest);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        return productService.updateProduct(id, productUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
