package com.borschevski.georegistry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Obec {
    @Id
    private int kod;
    private String nazev;

    // Getters and Setters
}