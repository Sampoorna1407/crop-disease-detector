
// Disease.java
package com.cropcare.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "diseases")
public class Disease {

    @Id
    // ❌ REMOVE auto generation since you're inserting IDs manually
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "crop_type")
    private String cropType;

    private String description;
    private String severity;

    // ✅ Map column names properly
    @Column(name = "avg_red_value")
    private double avgRedValue;

    @Column(name = "avg_green_value")
    private double avgGreenValue;

    @Column(name = "avg_brown_value")
    private double avgBrownValue;

    // ✅ Fix symptoms table mapping
    @ElementCollection
    @CollectionTable(
        name = "symptoms",
        joinColumns = @JoinColumn(name = "disease_id")
    )
    @Column(name = "symptoms")
    private List<String> symptoms;

    // ✅ Relationship with treatments
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL)
    private List<Treatment> treatments;
}