package com.ecommerce.api.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.api.dto.ProductCreateRequest;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.dto.ProductUpdateRequest;
import com.ecommerce.api.exception.ProductNotFoundException;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.repository.ProductRepository;
import com.ecommerce.api.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return entityToResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(
            Pageable pageable,
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        Specification<Product> specification = null;

        if (name != null && !name.isBlank()) {
            specification = ProductSpecification.nameContains(name);
        }

        if (minPrice != null) {
            Specification<Product> priceSpecification = ProductSpecification.priceGreaterThanOrEqualTo(minPrice);

            specification = specification == null
                    ? priceSpecification
                    : specification.and(priceSpecification);
        }

        if (maxPrice != null) {
            Specification<Product> priceSpecification = ProductSpecification.priceLessThanOrEqualTo(maxPrice);

            specification = specification == null
                    ? priceSpecification
                    : specification.and(priceSpecification);
        }

        Page<Product> products = productRepository.findAll(specification, pageable);

        return products.map(this::entityToResponse);
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {
        Product product = Product.builder()
                .name(productCreateRequest.getName())
                .price(productCreateRequest.getPrice())
                .stock(productCreateRequest.getStock())
                .build();
        return entityToResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setName(productUpdateRequest.getName());
        product.setPrice(productUpdateRequest.getPrice());
        product.setStock(productUpdateRequest.getStock());
        return entityToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public ProductResponse entityToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

}
