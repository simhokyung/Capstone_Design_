package com.example.airqualityplatform.service;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.UserResponseDto;
import com.example.airqualityplatform.exception.DuplicateResourceException;
import com.example.airqualityplatform.exception.InvalidCredentialsException;
import com.example.airqualityplatform.exception.ResourceNotFoundException;
import com.example.airqualityplatform.repository.UserRepository;
import com.example.airqualityplatform.dto.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입 처리
     */
    public UserResponseDto registerUser(UserSignupRequest dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("이미 가입된 이메일입니다.");
        }
        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // isVerified 기본값 false로 설정됨
        User saved = userRepo.save(user);
        return UserMapper.toResponseDto(saved);
    }

    /**
     * 인증 처리
     */
    public User authenticate(String email, String rawPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("등록된 회원이 아닙니다."));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
        return user;
    }

    /**
     * 사용자 조회
     */
    public Optional<UserResponseDto> getById(Long userId) {
        return userRepo.findById(userId)
                .map(UserMapper::toResponseDto);
    }
}
