
package com.ecommerce.api.service;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ecommerce.api.dto.UserCreateRequest;
import com.ecommerce.api.dto.UserResponse;
import com.ecommerce.api.exception.InvalidRequestParameterException;
import com.ecommerce.api.model.User;
import com.ecommerce.api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        private UserService userService;
        private BCryptPasswordEncoder passwordEncoder;

        @BeforeEach
        void setUp() {
                passwordEncoder = new BCryptPasswordEncoder();
                userService = new UserService(userRepository, passwordEncoder);
        }

        @Test
        void shouldCreateUserAndEncodePassword() {
                UserCreateRequest request = new UserCreateRequest();
                request.setName("Test User");
                request.setEmail("test@example.com");
                request.setPassword("myPassword123");

                when(userRepository.existsByEmail("test@example.com"))
                                .thenReturn(false);

                when(userRepository.save(any(User.class)))
                                .thenAnswer(invocation -> {
                                        User user = invocation.getArgument(0);
                                        user.setId(1L);
                                        user.setCreatedAt(LocalDateTime.now());
                                        return user;
                                });

                UserResponse response = userService.createUser(request);

                assertThat(response.getId()).isEqualTo(1L);
                assertThat(response.getName()).isEqualTo("Test User");
                assertThat(response.getEmail()).isEqualTo("test@example.com");

                verify(userRepository).save(any(User.class));
                verify(userRepository).save(
                                org.mockito.ArgumentMatchers.argThat(user -> !user.getPassword().equals("myPassword123")
                                                && passwordEncoder.matches(
                                                                "myPassword123", user.getPassword())));
        }

        @Test
        void shouldRejectDuplicateEmail() {
                UserCreateRequest request = new UserCreateRequest();
                request.setName("Test User");
                request.setEmail("existing@example.com");
                request.setPassword("myPassword123");

                when(userRepository.existsByEmail("existing@example.com"))
                                .thenReturn(true);

                assertThatThrownBy(() -> userService.createUser(request))
                                .isInstanceOf(InvalidRequestParameterException.class)
                                .hasMessage("Email is already registered");

                verify(userRepository, never()).save(any(User.class));
        }
}
