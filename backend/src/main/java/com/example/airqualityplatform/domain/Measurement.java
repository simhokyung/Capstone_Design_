// src/main/java/com/example/airqualityplatform/domain/Measurement.java
package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long measurementId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    @Column private Double temperature;
    @Column private Double humidity;
    @Column private Double co2;
    @Column private Double voc;
    @Column(name = "pm25_b") private Double pm25_b;
    @Column(name = "pm25_m") private Double pm25_m;
    @Column(name = "pm25_t") private Double pm25_t;
    @Column(name = "pm100_b") private Double pm100_b;
    @Column(name = "pm100_m") private Double pm100_m;
    @Column(name = "pm100_t") private Double pm100_t;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = new Date();
        }
    }
}
