package com.ecommerce.api.controller;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

import com.ecommerce.api.dto.ProductFilter;
import com.ecommerce.api.dto.ProductResponse;
import com.ecommerce.api.security.JwtAuthenticationFilter;
import com.ecommerce.api.service.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProductService productService;

        @MockitoBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                                any(Pageable.class),
                                any(ProductFilter.class)))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("page", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].name").value("Teclado"))
                                .andExpect(jsonPath("$.content[1].name").value("Ratón"))
                                .andExpect(jsonPath("$.page").value(0))
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
                                any(ProductFilter.class)))
                                .thenReturn(page);

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
                                                any(ProductFilter.class));

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

                ArgumentCaptor<ProductFilter> filterCaptor = ArgumentCaptor.forClass(ProductFilter.class);

                when(productService.getAllProducts(
                                any(Pageable.class),
                                any(ProductFilter.class)))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("name", "teclado")
                                                .param("page", "0")
                                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].name")
                                                .value("Teclado mecánico"));

                verify(productService)
                                .getAllProducts(
                                                any(Pageable.class),
                                                filterCaptor.capture());

                ProductFilter filter = filterCaptor.getValue();

                assertThat(filter.getName())
                                .isEqualTo("teclado");
        }

        @Test
        void shouldRejectPageSizeGreaterThanMaximum() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("size", "51"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Page size must be between 1 and 50"));
        }

        @Test
        void shouldRejectInvalidPageParameter() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("page", "hola"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldRejectInvalidSizeParameter() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("size", "hola"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldRejectNegativePage() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("page", "-1"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldRejectZeroPageSize() throws Exception {

                mockMvc.perform(get("/api/v1/products")
                                .param("size", "0"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Page size must be between 1 and 50"));

                verify(productService, never())
                                .getAllProducts(any(Pageable.class), any(ProductFilter.class));
        }

        @Test
        void shouldRejectUnsupportedSortField() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("sort", "unknown,asc"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Sorting by 'unknown' is not allowed"));
        }

        @Test
        void shouldRejectNegativeMinPrice() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("minPrice", "-10"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Validation failed"))
                                .andExpect(jsonPath("$.errors.minPrice")
                                                .value("minPrice cannot be negative"));
        }

        @Test
        void shouldRejectNegativeMaxPrice() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("maxPrice", "-10"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Validation failed"))
                                .andExpect(jsonPath("$.errors.maxPrice")
                                                .value("maxPrice cannot be negative"));
        }

        @Test
        void shouldRejectInvalidPriceRange() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("minPrice", "100")
                                                .param("maxPrice", "20"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("minPrice cannot be greater than maxPrice"));
        }

        @Test
        void shouldAcceptEqualMinAndMaxPrice() throws Exception {

                Page<ProductResponse> page = new PageImpl<>(
                                List.of(),
                                PageRequest.of(0, 10),
                                0);

                when(productService.getAllProducts(
                                any(Pageable.class),
                                any(ProductFilter.class)))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("minPrice", "50")
                                                .param("maxPrice", "50"))
                                .andExpect(status().isOk());
        }

        @Test
        void shouldRejectNegativeMinStock() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("minStock", "-10"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Validation failed"))
                                .andExpect(jsonPath("$.errors.minStock")
                                                .value("minStock cannot be negative"));
        }

        @Test
        void shouldRejectNegativeMaxStock() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("maxStock", "-10"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("Validation failed"))
                                .andExpect(jsonPath("$.errors.maxStock")
                                                .value("maxStock cannot be negative"));
        }

        @Test
        void shouldRejectInvalidStockRange() throws Exception {

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("minStock", "100")
                                                .param("maxStock", "20"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message")
                                                .value("minStock cannot be greater than maxStock"));
        }

        @Test
        void shouldAcceptEqualMinAndMaxStock() throws Exception {

                Page<ProductResponse> page = new PageImpl<>(
                                List.of(),
                                PageRequest.of(0, 10),
                                0);

                when(productService.getAllProducts(
                                any(Pageable.class),
                                any(ProductFilter.class)))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/v1/products")
                                                .param("minStock", "10")
                                                .param("maxStock", "10"))
                                .andExpect(status().isOk());
        }

        @Test
        void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {

                when(productService.getProductById(999999L))
                                .thenThrow(new com.ecommerce.api.exception.ProductNotFoundException(
                                                "Product not found"));

                mockMvc.perform(get("/api/v1/products/999999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldRejectInvalidPriceRangeWithoutCallingService() throws Exception {

                mockMvc.perform(get("/api/v1/products")
                                .param("minPrice", "100")
                                .param("maxPrice", "20"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("minPrice cannot be greater than maxPrice"));

                verify(productService, never())
                                .getAllProducts(any(Pageable.class), any(ProductFilter.class));
        }
}