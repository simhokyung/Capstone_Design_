package com.example.airqualityplatform.config;

import com.example.airqualityplatform.security.JwtAuthenticationFilter;
import com.example.airqualityplatform.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 경로 모두 허용
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 회원가입, 로그인 등 Public API
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/measurements/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/measurements/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/sensors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sensors/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/sensors/**").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/homes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/homes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()



                        .requestMatchers(HttpMethod.POST, "/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/homes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/homes/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/homes/**").permitAll()

                        .requestMatchers(HttpMethod.DELETE, "/homes/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/devices/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/users/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/filters/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/filters/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/policies/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/policies/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/policies/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/policies/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/ai/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ai/**").permitAll()


                        // AI 예측 API
                        .requestMatchers(HttpMethod.GET, "/ai/predictions").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
