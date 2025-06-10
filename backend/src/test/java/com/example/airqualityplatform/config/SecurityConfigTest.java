package com.example.airqualityplatform.config;

import com.example.airqualityplatform.security.JwtTokenProvider;
import io.jsonwebtoken.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
public class SecurityConfigTest {

    // 테스트 환경에서 사용될 Dummy JwtTokenProvider 빈 등록
    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider() {

            // 테스트에서는 항상 유효한 토큰으로 간주하도록 오버라이드함.
            @Override
            public Jws<Claims> validateToken(String token) {
                // 실제 토큰 검증 대신, 항상 더미 데이터를 담은 Jws<Claims> 객체를 반환합니다.
                return new Jws<Claims>() {
                    @Override
                    public JwsHeader getHeader() {
                        // 간단하게 Jwts.header()로 생성한 헤더를 반환합니다.
                        return (JwsHeader) Jwts.header();
                    }

                    @Override
                    public Claims getBody() {
                        // 더미 클레임을 생성합니다.
                        Claims claims = Jwts.claims();
                        claims.put("userId", 1L);
                        claims.put("email", "dummy@example.com");
                        return claims;
                    }

                    @Override
                    public String getSignature() {
                        return "dummySignature";
                    }
                };
            }

            // 실제 createAccessToken(), createRefreshToken() 메서드는 테스트에서 호출하지 않거나,
            // 필요하면 간단히 더미 값을 반환하도록 오버라이드하면 됩니다.
        };
    }

    // 기존의 SecurityConfigTest의 나머지 SecurityFilterChain 설정
    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .httpBasic();
        return http.build();
    }
}
