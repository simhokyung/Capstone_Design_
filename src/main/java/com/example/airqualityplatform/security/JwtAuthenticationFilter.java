package com.example.airqualityplatform.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if (StringUtils.hasText(token)) {
            try {
                // JWT 검증
                Claims claims = tokenProvider.validateToken(token).getBody();
                String userEmail = claims.getSubject();
                Long userId = ((Number) claims.get("userId")).longValue();
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.emptyList() // 권한은 필요에 따라 추가
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                // 토큰 검증 실패 시 SecurityContext에 아무 것도 설정하지 않음
                // 로그 기록 등 추가 가능
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
