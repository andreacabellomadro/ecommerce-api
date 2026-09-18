package com.ecommerce.api.controller;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProductService productService;

        @Test
        void shouldGetProductById() throws Exception {

                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Teclado")
                                .price(new BigDecimal("49.99"))
                                .stock(10)
                                .build();

                when(productService.getProductById(1L))
                                .thenReturn(response);

                mockMvc.perform(
                                get("/api/v1/products/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Teclado"))
                                .andExpect(jsonPath("$.price").value(49.99))
                                .andExpect(jsonPath("$.stock").value(10));
        }

        @Test
        void shouldGetAllProducts() throws Exception {

                ProductResponse product1 = ProductResponse.builder()
                                .id(1L)
                                .name("Teclado")
                                .price(new BigDecimal("49.99"))
                                .stock(10)
                                .build();

                ProductResponse product2 = ProductResponse.builder()
                                .id(2L)
                                .name("Ratón")
                                .price(new BigDecimal("29.99"))
                                .stock(20)
                                .build();

                Page<ProductResponse> page = new PageImpl<>(List.of(product1, product2),
                                PageRequest.of(0, 10),
                                2);

                when(productService.getAllProducts(
                                org.mockito.ArgumentMatchers.any(Pageable.class),
                                org.mockito.ArgumentMatchers.isNull()))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("page", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].name").value("Teclado"))
                                .andExpect(jsonPath("$.content[1].name").value("Ratón"))
                                .andExpect(jsonPath("$.number").value(0))
                                .andExpect(jsonPath("$.size").value(10))
                                .andExpect(jsonPath("$.totalElements").value(2))
                                .andExpect(jsonPath("$.totalPages").value(1));
        }

        @Test
        void shouldCreateProduct() throws Exception {

                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Teclado")
                                .price(new BigDecimal("49.99"))
                                .stock(10)
                                .build();

                when(productService.createProduct(org.mockito.ArgumentMatchers.any()))
                                .thenReturn(response);

                String requestJson = """
                                {
                                    "name": "Teclado",
                                    "price": 49.99,
                                    "stock": 10
                                }
                                """;

                mockMvc.perform(
                                post("/api/v1/products")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Teclado"))
                                .andExpect(jsonPath("$.price").value(49.99))
                                .andExpect(jsonPath("$.stock").value(10));
        }

        @Test
        void shouldReturnBadRequestWhenProductIsInvalid() throws Exception {

                String requestJson = """
                                {
                                    "name": "",
                                    "price": -50,
                                    "stock": -10
                                }
                                """;

                mockMvc.perform(
                                post("/api/v1/products")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isBadRequest());

                verify(productService, never())
                                .createProduct(org.mockito.ArgumentMatchers.any());
        }

        @Test
        void shouldUpdateProduct() throws Exception {

                ProductResponse response = ProductResponse.builder()
                                .id(1L)
                                .name("Teclado mecánico")
                                .price(new BigDecimal("79.99"))
                                .stock(15)
                                .build();

                when(productService.updateProduct(
                                org.mockito.ArgumentMatchers.eq(1L),
                                org.mockito.ArgumentMatchers.any())).thenReturn(response);

                String requestJson = """
                                {
                                    "name": "Teclado mecánico",
                                    "price": 79.99,
                                    "stock": 15
                                }
                                """;

                mockMvc.perform(
                                put("/api/v1/products/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Teclado mecánico"))
                                .andExpect(jsonPath("$.price").value(79.99))
                                .andExpect(jsonPath("$.stock").value(15));
        }

        @Test
        void shouldDeleteProduct() throws Exception {

                mockMvc.perform(
                                delete("/api/v1/products/1"))
                                .andExpect(status().isOk());

                verify(productService)
                                .deleteProduct(1L);
        }

        @Test
        void shouldReturnBadRequestWhenUpdatingInvalidProduct() throws Exception {

                String requestJson = """
                                {
                                    "name": "",
                                    "price": -50,
                                    "stock": -10
                                }
                                """;

                mockMvc.perform(
                                put("/api/v1/products/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(requestJson))
                                .andExpect(status().isBadRequest());

                verify(productService, never())
                                .updateProduct(
                                                org.mockito.ArgumentMatchers.eq(1L),
                                                org.mockito.ArgumentMatchers.any());
        }

        @Test
        void shouldSortProductsByPriceDescending() throws Exception {

                Page<ProductResponse> page = new PageImpl<>(
                                List.of(),
                                PageRequest.of(0, 10),
                                0);

                when(productService.getAllProducts(
                                any(Pageable.class),
                                isNull())).thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("page", "0")
                                                .param("size", "10")
                                                .param("sort", "price,desc"))
                                .andExpect(status().isOk());

                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

                verify(productService)
                                .getAllProducts(
                                                pageableCaptor.capture(),
                                                isNull());

                Pageable pageable = pageableCaptor.getValue();

                assertThat(pageable.getPageNumber()).isEqualTo(0);
                assertThat(pageable.getPageSize()).isEqualTo(10);
                assertThat(pageable.getSort().getOrderFor("price").isDescending())
                                .isTrue();
        }

        @Test
        void shouldFilterProductsByName() throws Exception {

                Page<ProductResponse> page = new PageImpl<>(
                                List.of(
                                                ProductResponse.builder()
                                                                .id(1L)
                                                                .name("Teclado mecánico")
                                                                .price(new BigDecimal("79.99"))
                                                                .stock(10)
                                                                .build()),
                                PageRequest.of(0, 10),
                                1);

                when(productService.getAllProducts(
                                org.mockito.ArgumentMatchers.any(Pageable.class),
                                org.mockito.ArgumentMatchers.eq("teclado"))).thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("name", "teclado")
                                                .param("page", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].name")
                                                .value("Teclado mecánico"));
        }
}