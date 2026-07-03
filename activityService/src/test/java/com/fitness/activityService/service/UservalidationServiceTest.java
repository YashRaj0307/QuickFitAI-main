package com.fitness.activityService.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UservalidationServiceTest {

    @Mock
    private WebClient userServideWebclient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UservalidationService uservalidationService;

    @Test
    void validateUser_whenWebClientReturnsTrue_returnsTrue() {
        given(userServideWebclient.get()).willReturn(uriSpec);
        given(uriSpec.uri("api/user/{userId}/validate", "user-1")).willReturn(headersSpec);
        given(headersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(Boolean.class)).willReturn(Mono.just(true));

        boolean result = uservalidationService.validateUser("user-1");

        assertThat(result).isTrue();
    }

    @Test
    void validateUser_whenUserNotFound_throwsRuntimeException() {
        given(userServideWebclient.get()).willReturn(uriSpec);
        given(uriSpec.uri("api/user/{userId}/validate", "missing")).willReturn(headersSpec);
        given(headersSpec.retrieve()).willThrow(WebClientResponseException.create(404, "Not Found", null, null, StandardCharsets.UTF_8));

        assertThatThrownBy(() -> uservalidationService.validateUser("missing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User Not Fount");
    }

    @Test
    void validateUser_whenBadRequest_throwsRuntimeException() {
        given(userServideWebclient.get()).willReturn(uriSpec);
        given(uriSpec.uri("api/user/{userId}/validate", "bad")).willReturn(headersSpec);
        given(headersSpec.retrieve()).willThrow(WebClientResponseException.create(400, "Bad Request", null, null, StandardCharsets.UTF_8));

        assertThatThrownBy(() -> uservalidationService.validateUser("bad"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Server is returning Bad Request");
    }
}
