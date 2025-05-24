// src/main/java/com/example/airqualityplatform/dto/request/LoginRequest.java
package com.example.airqualityplatform.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;
}
