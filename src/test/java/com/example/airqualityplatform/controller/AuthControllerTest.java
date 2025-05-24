package com.example.airqualityplatform.controller;

import com.example.airqualityplatform.config.SecurityConfigTest;
import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.request.LoginRequest;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.TokenResponse;
import com.example.airqualityplatform.dto.response.UserResponseDto;
import com.example.airqualityplatform.security.JwtTokenProvider;
import com.example.airqualityplatform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfigTest.class) // 테스트용 Security 설정
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원가입 테스트")
    public void testSignup() throws Exception {
        // 입력용 회원가입 요청 DTO 생성
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setUsername("NewUser");
        signupRequest.setPhoneNumber("01011112222");
        signupRequest.setHasAsthma(false);
        signupRequest.setHasAllergy(false);
        signupRequest.setPreferredTempMin(20.0f);
        signupRequest.setPreferredTempMax(25.0f);
        signupRequest.setPreferredHumMin(40.0f);
        signupRequest.setPreferredHumMax(60.0f);

        // 모킹: 회원가입 후 반환될 UserResponseDto 생성
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setUserId(1L);
        userResponse.setEmail("newuser@example.com");
        userResponse.setUsername("NewUser");
        // 나머지 필드도 필요에 따라 설정

        when(userService.registerUser(any(UserSignupRequest.class))).thenReturn(userResponse);

        // 요청 수행 및 검증
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.username").value("NewUser"));
    }

    @Test
    @DisplayName("로그인 테스트")
    public void testLogin() throws Exception {
        // 로그인 요청용 DTO 생성
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("plaintextPass");

        // 모킹: 실제 로그인 시 반환될 User 객체를 생성하여 모킹
        User dummyUser = new User();
        dummyUser.setUserId(1L);
        dummyUser.setEmail("login@example.com");
        // 필요한 추가 필드 설정 가능

        when(userService.login(eq("login@example.com"), eq("plaintextPass")))
                .thenReturn(dummyUser);

        // 모킹: JWT 토큰 생성
        when(jwtTokenProvider.createAccessToken(eq("login@example.com"), eq(1L)))
                .thenReturn("access123");
        when(jwtTokenProvider.createRefreshToken(eq("login@example.com"), eq(1L)))
                .thenReturn("refresh123");

        // 로그인 요청 후 토큰 응답 검증
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh123"));
    }

    @Test
    @DisplayName("리프레시 토큰 테스트")
    public void testRefreshToken() throws Exception {
        String refreshToken = "refresh123";

        // 모킹: validateToken() 메서드가 유효한 Jws<Claims> 객체를 반환하도록 설정
        @SuppressWarnings("unchecked")
        Jws<Claims> mockJws = Mockito.mock(Jws.class);
        Claims claims = io.jsonwebtoken.Jwts.claims();
        claims.put("userId", 1L);
        claims.setSubject("login@example.com");
        when(mockJws.getBody()).thenReturn(claims);

        when(jwtTokenProvider.validateToken(eq(refreshToken))).thenReturn(mockJws);
        when(jwtTokenProvider.createAccessToken(eq("login@example.com"), eq(1L)))
                .thenReturn("newAccessToken");

        // 리프레시 토큰 엔드포인트 호출 후 응답 검증
        mockMvc.perform(post("/auth/refresh")
                        .param("refreshToken", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }
}
