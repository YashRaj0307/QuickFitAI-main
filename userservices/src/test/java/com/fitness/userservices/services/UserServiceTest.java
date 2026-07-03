package com.fitness.userservices.services;

import com.fitness.userservices.dto.RegisterRequest;
import com.fitness.userservices.dto.UserResponse;
import com.fitness.userservices.model.User;
import com.fitness.userservices.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    @Test
    void register_givenExistingEmail_returnsExistingUserProfile() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("secret123");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setKeycloakId("kc-123");

        User existing = new User();
        existing.setId("u-1");
        existing.setEmail("existing@example.com");
        existing.setPassword("secret123");
        existing.setKeycloakId("kc-123");
        existing.setFirstName("Jane");
        existing.setLastName("Doe");
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        given(repository.existsByEmail("existing@example.com")).willReturn(true);
        given(repository.findByEmail("existing@example.com")).willReturn(existing);

        UserResponse response = userService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("u-1");
        assertThat(response.getEmail()).isEqualTo("existing@example.com");
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void register_givenNewUser_savesAndReturnsUserProfile() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("winner123");
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setKeycloakId("kc-456");

        User newUser = new User();
        newUser.setId("u-2");
        newUser.setEmail("new@example.com");
        newUser.setPassword("winner123");
        newUser.setKeycloakId("kc-456");
        newUser.setFirstName("John");
        newUser.setLastName("Smith");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        given(repository.existsByEmail("new@example.com")).willReturn(false);
        given(repository.save(any(User.class))).willReturn(newUser);

        UserResponse response = userService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("u-2");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getKeycloakId()).isEqualTo("kc-456");
    }

    @Test
    void getUserProfile_whenNotFound_throwsException() {
        given(repository.findById("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("missing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User Not Found");
    }

    @Test
    void existByUserId_delegatesToRepository() {
        given(repository.existsByKeycloakId("kc-789")).willReturn(true);

        Boolean exists = userService.existByUserId("kc-789");

        assertThat(exists).isTrue();
    }
}
