// src/main/java/com/example/airqualityplatform/domain/AiPredictionBatch.java
package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_prediction_batches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiPredictionBatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    /** AI가 예측한 시각 (UTC) */
    @Column(nullable = false)
    private Instant timestamp;

    @Builder.Default
    @OneToMany(
            mappedBy = "batch",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SensorPrediction> predictions = new ArrayList<>();
}
