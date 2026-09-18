package com.ecommerce.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.api.dto.ProductCreateRequest;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.dto.ProductUpdateRequest;
import com.ecommerce.api.exception.ProductNotFoundException;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional (readOnly = true)
    public ProductResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return entityToResponse(product);
    }

    @Transactional (readOnly = true)
    public List<ProductResponse> getAllProducts(){
        List<Product> products;
        products = productRepository.findAll();
        return products.stream()
                .map(this::entityToResponse)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest productCreateRequest){
        Product product = Product.builder()
                .name(productCreateRequest.getName())
                .price(productCreateRequest.getPrice())
                .stock(productCreateRequest.getStock())
                .build();
        return entityToResponse(productRepository.save(product));
    }

    @Transactional 
    public ProductResponse updateProduct(Long id, ProductUpdateRequest productUpdateRequest){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setName(productUpdateRequest.getName());
        product.setPrice(productUpdateRequest.getPrice());
        product.setStock(productUpdateRequest.getStock());
        return entityToResponse(product);
    }

    @Transactional 
    public void deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public ProductResponse entityToResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

}
