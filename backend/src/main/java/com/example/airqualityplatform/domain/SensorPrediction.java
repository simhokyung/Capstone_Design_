// src/main/java/com/example/airqualityplatform/domain/SensorPrediction.java
package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "sensor_predictions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SensorPrediction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어느 배치(시간)의 예측인지 FK */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private AiPredictionBatch batch;

    private Long sensorId;
    private String deviceId;

    @ElementCollection
    @CollectionTable(name = "sensor_prediction_voc", joinColumns = @JoinColumn(name = "sensor_prediction_id"))
    @Column(name = "voc")
    private List<Double> voc;

    @ElementCollection
    @CollectionTable(name = "sensor_prediction_temperature", joinColumns = @JoinColumn(name = "sensor_prediction_id"))
    @Column(name = "temperature")
    private List<Double> temperature;

    @ElementCollection
    @CollectionTable(name = "sensor_prediction_humidity", joinColumns = @JoinColumn(name = "sensor_prediction_id"))
    @Column(name = "humidity")
    private List<Double> humidity;

    @ElementCollection
    @CollectionTable(name = "sensor_prediction_co2", joinColumns = @JoinColumn(name = "sensor_prediction_id"))
    @Column(name = "co2")
    private List<Double> co2;

    @ElementCollection
    @CollectionTable(name = "sensor_prediction_pm25", joinColumns = @JoinColumn(name = "sensor_prediction_id"))
    @Column(name = "pm25_t")
    private List<Double> pm25_t;

    @ElementCollection
    @CollectionTable(name = "sensor_prediction_pm100", joinColumns = @JoinColumn(name = "sensor_prediction_id"))
    @Column(name = "pm100_t")
    private List<Double> pm100_t;
}
