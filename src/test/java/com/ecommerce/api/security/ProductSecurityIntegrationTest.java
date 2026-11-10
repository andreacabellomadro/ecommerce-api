
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void shouldAllowOnlyProductOwnerToUpdateAndDeleteProduct() throws Exception {

        // 1. Creamos dos usuarios con correos únicos
        String uniqueId = UUID.randomUUID().toString();

        String emailUser1 = "usuario1-" + uniqueId + "@test.com";
        String emailUser2 = "usuario2-" + uniqueId + "@test.com";
        String password = "ClavePrueba123";

        registerUser("Usuario 1", emailUser1, password);
        registerUser("Usuario 2", emailUser2, password);

        // 2. Iniciamos sesión y obtenemos los JWT
        String tokenUser1 = loginAndGetToken(emailUser1, password);
        String tokenUser2 = loginAndGetToken(emailUser2, password);

        assertNotNull(tokenUser1);
        assertNotNull(tokenUser2);

        // 3. El usuario 1 crea un producto
        MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + tokenUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "name", "Producto de prueba",
                        "price", new BigDecimal("25.50"),
                        "stock", 10
                ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode productResponse = objectMapper.readTree(
                createResult.getResponse().getContentAsString());

        long productId = productResponse.get("id").asLong();

        // 4. El usuario 2 intenta modificar el producto
        mockMvc.perform(put("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + tokenUser2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "name", "Modificación no autorizada",
                        "price", new BigDecimal("99.99"),
                        "stock", 50
                ))))
                .andExpect(status().isNotFound());

        // 5. El usuario 2 intenta eliminar el producto
        mockMvc.perform(delete("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + tokenUser2))
                .andExpect(status().isNotFound());

        // 6. El usuario 1 sí puede modificar su propio producto
        mockMvc.perform(put("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + tokenUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "name", "Producto actualizado por su dueño",
                        "price", new BigDecimal("30.00"),
                        "stock", 20
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Producto actualizado por su dueño"));

        // 7. El usuario 1 sí puede eliminar su propio producto
        mockMvc.perform(delete("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isNoContent());
    }

    private void registerUser(String name, String email, String password)
            throws Exception {

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "name", name,
                        "email", email,
                        "password", password
                ))))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String email, String password)
            throws Exception {

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString());

        return response.get("token").asText();
    }
}
