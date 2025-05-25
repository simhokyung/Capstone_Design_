package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_control_batches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiControlBatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 100)
    private String deviceId;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AiControlSegment> segments = new ArrayList<>();

    public void addSegment(AiControlSegment seg) {
        seg.setBatch(this);
        this.segments.add(seg);
    }
}