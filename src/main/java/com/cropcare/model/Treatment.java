// Treatment.java
package com.cropcare.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "treatments")
public class Treatment {

    @Id
    // ❌ REMOVE this because you're inserting IDs manually in data.sql
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // ORGANIC, CHEMICAL, PREVENTIVE

    private String description;

    // ✅ Match SQL column name
    @Column(name = "application_method")
    private String applicationMethod;

    private String frequency;

    @ManyToOne
    @JoinColumn(name = "disease_id")
    private Disease disease;
}