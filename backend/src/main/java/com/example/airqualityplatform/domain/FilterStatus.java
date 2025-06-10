// src/main/java/com/example/airqualityplatform/domain/FilterStatus.java
package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "filter_statuses")
public class FilterStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK → filters.filter_id */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filter_id", nullable = false)
    private Filter filter;

    /** 필터 총 용량 (시간 단위) */
    @Column(nullable = false)
    private Double capacity;

    /** 용량 단위 (예: "Hour") */
    @Column(length = 20)
    private String capacityUnit;

    /** 누적 사용 시간 (hours) */
    @Column(name = "used_hours", nullable = false)
    private Double usedHours;

    /** 사용량 증가 스텝 (hours) */
    @Column(name = "used_hours_step", nullable = false)
    private Double usedHoursStep;

    /** 필터 상태 (예: "normal"/"warning"/"expired") */
    @Column(length = 20)
    private String status;

    /** 리셋 가능 타입 (예: ["replaceable","resettable"]) */
    @ElementCollection
    @CollectionTable(
            name = "filter_status_reset_types",
            joinColumns = @JoinColumn(name = "filter_status_id")
    )
    @Column(name = "reset_type", length = 50)
    private List<String> resetTypes;

    /** 마지막 리셋 일시 (nullable) */
    private Instant lastResetDate;

    /** 외부 JSON 내 timestamp 중 가장 최신 시각 */
    @Column(nullable = false)
    private Instant statusAt;

    /** 로컬에 저장된 시각 */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant fetchedAt;

    /** 이 시점까지 누적된 “실효 사용량” (usage * weight) */
    @Column(nullable = false)
    private Double effectiveUsageAccumulated;
}
