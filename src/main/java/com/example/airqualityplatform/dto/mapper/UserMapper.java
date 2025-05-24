// src/main/java/com/example/airqualityplatform/dto/mapper/UserMapper.java
package com.example.airqualityplatform.dto.mapper;

import com.example.airqualityplatform.domain.User;
import com.example.airqualityplatform.dto.request.UserSignupRequest;
import com.example.airqualityplatform.dto.response.UserResponseDto;

public class UserMapper {

    /**
     * 회원가입 요청 DTO → User 엔티티
     */
    public static User toEntity(UserSignupRequest dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());  // Service 레이어에서 인코딩 처리
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setHasAsthma(dto.isHasAsthma());
        user.setHasAllergy(dto.isHasAllergy());
        user.setNotificationEnabled(dto.getNotificationEnabled());
        user.setNightNotificationEnabled(dto.getNightNotificationEnabled());
        user.setWarningEnabled(dto.getWarningEnabled());
        user.setNightWarningEnabled(dto.getNightWarningEnabled());
        user.setIsVerified(false);
        return user;
    }

    /**
     * User 엔티티 → 응답용 DTO
     */
    public static UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setHasAsthma(user.isHasAsthma());
        dto.setHasAllergy(user.isHasAllergy());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsVerified(user.getIsVerified());
        dto.setNotificationEnabled(user.getNotificationEnabled());
        dto.setNightNotificationEnabled(user.getNightNotificationEnabled());
        dto.setWarningEnabled(user.getWarningEnabled());
        dto.setNightWarningEnabled(user.getNightWarningEnabled());
        return dto;
    }

}
