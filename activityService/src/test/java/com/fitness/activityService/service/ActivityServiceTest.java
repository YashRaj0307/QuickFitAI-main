package com.fitness.activityService.service;

import com.fitness.activityService.ActivityRepository;
import com.fitness.activityService.dto.ActivityRequest;
import com.fitness.activityService.dto.ActivityResponse;
import com.fitness.activityService.model.Activity;
import com.fitness.activityService.model.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UservalidationService uservalidationService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(activityService, "exchange", "test.exchange");
        ReflectionTestUtils.setField(activityService, "routingKey", "test.routing.key");
    }

    @Test
    void trackActivity_whenValidUser_savesAndPublishesAndReturnsResponse() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("user-123");
        request.setType(ActivityType.RUNNING);
        request.setDuration(30);
        request.setCaloriesBurned(300);
        request.setStartTime(LocalDateTime.of(2026, 3, 25, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 3, 25, 10, 30));

        Activity savedActivity = Activity.builder()
                .id("act-1")
                .userId("user-123")
                .type(ActivityType.RUNNING)
                .duration(30)
                .caloriesBurned(300)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .additionalMetrics(Collections.singletonMap("pace", "5:00"))
                .build();

        given(uservalidationService.validateUser("user-123")).willReturn(true);
        given(activityRepository.save(any(Activity.class))).willReturn(savedActivity);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Activity.class));

        ActivityResponse response = activityService.trackActivity(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("act-1");
        assertThat(response.getUserId()).isEqualTo("user-123");
        assertThat(response.getType()).isEqualTo(ActivityType.RUNNING);
        assertThat(response.getDuration()).isEqualTo(30);
        assertThat(response.getCaloriesBurned()).isEqualTo(300);

        verify(activityRepository, times(1)).save(any(Activity.class));
        verify(rabbitTemplate, times(1)).convertAndSend("test.exchange", "test.routing.key", savedActivity);
    }

    @Test
    void trackActivity_whenInvalidUser_throwsRuntimeException() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("bad-user");

        given(uservalidationService.validateUser("bad-user")).willReturn(false);

        assertThatThrownBy(() -> activityService.trackActivity(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid User");

        verify(activityRepository, times(0)).save(any(Activity.class));
        verify(rabbitTemplate, times(0)).convertAndSend(anyString(), anyString(), any());
    }

    @Test
    void getActivities_whenUserExists_returnsMappedResponses() {
        Activity activity = Activity.builder()
                .id("act-1")
                .userId("user-123")
                .type(ActivityType.CYCLING)
                .duration(45)
                .caloriesBurned(450)
                .build();

        given(activityRepository.findByUserId("user-123")).willReturn(List.of(activity));

        List<ActivityResponse> responses = activityService.getActivities("user-123");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo("act-1");
        assertThat(responses.get(0).getType()).isEqualTo(ActivityType.CYCLING);
    }

    @Test
    void getActivityById_whenNotFound_throwsRuntimeException() {
        given(activityRepository.findById("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.getActivityById("missing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No Such Activity Found");
    }

    @Test
    void getActivityById_whenFound_returnsResponse() {
        Activity activity = Activity.builder()
                .id("act-2")
                .userId("user-123")
                .type(ActivityType.SWIMMING)
                .duration(60)
                .caloriesBurned(650)
                .build();

        given(activityRepository.findById("act-2")).willReturn(Optional.of(activity));

        ActivityResponse response = activityService.getActivityById("act-2");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("act-2");
        assertThat(response.getUserId()).isEqualTo("user-123");
    }
}
