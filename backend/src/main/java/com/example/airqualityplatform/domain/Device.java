package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 100)
    private String deviceName;

    @Column(nullable = false, length = 50)
    private String deviceType;

    @Column(nullable = false)
    private Double xCoordinate;

    @Column(nullable = false)
    private Double yCoordinate;

    @Column(nullable = false)
    private Boolean power;

    @Column(length = 50)
    private String fanMode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date registeredAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updatedAt;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Filter> filters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private DeviceAutoControl policy;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        registeredAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}