package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.domain.UserPreference;
import com.example.airqualityplatform.dto.mapper.UserPreferenceMapper;
import com.example.airqualityplatform.dto.request.UserPreferenceRequestDto;
import com.example.airqualityplatform.dto.response.UserPreferenceResponseDto;
import com.example.airqualityplatform.repository.UserPreferenceRepository;
import com.example.airqualityplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserPreferenceServiceTest {

    @InjectMocks
    private UserPreferenceService preferenceService;

    @Mock
    private UserPreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrUpdatePreference() {
        Long userId = 1L;
        UserPreferenceRequestDto req = new UserPreferenceRequestDto();
        req.setPm25Threshold(35.0);
        req.setPm10Threshold(50.0);
        req.setCo2Threshold(800.0);
        req.setVocThreshold(0.12);

        // 사용자 조회
        User user = new User();
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Preference가 없을 경우, 새로운 Preference 생성
        when(preferenceRepository.findByUser_UserId(userId)).thenReturn(Optional.empty());

        UserPreference preference = UserPreferenceMapper.toEntity(req, null);
        preference.setUser(user);
        preference.setPreferenceId(100L);
        Date now = new Date();
        preference.setCreatedAt(now);
        preference.setUpdatedAt(now);

        when(preferenceRepository.save(any(UserPreference.class))).thenReturn(preference);

        UserPreferenceResponseDto response = preferenceService.createOrUpdatePreference(userId, req);
        assertNotNull(response);
        assertEquals(100L, response.getPreferenceId());
        assertEquals(userId, response.getUserId());
        assertEquals(35.0, response.getPm25Threshold());
        verify(userRepository, times(1)).findById(userId);
        verify(preferenceRepository, times(1)).save(any(UserPreference.class));
    }

    @Test
    public void testGetPreferenceByUserId() {
        Long userId = 1L;
        UserPreference pref = new UserPreference();
        pref.setPreferenceId(100L);
        User user = new User();
        user.setUserId(userId);
        pref.setUser(user);
        pref.setPm25Threshold(35.0);
        pref.setPm10Threshold(50.0);
        pref.setCo2Threshold(800.0);
        pref.setVocThreshold(0.12);
        Date now = new Date();
        pref.setCreatedAt(now);
        pref.setUpdatedAt(now);

        when(preferenceRepository.findByUser_UserId(userId)).thenReturn(Optional.of(pref));

        Optional<UserPreferenceResponseDto> result = preferenceService.getPreferenceByUserId(userId);
        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getPreferenceId());
        verify(preferenceRepository, times(1)).findByUser_UserId(userId);
    }

    // 설문을 통한 Preference 생성 테스트는 기존 방식(엔티티 반환)으로 처리되어 있으므로 여기서는 생략하거나 별도 작성 가능합니다.
}
