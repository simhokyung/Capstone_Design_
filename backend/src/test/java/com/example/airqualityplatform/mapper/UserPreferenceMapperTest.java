package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.domain.UserPreference;
import com.example.airqualityplatform.dto.mapper.UserPreferenceMapper;
import com.example.airqualityplatform.dto.request.UserPreferenceRequestDto;
import com.example.airqualityplatform.dto.response.UserPreferenceResponseDto;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class UserPreferenceMapperTest {

    @Test
    public void testToEntity() {
        // UserPreferenceRequestDto 생성 및 필드 설정
        UserPreferenceRequestDto req = new UserPreferenceRequestDto();
        req.setPm25Threshold(35.0);
        req.setPm10Threshold(50.0);
        req.setCo2Threshold(800.0);
        req.setVocThreshold(0.12);

        // DTO -> UserPreference 엔티티 변환 (업데이트 대상은 null)
        UserPreference pref = UserPreferenceMapper.toEntity(req, null);
        assertNotNull(pref);
        assertEquals(35.0, pref.getPm25Threshold());
        assertEquals(50.0, pref.getPm10Threshold());
        assertEquals(800.0, pref.getCo2Threshold());
        assertEquals(0.12, pref.getVocThreshold());
    }

    @Test
    public void testToResponseDto() {
        // UserPreference 엔티티 생성 및 User 설정
        UserPreference pref = new UserPreference();
        pref.setPreferenceId(100L);
        User user = new User();
        user.setUserId(10L);
        pref.setUser(user);
        pref.setPm25Threshold(35.0);
        pref.setPm10Threshold(50.0);
        pref.setCo2Threshold(800.0);
        pref.setVocThreshold(0.12);
        Date now = new Date();
        pref.setCreatedAt(now);
        pref.setUpdatedAt(now);

        UserPreferenceResponseDto dto = UserPreferenceMapper.toResponseDto(pref);
        assertNotNull(dto);
        assertEquals(100L, dto.getPreferenceId());
        assertEquals(10L, dto.getUserId());
        assertEquals(35.0, dto.getPm25Threshold());
        assertEquals(50.0, dto.getPm10Threshold());
        assertEquals(800.0, dto.getCo2Threshold());
        assertEquals(0.12, dto.getVocThreshold());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
