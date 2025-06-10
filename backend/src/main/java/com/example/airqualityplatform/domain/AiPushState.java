package com.example.airqualityplatform.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "ai_push_state")
@Getter
@Setter
@NoArgsConstructor
public class AiPushState {
    @Id
    private Long id = 1L;

    private Date lastMeasurementTime;
    private Instant lastStatusTime;
}