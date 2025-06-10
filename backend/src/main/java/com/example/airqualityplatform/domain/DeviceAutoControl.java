package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_auto_controls")
public class DeviceAutoControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long controlId;  // 기본키

    // 예: PM2.5 임계치, CO2 임계치 등
    @Column
    private Double pm25Threshold;
    @Column
    private Double co2Threshold;
    @Column
    private Double pm100Threshold;
    @Column
    private Double vocThreshold;
    // 자동 제어 시 적용될 동작

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updatedAt;

    // 여러 기기(N) → 하나의 정책(1)
    // 하나의 정책이 여러 Device에 적용됨 (1:N 관계)
    @OneToMany(mappedBy = "policy")
    private List<Device> devices;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}