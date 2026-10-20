package com.ecommerce.api.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.ecommerce.api.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("hashed-password");

        userRepository.saveAndFlush(user);

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test User");
        assertThat(result.get().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckIfEmailExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("existing@example.com");
        user.setPassword("hashed-password");

        userRepository.saveAndFlush(user);

        assertThat(userRepository.existsByEmail("existing@example.com"))
                .isTrue();

        assertThat(userRepository.existsByEmail("missing@example.com"))
                .isFalse();
    }

}
