package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "slideshows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slideshow {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 80)
    private String title;

    @Column(name = "destination_url", columnDefinition = "TEXT")
    private String destinationUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String placeholder;

    @Column(length = 160)
    private String description;

    @Column(name = "btn_label", length = 50)
    private String btnLabel;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Builder.Default
    private Boolean published = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer clicks = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    private String styles;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private com.phannhubao.entity.StaffAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private com.phannhubao.entity.StaffAccount updatedBy;
}
