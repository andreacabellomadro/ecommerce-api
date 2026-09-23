package com.ecommerce.api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.ecommerce.api.dto.ProductCreateRequest;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.dto.ProductUpdateRequest;
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

                ProductUpdateRequest request = ProductUpdateRequest.builder()
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

                ProductUpdateRequest request = ProductUpdateRequest.builder()
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

                Pageable pageable = PageRequest.of(0, 10);

                Page<Product> page = new PageImpl<>(
                                List.of(product1, product2),
                                pageable,
                                2);

                when(productRepository.findAll(
                                ArgumentMatchers.<Specification<Product>>isNull(),
                                ArgumentMatchers.eq(pageable))).thenReturn(page);

                Page<ProductResponse> result = productService.getAllProducts(pageable, null, null, null);

                assertThat(result.getContent()).hasSize(2);
                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Teclado");
                assertThat(result.getContent().get(1).getName())
                                .isEqualTo("Ratón");

                assertThat(result.getNumber()).isEqualTo(0);
                assertThat(result.getSize()).isEqualTo(10);
                assertThat(result.getTotalElements()).isEqualTo(2);
                assertThat(result.getTotalPages()).isEqualTo(1);

                verify(productRepository).findAll(
                                ArgumentMatchers.<Specification<Product>>isNull(),
                                ArgumentMatchers.eq(pageable));
        }

        @Test
        void shouldGetProductsByName() {

                Product product = Product.builder()
                                .id(1L)
                                .name("Teclado mecánico")
                                .price(new BigDecimal("79.99"))
                                .stock(10)
                                .build();

                Pageable pageable = PageRequest.of(0, 10);

                Page<Product> page = new PageImpl<>(
                                List.of(product),
                                pageable,
                                1);

                when(productRepository.findAll(
                                ArgumentMatchers.any(Specification.class),
                                ArgumentMatchers.eq(pageable))).thenReturn(page);

                Page<ProductResponse> result = productService.getAllProducts(
                                pageable,
                                "teclado",
                                null,
                                null);

                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Teclado mecánico");

                verify(productRepository)
                                .findAll(
                                                ArgumentMatchers.any(Specification.class),
                                                ArgumentMatchers.eq(pageable));
        }

        @Test
        void shouldGetProductsWithCombinedFilters() {

                Product product = Product.builder()
                                .id(1L)
                                .name("Teclado mecánico")
                                .price(new BigDecimal("79.99"))
                                .stock(10)
                                .build();

                Pageable pageable = PageRequest.of(0, 10);

                Page<Product> page = new PageImpl<>(
                                List.of(product),
                                pageable,
                                1);

                when(productRepository.findAll(
                                ArgumentMatchers.<Specification<Product>>any(),
                                ArgumentMatchers.eq(pageable))).thenReturn(page);

                Page<ProductResponse> result = productService.getAllProducts(
                                pageable,
                                "teclado",
                                new BigDecimal("50"),
                                new BigDecimal("100"));

                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Teclado mecánico");

                assertThat(result.getContent().get(0).getPrice())
                                .isEqualByComparingTo("79.99");

                verify(productRepository).findAll(
                                ArgumentMatchers.<Specification<Product>>any(),
                                ArgumentMatchers.eq(pageable));
        }
}