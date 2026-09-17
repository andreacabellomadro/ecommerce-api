package com.ecommerce.api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.api.dto.ProductCreateRequest;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.exception.ProductNotFoundException;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldGetProductById() {

        Product product = Product.builder()
                .id(1L)
                .name("Teclado")
                .price(new BigDecimal("49.99"))
                .stock(10)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Teclado");
        assertThat(response.getPrice())
                .isEqualByComparingTo("49.99");
        assertThat(response.getStock()).isEqualTo(10);

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999L));

        verify(productRepository).findById(999L);
    }

    @Test
    void shouldCreateProduct() {

        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Teclado")
                .price(new BigDecimal("49.99"))
                .stock(10)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Teclado")
                .price(new BigDecimal("49.99"))
                .stock(10)
                .build();

        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product productSentToRepository = productCaptor.getValue();

        assertThat(productSentToRepository.getName())
                .isEqualTo("Teclado");

        assertThat(productSentToRepository.getPrice())
                .isEqualByComparingTo("49.99");

        assertThat(productSentToRepository.getStock())
                .isEqualTo(10);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateProduct() {

        Product existingProduct = Product.builder()
                .id(1L)
                .name("Teclado antiguo")
                .price(new BigDecimal("30.00"))
                .stock(5)
                .build();

        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Teclado nuevo")
                .price(new BigDecimal("50.00"))
                .stock(10)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        ProductResponse response = productService.updateProduct(1L, request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Teclado nuevo");
        assertThat(response.getPrice())
                .isEqualByComparingTo("50.00");
        assertThat(response.getStock()).isEqualTo(10);

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {

        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Teclado")
                .price(new BigDecimal("50.00"))
                .stock(10)
                .build();

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(999L, request));

        verify(productRepository).findById(999L);
    }

    @Test
    void shouldDeleteProduct() {

        Product product = Product.builder()
                .id(1L)
                .name("Teclado")
                .price(new BigDecimal("49.99"))
                .stock(10)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(999L));

        verify(productRepository).findById(999L);

        verify(productRepository,
                org.mockito.Mockito.never())
                .delete(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void shouldGetAllProducts() {

        Product product1 = Product.builder()
                .id(1L)
                .name("Teclado")
                .price(new BigDecimal("49.99"))
                .stock(10)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Ratón")
                .price(new BigDecimal("29.99"))
                .stock(20)
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponse> responses = productService.getAllProducts();

        assertThat(responses).hasSize(2);

        assertThat(responses.get(0).getName())
                .isEqualTo("Teclado");

        assertThat(responses.get(1).getName())
                .isEqualTo("Ratón");

        verify(productRepository).findAll();
    }
}