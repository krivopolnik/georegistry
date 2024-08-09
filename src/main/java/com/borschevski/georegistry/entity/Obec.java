package com.borschevski.georegistry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Obec {
    @Id
    private int kod;
    private String nazev;

    public int getKod() {
        return kod;
    }

    public void setKod(int kod) {
        this.kod = kod;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }
}
