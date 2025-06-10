package com.example.airqualityplatform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserResponseDto {
    private Long userId;
    private String email;
    private String username;
    private String phoneNumber;
    private boolean hasAsthma;
    private boolean hasAllergy;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isVerified;

    private Boolean notificationEnabled;
    private Boolean nightNotificationEnabled;
    private Boolean warningEnabled;
    private Boolean nightWarningEnabled;
}
