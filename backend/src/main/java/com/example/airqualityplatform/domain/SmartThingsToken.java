package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "smartthings_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SmartThingsToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 24시간마다 갱신되는 Bearer 토큰 */
    @Column(nullable = false, length = 128)
    private String token;

    /** 토큰을 받은 시각 */
    @Column(nullable = false)
    private Instant receivedAt;
}
