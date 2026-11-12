
package com.ecommerce.api.security;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ecommerce.api.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.ecommerce.api.model.Role;
import com.ecommerce.api.model.User;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
                "jwt.secret=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
@AutoConfigureMockMvc
class ProductSecurityIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Test
        void shouldAllowOnlyProductOwnerToUpdateAndDeleteProduct() throws Exception {

                String uniqueId = UUID.randomUUID().toString();

                String emailUser1 = "usuario1-" + uniqueId + "@test.com";
                String emailUser2 = "usuario2-" + uniqueId + "@test.com";
                String password = "ClavePrueba123";

                registerUser("Usuario 1", emailUser1, password);
                registerUser("Usuario 2", emailUser2, password);

                String tokenUser1 = loginAndGetToken(emailUser1, password);
                String tokenUser2 = loginAndGetToken(emailUser2, password);

                assertNotNull(tokenUser1);
                assertNotNull(tokenUser2);

                MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                                .header("Authorization", "Bearer " + tokenUser1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "Producto de prueba",
                                                "price", new BigDecimal("25.50"),
                                                "stock", 10))))
                                .andExpect(status().isCreated())
                                .andReturn();

                JsonNode productResponse = objectMapper.readTree(
                                createResult.getResponse().getContentAsString());

                long productId = productResponse.get("id").asLong();

                mockMvc.perform(put("/api/v1/products/" + productId)
                                .header("Authorization", "Bearer " + tokenUser2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "Modificación no autorizada",
                                                "price", new BigDecimal("99.99"),
                                                "stock", 50))))
                                .andExpect(status().isNotFound());

                mockMvc.perform(delete("/api/v1/products/" + productId)
                                .header("Authorization", "Bearer " + tokenUser2))
                                .andExpect(status().isNotFound());

                mockMvc.perform(put("/api/v1/products/" + productId)
                                .header("Authorization", "Bearer " + tokenUser1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "Producto actualizado por su dueño",
                                                "price", new BigDecimal("30.00"),
                                                "stock", 20))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name")
                                                .value("Producto actualizado por su dueño"));

                mockMvc.perform(delete("/api/v1/products/" + productId)
                                .header("Authorization", "Bearer " + tokenUser1))
                                .andExpect(status().isNoContent());
        }

        @Test
        void shouldAllowAdminToUpdateAndDeleteProductsOwnedByOtherUsers()
                        throws Exception {

                String uniqueId = UUID.randomUUID().toString();

                String ownerEmail = "propietario-" + uniqueId + "@test.com";
                String adminEmail = "admin-" + uniqueId + "@test.com";
                String password = "ClavePrueba123";

                registerUser("Propietario", ownerEmail, password);
                registerUser("Administrador", adminEmail, password);

                User admin = userRepository.findByEmail(adminEmail)
                                .orElseThrow();

                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                String tokenOwner = loginAndGetToken(ownerEmail, password);
                String tokenAdmin = loginAndGetToken(adminEmail, password);

                assertNotNull(tokenOwner);
                assertNotNull(tokenAdmin);

                MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                                .header("Authorization", "Bearer " + tokenOwner)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "Producto de otro usuario",
                                                "price", new BigDecimal("25.50"),
                                                "stock", 10))))
                                .andExpect(status().isCreated())
                                .andReturn();

                JsonNode productResponse = objectMapper.readTree(
                                createResult.getResponse().getContentAsString());

                long productId = productResponse.get("id").asLong();

                mockMvc.perform(put("/api/v1/products/" + productId)
                                .header("Authorization", "Bearer " + tokenAdmin)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "Producto actualizado por ADMIN",
                                                "price", new BigDecimal("99.99"),
                                                "stock", 50))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name")
                                                .value("Producto actualizado por ADMIN"));

                mockMvc.perform(delete("/api/v1/products/" + productId)
                                .header("Authorization", "Bearer " + tokenAdmin))
                                .andExpect(status().isNoContent());
        }

        private void registerUser(String name, String email, String password)
                        throws Exception {

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", name,
                                                "email", email,
                                                "password", password))))
                                .andExpect(status().isCreated());
        }

        private String loginAndGetToken(String email, String password)
                        throws Exception {

                MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "email", email,
                                                "password", password))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").isNotEmpty())
                                .andReturn();

                JsonNode response = objectMapper.readTree(
                                result.getResponse().getContentAsString());

                return response.get("token").asText();
        }

        @Test
        void shouldAlwaysRegisterUserWithUserRoleEvenIfAdminRoleIsRequested()
                        throws Exception {

                String email = "security-" + UUID.randomUUID() + "@test.com";
                String password = "ClavePrueba123";

                mockMvc.perform(post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "name", "Usuario de prueba",
                                                "email", email,
                                                "password", password,
                                                "role", "ADMIN"))))
                                .andExpect(status().isCreated());

                User registeredUser = userRepository.findByEmail(email)
                                .orElseThrow();

                org.junit.jupiter.api.Assertions.assertEquals(
                                Role.USER,
                                registeredUser.getRole());
        }

}
