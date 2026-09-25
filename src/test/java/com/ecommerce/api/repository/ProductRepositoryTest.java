package com.ecommerce.api.repository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.ecommerce.api.model.Product;
import com.ecommerce.api.specification.ProductSpecification;

@DataJpaTest
class ProductRepositoryTest {

        @Autowired
        private ProductRepository productRepository;

        @Test
        void shouldFindProductsByNameSpecification() {

                Product keyboard = Product.builder()
                                .name("Teclado mecánico")
                                .price(new BigDecimal("79.99"))
                                .stock(10)
                                .build();

                Product mouse = Product.builder()
                                .name("Ratón inalámbrico")
                                .price(new BigDecimal("29.99"))
                                .stock(20)
                                .build();

                productRepository.saveAll(List.of(keyboard, mouse));

                Specification<Product> specification = ProductSpecification.nameContains("teclado");

                Pageable pageable = PageRequest.of(0, 10);

                Page<Product> result = productRepository.findAll(
                                specification,
                                pageable);

                assertThat(result.getContent())
                                .hasSize(1);

                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Teclado mecánico");
        }

        @Test
        void shouldFindProductsWithMinimumPrice() {

                Product cheapProduct = Product.builder()
                                .name("Ratón")
                                .price(new BigDecimal("29.99"))
                                .stock(20)
                                .build();

                Product expensiveProduct = Product.builder()
                                .name("Teclado")
                                .price(new BigDecimal("79.99"))
                                .stock(10)
                                .build();

                productRepository.saveAll(
                                List.of(cheapProduct, expensiveProduct));

                Specification<Product> specification = ProductSpecification.priceGreaterThanOrEqualTo(
                                new BigDecimal("50"));

                Page<Product> result = productRepository.findAll(
                                specification,
                                PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(1);

                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Teclado");
        }

        @Test
        void shouldFindProductsWithMaximumPrice() {

                Product cheapProduct = Product.builder()
                                .name("Ratón")
                                .price(new BigDecimal("29.99"))
                                .stock(20)
                                .build();

                Product expensiveProduct = Product.builder()
                                .name("Teclado")
                                .price(new BigDecimal("79.99"))
                                .stock(10)
                                .build();

                productRepository.saveAll(
                                List.of(cheapProduct, expensiveProduct));

                Specification<Product> specification = ProductSpecification.priceLessThanOrEqualTo(
                                new BigDecimal("50"));

                Page<Product> result = productRepository.findAll(
                                specification,
                                PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(1);

                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Ratón");
        }

        @Test
        void shouldFindProductsWithMinimumStock() {

                Product lowStockProduct = Product.builder()
                                .name("Ratón")
                                .price(new BigDecimal("29.99"))
                                .stock(5)
                                .build();

                Product highStockProduct = Product.builder()
                                .name("Teclado")
                                .price(new BigDecimal("79.99"))
                                .stock(100)
                                .build();

                productRepository.saveAll(
                                List.of(lowStockProduct, highStockProduct));

                Specification<Product> specification = ProductSpecification.stockGreaterThanOrEqualTo(10);

                Page<Product> result = productRepository.findAll(
                                specification,
                                PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(1);

                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Teclado");
        }

        @Test
        void shouldFindProductsWithMaximumStock() {

                Product lowStockProduct = Product.builder()
                                .name("Ratón")
                                .price(new BigDecimal("29.99"))
                                .stock(10)
                                .build();

                Product highStockProduct = Product.builder()
                                .name("Teclado")
                                .price(new BigDecimal("79.99"))
                                .stock(200)
                                .build();

                productRepository.saveAll(
                                List.of(lowStockProduct, highStockProduct));

                Specification<Product> specification = ProductSpecification.stockLessThanOrEqualTo(100);

                Page<Product> result = productRepository.findAll(
                                specification,
                                PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(1);

                assertThat(result.getContent().get(0).getName())
                                .isEqualTo("Ratón");
        }
}