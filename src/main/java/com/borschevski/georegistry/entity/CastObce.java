package com.borschevski.georegistry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CastObce {
    @Id
    private int kod;
    private String nazev;
    private int kodObec;

    // Getters and Setters
}