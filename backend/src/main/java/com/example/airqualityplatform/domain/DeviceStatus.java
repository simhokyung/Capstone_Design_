package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "device_states")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK → devices.id */
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    /** 지난 기간 에너지 사용량 */
    @Column
    private Long energy;

    /** 전원 상태 (“on”/“off”) */
    @Column(length = 16)
    private String powerState;

    /** 팬 모드 (“low”/“medium”/“high”) */
    @Column(length = 16)
    private String fanMode;

    /** 로컬에 저장 시각 */
    @Column(nullable = false)
    private Instant fetchedAt;

    /** SmartThings가 찍어준 상태 시각 */
    @Column(nullable = false)
    private Instant statusAt;
}
