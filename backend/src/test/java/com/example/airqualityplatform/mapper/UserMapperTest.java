package com.example.airqualityplatform.mapper;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.mapper.UserMapper;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.UserResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    @Test
    public void testToEntity() {
        // UserSignupRequest DTO 객체 생성
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setUsername("홍길동");
        signupRequest.setPhoneNumber("01012345678");
        signupRequest.setHasAsthma(true);
        signupRequest.setHasAllergy(false);
        signupRequest.setPreferredTempMin(20.0f);
        signupRequest.setPreferredTempMax(25.0f);
        signupRequest.setPreferredHumMin(40.0f);
        signupRequest.setPreferredHumMax(60.0f);

        // 변환: DTO -> User 엔티티
        User user = UserMapper.toEntity(signupRequest);

        // 변환 결과 검증
        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword()); // 실제 프로젝트에서는 해싱된 값일 수 있으므로 단순 비교는 피해야 함.
        assertEquals("홍길동", user.getUsername());
        assertEquals("01012345678", user.getPhoneNumber());
        assertTrue(user.isHasAsthma());
        assertFalse(user.isHasAllergy());
        assertEquals(20.0f, user.getPreferredTempMin());
        assertEquals(25.0f, user.getPreferredTempMax());
        assertEquals(40.0f, user.getPreferredHumMin());
        assertEquals(60.0f, user.getPreferredHumMax());
    }

    @Test
    public void testToResponseDto() {
        // User 엔티티 생성 (최소한의 필드 설정)
        User user = new User();
        user.setUserId(1L);
        user.setEmail("user@example.com");
        user.setUsername("홍길동");
        user.setPhoneNumber("01012345678");
        user.setHasAsthma(true);
        user.setHasAllergy(false);
        user.setPreferredTempMin(20.0f);
        user.setPreferredTempMax(25.0f);
        user.setPreferredHumMin(40.0f);
        user.setPreferredHumMax(60.0f);
        user.setAddress("서울특별시 강남구");
        user.setLatitude(37.5);
        user.setLongitude(127.0);
        // createdAt, updatedAt 등은 테스트를 위해 임의 설정 가능
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());

        // 변환: User 엔티티 -> UserResponseDto
        UserResponseDto responseDto = UserMapper.toResponseDto(user);

        // 변환 결과 검증
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getUserId());
        assertEquals("user@example.com", responseDto.getEmail());
        assertEquals("홍길동", responseDto.getUsername());
        assertEquals("01012345678", responseDto.getPhoneNumber());
        assertTrue(responseDto.isHasAsthma());
        assertFalse(responseDto.isHasAllergy());
        assertEquals(20.0f, responseDto.getPreferredTempMin());
        assertEquals(25.0f, responseDto.getPreferredTempMax());
        assertEquals(40.0f, responseDto.getPreferredHumMin());
        assertEquals(60.0f, responseDto.getPreferredHumMax());
        assertEquals("서울특별시 강남구", responseDto.getAddress());
        assertEquals(37.5, responseDto.getLatitude());
        assertEquals(127.0, responseDto.getLongitude());
        assertNotNull(responseDto.getCreatedAt());
        assertNotNull(responseDto.getUpdatedAt());
    }
}
