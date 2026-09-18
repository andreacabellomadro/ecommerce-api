package com.ecommerce.api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

                when(productService.getAllProducts())
                                .thenReturn(List.of(product1, product2));

                mockMvc.perform(
                                get("/api/v1/products"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].name").value("Teclado"))
                                .andExpect(jsonPath("$[1].name").value("Ratón"));
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
}