
package com.ecommerce.api.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.dto.UserCreateRequest;
import com.ecommerce.api.dto.UserResponse;
import com.ecommerce.api.security.JwtAuthenticationFilter;
import com.ecommerce.api.service.UserService;
import com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        private final JsonMapper objectMapper = JsonMapper.builder().build();

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Test
        void shouldCreateUser() throws Exception {
                UserCreateRequest request = new UserCreateRequest();
                request.setName("Test User");
                request.setEmail("test@example.com");
                request.setPassword("myPassword123");

                when(userService.createUser(any(UserCreateRequest.class)))
                                .thenReturn(new UserResponse(
                                                1L,
                                                "Test User",
                                                "test@example.com",
                                                LocalDateTime.of(2026, 1, 1, 12, 0)));

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Test User"))
                                .andExpect(jsonPath("$.email").value("test@example.com"))
                                .andExpect(jsonPath("$.password").doesNotExist());

                verify(userService).createUser(any(UserCreateRequest.class));
        }

        @Test
        void shouldRejectInvalidUserRequest() throws Exception {
                UserCreateRequest request = new UserCreateRequest();
                request.setName("");
                request.setEmail("invalid-email");
                request.setPassword("short");

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());

                verify(userService, never())
                                .createUser(any(UserCreateRequest.class));
        }
}
