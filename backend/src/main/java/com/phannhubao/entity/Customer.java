package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String email;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Builder.Default
    private Boolean active = true;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    @Column(name = "sales_notification")
    @Builder.Default
    private Boolean salesNotification = true;

    @Column(name = "new_arrivals_notification")
    @Builder.Default
    private Boolean newArrivalsNotification = true;

    @Column(name = "delivery_status_notification")
    @Builder.Default
    private Boolean deliveryStatusNotification = true;

    @Column(name = "registered_at", nullable = false)
    private java.time.OffsetDateTime registeredAt;

    @Column(name = "updated_at", nullable = false)
    private java.time.OffsetDateTime updatedAt;
}
