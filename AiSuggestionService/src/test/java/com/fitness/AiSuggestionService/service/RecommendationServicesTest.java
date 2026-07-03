package com.fitness.AiSuggestionService.service;

import com.fitness.AiSuggestionService.model.Recommendation;
import com.fitness.AiSuggestionService.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RecommendationServicesTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationServices recommendationServices;

    @Test
    void getUserRecommendation_whenFound_returnsRecommendations() {
        Recommendation rec = Recommendation.builder()
                .id("r-1")
                .userId("user-1")
                .activityId("a-1")
                .recommendation("Keep going")
                .build();

        given(recommendationRepository.findByUserId("user-1")).willReturn(List.of(rec));

        List<Recommendation> result = recommendationServices.getUserRecommendation("user-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("r-1");
    }

    @Test
    void getActivityRecommention_whenMissing_throwsRuntimeException() {
        given(recommendationRepository.findByActivityId("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationServices.getActivityRecommention("missing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No recommendation for this Activity id");
    }
}
