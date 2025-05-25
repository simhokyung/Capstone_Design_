package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_control_segments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiControlSegment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private AiControlBatch batch;

    @Column(nullable = false)
    private Integer startMinute;

    @Column(nullable = false)
    private Integer endMinute;

    @Column(nullable = false)
    private String airPurifier;   // "on"/"off"

    @Column(nullable = false)
    private String fanMode;       // ex: "medium", "sleep"

    @Column(nullable = false)
    private Boolean ventilation;
}