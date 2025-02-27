package com.SmartEvCharger.SmartEvCharger.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data                   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // No-args constructor
@AllArgsConstructor     // All-args constructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double latitude;
    private Double longitude;
    private String provider;
}
