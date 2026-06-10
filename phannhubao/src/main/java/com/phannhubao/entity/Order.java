package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 50, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_status_id")
    private OrderStatus orderStatus;

    @Column(name = "order_approved_at")
    private OffsetDateTime orderApprovedAt;

    @Column(name = "order_delivered_carrier_date")
    private OffsetDateTime orderDeliveredCarrierDate;

    @Column(name = "order_delivered_customer_date")
    private OffsetDateTime orderDeliveredCustomerDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "delivery_method")
    private String deliveryMethod;

    @Column(name = "payment_method")
    private String paymentMethod;

    private Double discount;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private StaffAccount updatedBy;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
