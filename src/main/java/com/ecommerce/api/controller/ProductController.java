package com.ecommerce.api.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.dto.ProductCreateRequest;
import com.ecommerce.api.dto.ProductFilter;
import com.ecommerce.api.dto.ProductPageResponse;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.dto.ProductUpdateRequest;
import com.ecommerce.api.exception.InvalidRequestParameterException;
import com.ecommerce.api.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag (name = "Products", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price", "stock");

    @Operation(summary = "Get all products", 
    description = "Retrieve a paginated list of products with optional filtering and sorting by name, price, and stock.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "400", description = "Invalid request, sorting or filtering parameters")
    })
    @GetMapping
    public ProductPageResponse getAllProducts(
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @Valid @ModelAttribute ProductFilter productFilter) {

        for (Sort.Order order : pageable.getSort()) {

            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestParameterException(
                        "Sorting by '" + order.getProperty() + "' is not allowed");
            }
        }

        if (productFilter.getMinPrice() != null
                && productFilter.getMaxPrice() != null
                && productFilter.getMinPrice().compareTo(productFilter.getMaxPrice()) > 0) {

            throw new InvalidRequestParameterException(
                    "minPrice cannot be greater than maxPrice");
        }
        if (productFilter.getMinStock() != null
                && productFilter.getMaxStock() != null
                && productFilter.getMinStock() > productFilter.getMaxStock()) {

            throw new InvalidRequestParameterException(
                    "minStock cannot be greater than maxStock");
        }

        Page<ProductResponse> products = productService.getAllProducts(pageable, productFilter);

        return ProductPageResponse.builder()
        .content(products.getContent())
        .page(products.getNumber())
        .size(products.getSize())
        .totalElements(products.getTotalElements())
        .totalPages(products.getTotalPages())
        .build();
    }

    @Operation(summary = "Get product by ID", 
    description = "Retrieve a product by its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @Operation(summary = "Create a new product", 
    description = "Create a new product with the provided details.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest) {
        return productService.createProduct(productCreateRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", 
    description = "Update an existing product with the provided details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponse updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        return productService.updateProduct(id, productUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", 
    description = "Delete an existing product.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
