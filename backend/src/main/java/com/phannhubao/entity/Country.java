package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String iso;

    @Column(nullable = false)
    private String name;

    @Column(name = "upper_name", nullable = false)
    private String upperName;

    private String iso3;

    @Column(name = "num_code")
    private Integer numCode;

    @Column(name = "phone_code", nullable = false)
    private Integer phoneCode;
}
