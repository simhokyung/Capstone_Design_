package com.example.airqualityplatform.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean hasAsthma;

    @Column(nullable = false)
    private boolean hasAllergy;

    @Column(nullable = false)
    private Boolean notificationEnabled = true;

    @Column(nullable = false)
    private Boolean nightNotificationEnabled = false;

    @Column(nullable = false)
    private Boolean warningEnabled = true;

    @Column(nullable = false)
    private Boolean nightWarningEnabled = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
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
