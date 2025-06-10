package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "비밀번호는 대문자, 소문자, 숫자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank
    @Size(max = 100)
    private String username;

    @Pattern(regexp = "^01[016789]\\d{3,4}\\d{4}$",
            message = "전화번호 형식이 잘못되었습니다.")
    private String phoneNumber;

    @NotNull
    private boolean hasAsthma;

    @NotNull
    private boolean hasAllergy;

    @NotNull
    private Boolean notificationEnabled;

    @NotNull
    private Boolean nightNotificationEnabled;

    @NotNull
    private Boolean warningEnabled;

    @NotNull
    private Boolean nightWarningEnabled;
}