package com.example.airqualityplatform.dto;

import com.example.airqualityplatform.dto.request.LoginRequest;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DtoSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    /**
     * LoginRequest DTO의 직렬화 및 역직렬화 테스트.
     */
    @Test
    public void testLoginRequestSerialization() throws Exception {
        // LoginRequest 객체 생성 및 필드 설정
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // 객체를 JSON 문자열로 직렬화
        String json = objectMapper.writeValueAsString(loginRequest);
        System.out.println("LoginRequest JSON: " + json);

        // JSON 문자열을 다시 LoginRequest 객체로 역직렬화
        LoginRequest deserialized = objectMapper.readValue(json, LoginRequest.class);

        // 필드값 검증
        assertEquals("test@example.com", deserialized.getEmail());
        assertEquals("password123", deserialized.getPassword());
    }

    /**
     * UserSignupRequest DTO의 직렬화 및 역직렬화 테스트.
     */
    @Test
    public void testUserSignupRequestSerialization() throws Exception {
        // UserSignupRequest 객체 생성 및 테스트 데이터 설정
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setEmail("user@example.com");
        signupRequest.setPassword("securePass");
        signupRequest.setUsername("홍길동");
        signupRequest.setPhoneNumber("01012345678");
        signupRequest.setHasAsthma(true);
        signupRequest.setHasAllergy(false);
        signupRequest.setPreferredTempMin(20.0f);
        signupRequest.setPreferredTempMax(25.0f);
        signupRequest.setPreferredHumMin(40.0f);
        signupRequest.setPreferredHumMax(60.0f);

        // DTO -> JSON 직렬화
        String json = objectMapper.writeValueAsString(signupRequest);
        System.out.println("UserSignupRequest JSON: " + json);

        // JSON -> DTO 역직렬화
        UserSignupRequest deserialized = objectMapper.readValue(json, UserSignupRequest.class);

        // 각 필드 검증
        assertEquals("user@example.com", deserialized.getEmail());
        assertEquals("securePass", deserialized.getPassword());
        assertEquals("홍길동", deserialized.getUsername());
        assertEquals("01012345678", deserialized.getPhoneNumber());
        assertTrue(deserialized.isHasAsthma());
        assertFalse(deserialized.isHasAllergy());
        assertEquals(20.0f, deserialized.getPreferredTempMin());
        assertEquals(25.0f, deserialized.getPreferredTempMax());
        assertEquals(40.0f, deserialized.getPreferredHumMin());
        assertEquals(60.0f, deserialized.getPreferredHumMax());
    }

    /**
     * TokenResponse DTO의 직렬화 및 역직렬화 테스트.
     */
    @Test
    public void testTokenResponseSerialization() throws Exception {
        // TokenResponse 객체 생성
        TokenResponse tokenResponse = new TokenResponse("access123", "refresh123");

        // 객체를 JSON 문자열로 직렬화
        String json = objectMapper.writeValueAsString(tokenResponse);
        System.out.println("TokenResponse JSON: " + json);

        // JSON 문자열을 다시 TokenResponse 객체로 역직렬화
        TokenResponse deserialized = objectMapper.readValue(json, TokenResponse.class);

        // 필드값 검증
        assertEquals("access123", deserialized.getAccessToken());
        assertEquals("refresh123", deserialized.getRefreshToken());
    }
}
