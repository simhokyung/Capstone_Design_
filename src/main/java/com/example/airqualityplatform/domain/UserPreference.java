package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "user_preferences")
public class UserPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Double pm25Threshold;

    @Column(nullable = false)
    private Double pm100Threshold;

    @Column(nullable = false)
    private Double co2Threshold;

    @Column(nullable = false)
    private Double vocThreshold;

    @Temporal(TemporalType.TIMESTAMP) @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP) @Column(nullable = false)
    private Date updatedAt;

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
